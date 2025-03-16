<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import MasonryGrid from "@/components/MasonryGrid.vue";
import PostChat from "@/components/PostChat.vue";
import 'vue3-toastify/dist/index.css';
import {toast} from "vue3-toastify";

interface Post {
  id: string;
  content: string;
  postImages: Array<PostImage>;
  createDate: string;
  uid: number;
  writer: string;
}

interface PostImage {
  id: string | null;
  url: string;
}

interface Image {
  id: string | null;
  filename: string | null;
  url: string;
  source: "gallery" | "upload";
}

// 이미지 제공자 상태
const providers = reactive<string[]>([]);
const selectedProvider = reactive({ value: "Nyangmunity" });

// 게시물 및 페이지네이션 상태
const posts = reactive<Post[]>([]);
const postPage = reactive({ value: 1 });
const postTotalPage = reactive({ value: 0 });
const postContainerRef = ref<HTMLElement | null>(null);

// 게시물 작성
const content = ref<string>("");

// 이미지 및 페이지네이션 상태
const images = reactive<Image[]>([]);
const imagePage = reactive({ value: 0 });
const imageTotalPage = reactive({ value: 0 });

// 업로드 이미지
const uploadImageList = reactive<Image[]>([]);
const selectedPreviewImage = ref<string | null>(null);
const uploadImage = ref<string | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);
const MAX_UPLOAD_IMAGES = 5;

// 게시물 가져오기
const getPosts = async (page: number, init: boolean) => {
  if (postTotalPage.value !== 0 && page >= postTotalPage.value) return;

  try {
    const response = await axios.get(`/nm/post?page=${page - 1}&size=10`);
    postTotalPage.value = response.data.totalPages;

    if (init) {
      posts.splice(0, posts.length, ...response.data.content);
      await nextTick();
      postContainerRef.value?.scrollTo(0, postContainerRef.value.scrollHeight); // 초기 로드 시 맨 아래로 스크롤
    } else {
      const prevScrollHeight = postContainerRef.value?.scrollHeight || 0;
      posts.push(...response.data.content);
      await nextTick();
      const currentScrollHeight = postContainerRef.value?.scrollHeight || 0;
      postContainerRef.value?.scrollTo(0, currentScrollHeight - prevScrollHeight);
    }

    console.log(posts);
  } catch (error) {
    console.error(error);
  }
};

// 게시물 업로드
const writePost = async () => {
  const uploadedImageIds: string[] = await uploadImages();
  axios.post('/nm/post', {
    content: content.value,
    postImageIds: uploadedImageIds,
  }).then(() => {
    uploadImageList.splice(0, uploadImageList.length);  // 업로드 후 초기화
    content.value = "";  // 내용 초기화
    getPosts(postPage.value, true); // 최신 글 불러오기
  });
};

// 이미지 업로드
const uploadImages = async () => {
  const uploadedImageIds: string[] = [];

  for (const image of uploadImageList) {
    if (image.source === "gallery") { // 선택된 이미지
      uploadedImageIds.push(image.id as string);
    } else if (image.source === "upload") { // 업로드 될 이미지
      // 직접 업로드한 이미지 -> presigned URL 요청 후 업로드
      const response = await axios.get(`/nm/image/upload?${image.filename}`);

      const { id, uploadUrl, filePath } = response.data;

      await axios.put(uploadUrl + filePath, dataUrlToBlob(image.url));

      // 업로드 완료 후 서버에 저장된 이미지 ID 수집
      uploadedImageIds.push(id);
    }
  }

  return uploadedImageIds;
};

const dataUrlToBlob = (dataUrl: string) => {
  const arr = dataUrl.split(',');
  const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/png';
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);

  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }

  return new Blob([u8arr], { type: mime });
};


// 스크롤 이벤트 핸들러 (위로 스크롤시 이전 페이지 로드)
const handlePostScroll = (event: Event) => {
  const element = event.target as HTMLElement;
  if (element.scrollTop === 0) {
    postPage.value += 1;
    getPosts(postPage.value, false);
  }
};

// Provider 가져오기
const getProviders = async () => {
  const response = await axios.get(`/nm/images/providers`);
  response.data.Provider.forEach((item: string) => {  // Change String to string
    providers.push(item);
  });
};

const handleProviderClick = (provider: string) => {
  selectedProvider.value = provider;

  images.splice(0, images.length);
  imagePage.value = 0;

  getImages(imagePage.value);
};

// MasonryGrid에서 이미지 선택 시 업로드 리스트에 추가
const selectImageFromMasonry = (item: Image) => {
  if (uploadImageList.length >= MAX_UPLOAD_IMAGES) {
    warningToast(`최대 ${MAX_UPLOAD_IMAGES}장 까지 업로드할 수 있습니다.`);
    return;
  }

  // 이미 선택된 이미지인지 확인
  if (!uploadImageList.some(image => image.url === item.url)) {
    uploadImageList.push({
      id: item.id,
      url: item.url,
      filename: null,
      source: "gallery",
    });
    alertToast("이미지가 선택되었습니다.")
  } else {
    warningToast("이미 선택된 이미지 입니다.")
  }
};

// 이미지 가져오기
const getImages = async (pageValue: number) => {
  if (imageTotalPage.value !== 0 && pageValue >= imageTotalPage.value) return;

  const response = await axios.get(`/nm/images?page=${pageValue}&provider=${selectedProvider.value}`);
  imageTotalPage.value = response.data.totalPages;

  const newImages = response.data.content.filter((newImage: Image) => !images.some(image => image.id === newImage.id));
  images.push(...newImages);
};

// 이미지 제거 함수 (업로드 리스트에서 제거)
const removeUploadImage = (removeImage: Image) => {
  const index = uploadImageList.findIndex(img => img.url === removeImage.url);
  if (index > -1) {
    uploadImageList.splice(index, 1);
  }
  if (selectedPreviewImage.value === removeImage.url) {
    selectedPreviewImage.value = null;
  }
};

// 스크롤 이벤트 감지 후 다음 페이지 로드
const handleImageScroll = (event: Event) => {
  const element = event.target as HTMLElement;
  if (element.scrollHeight - element.scrollTop === element.clientHeight) {
    getImages(imagePage.value++);
  }
};

// 이미지 드래그 앤 드롭 업로드 핸들러
const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const files = event.dataTransfer?.files;
  if (files) {
    const filesArray = Array.from(files);
    if (uploadImageList.length + filesArray.length > MAX_UPLOAD_IMAGES) {
      warningToast(`최대 ${MAX_UPLOAD_IMAGES}장까지만 업로드할 수 있습니다.`);
      return;
    }
    filesArray.forEach(processFile);
  }
};

const processFile = (file: File) => {
  if (uploadImageList.length >= MAX_UPLOAD_IMAGES) {
    warningToast(`최대 ${MAX_UPLOAD_IMAGES}장 까지 업로드할 수 있습니다.`);
    return;
  }

  const reader = new FileReader();
  reader.onload = (e: ProgressEvent<FileReader>) => {
    const resultString = e.target?.result as string;
    if (resultString) {
      uploadImageList.push({
        id: null,
        url: resultString,
        filename: file.name,
        source: "upload"
      });
    }
  };
  reader.readAsDataURL(file);
};

const handleClick = () => {
  (fileInput.value as HTMLInputElement).click();
};

// 이미지 파일 선택 핸들러
const handleFileSelect = (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (files) {
    const filesArray = Array.from(files);
    if (uploadImageList.length + filesArray.length > MAX_UPLOAD_IMAGES) {
      warningToast(`최대 ${MAX_UPLOAD_IMAGES}장까지만 업로드할 수 있습니다.`);
      return;
    }
    filesArray.forEach(processFile);
  }
};

// Vue 컴포넌트가 마운트될 때 스크롤 이벤트 리스너 추가
onMounted(() => {
  getImages(imagePage.value);
  const imageListElement = document.querySelector('.imageList');
  imageListElement?.addEventListener('scroll', handleImageScroll);

  getProviders();
  getPosts(postPage.value, true);
  postContainerRef.value?.addEventListener('scroll', handlePostScroll);
});

const warningToast = (message: string) => {
  toast(message, {
    autoClose: 2000,
    theme: "dark",
    type: "warning",
  });
}

const alertToast = (message: string) => {
  toast(message, {
    autoClose: 2000,
    theme: "dark",
    type: "info",
  });
}

</script>


<template>
  <div class="w-screen h-screen flex p-2">
    <div class="flex flex-col w-1/5 bg-zinc-800 p-4 mx-2 rounded-md transition-all duration-300">
      <div class="text-white px-4 py-2">
        <p>고양이 짤</p>
        <p class="text-xs text-gray-400">나만 고양이 없어... ᓚᘏᗢ<br>고양이가 없는 분들을 위해 준비했습니다!</p>
      </div>
      <div class="flex flex-row py-2">
        <button v-for="provider in providers" class="btn btn-ghost mr-2" @click="handleProviderClick(provider)">{{ provider }}</button>
      </div>
      <div class="imageList border border-gray-400 rounded-md w-full h-[43rem] p-4 overflow-y-auto scroll-custom" @scroll="handleImageScroll">
        <MasonryGrid :images="images" @select-image="selectImageFromMasonry" />
      </div>
    </div>

    <!-- 메인 게시판 섹션 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <PostChat
          :posts="posts"
          @scrollTop="() => { postPage.value += 1; getPosts(postPage.value, false); }"
      ></PostChat>
      <div class="flex flex-col p-2">
        <!-- 업로드 영역 -->
        <div class="h-full mx-2">
          <div class="upload-area border border-dashed rounded-md border-gray-500 p-2 relative"
               @drop="handleDrop"
               @dragover.prevent
               @click="handleClick"
          >
            <input
                type="file"
                ref="fileInput"
                class="hidden"
                @change="handleFileSelect"
                multiple
            />
            <div v-if="uploadImageList.length" class="flex flex-wrap gap-2">
              <div v-for="(img, index) in uploadImageList" :key="index" class="relative w-20 h-20">
                <img :src="img.url" class="w-full h-full object-cover rounded-md" />
                <button
                    @click.stop="removeUploadImage(img)"
                    class="absolute top-0 right-0 bg-black bg-opacity-60 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs"
                >
                  ✕
                </button>
              </div>
            </div>
            <div v-else class="p-4">
              <p class="text-center text-gray-400">왼쪽에서 이미지를 선택하거나 첨부하세요!</p>
            </div>
          </div>

        </div>
        <div class="flex flex-row mt-2">
          <!-- 입력창 영역 -->
          <div class="bg-zinc-800 rounded-md w-full h-full p-2">
            <textarea v-model:="content" placeholder="간단한 설명을 입력해주세요." maxlength="100" class="textarea textarea-bordered textarea-md bg-zinc-900 w-full h-full resize-none"></textarea>
          </div>
          <div class="h-full p-2">
            <button @click="writePost" class="btn btn-ghost border h-full border-gray-400"> ↵ </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.upload-area {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.scroll-custom {
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a; /* 스크롤바 색상과 트랙 색상 */
}

</style>
