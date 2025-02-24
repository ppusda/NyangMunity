<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import MasonryGrid from "@/components/MasonryGrid.vue";
import PostChat from "@/components/PostChat.vue";
import 'vue3-toastify/dist/index.css';

interface Post {
  id: string;
  content: string;
  createDate: string;
  uid: number;
  writer: string;
}

interface Image {
  id: string;
  url: string;
}

// 게시물 및 페이지네이션 상태
const posts = reactive<Post[]>([]);
const postPage = reactive({ value: 1 });
const postTotalPage = reactive({ value: 0 });
const postContainerRef = ref<HTMLElement | null>(null);

// 게시물 작성
const content = ref<string>("");

// 밈 및 페이지네이션 상태
const images = reactive<Image[]>([]);
const imagePage = reactive({ value: 0 });
const imageTotalPage = reactive({ value: 0 });

// 업로드 이미지
const uploadImageList = ref<string[]>([]);
const uploadImage = ref<string | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

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
  } catch (error) {
    console.error(error);
  }
};

// 스크롤 이벤트 핸들러 (위로 스크롤시 이전 페이지 로드)
const handlePostScroll = (event: Event) => {
  const element = event.target as HTMLElement;
  if (element.scrollTop === 0) {
    postPage.value += 1;
    getPosts(postPage.value, false);
  }
};


// 게시물 업로드
const writePost = () => {
  axios.post('/nm/post', {
    "content": content.value,
    "postImages": uploadImageList.value
  });
  getPosts(postPage.value, true);
}

// Provider 가져오기
const getProviders = async (pageValue: number) => {
  const response = await axios.get(`/nm/images/providers`);

};

// 이미지 가져오기
const getImages = async (pageValue: number) => {
  if (imageTotalPage.value !== 0 && pageValue >= imageTotalPage.value) return;

  const response = await axios.get(`/nm/images?page=${pageValue}`);
  imageTotalPage.value = response.data.totalPages;

  const newImages = response.data.content.filter((newImage: Image) => !images.some(image => image.id === newImage.id));
  images.push(...newImages);
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
  if (files && files.length > 0) {
    const file = files[0];
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      uploadImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const handleClick = () => {
  (fileInput.value as HTMLInputElement).click();
};

// 이미지 파일 선택 핸들러
const handleFileSelect = (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (files) {
    const file = files[0];
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      uploadImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

// 이미지 업로드
const postImageUpload = () => {
  uploadImageList.value.push();
  axios.get(`/nm/image`, {

  }).then(response => {

  });
};

// Vue 컴포넌트가 마운트될 때 스크롤 이벤트 리스너 추가
onMounted(() => {
  getImages(imagePage.value);
  const imageListElement = document.querySelector('.imageList');
  imageListElement?.addEventListener('scroll', handleImageScroll);

  getPosts(postPage.value, true);
  postContainerRef.value?.addEventListener('scroll', handlePostScroll);
});
</script>


<template>
  <div class="w-screen h-screen flex p-2">
    <div class="flex flex-col w-1/5 bg-zinc-800 p-4 mx-2 rounded-md transition-all duration-300">
      <div class="text-white px-4 py-2">
        <p>고양이 짤</p>
        <p class="text-xs text-gray-400">나만 고양이 없어... ᓚᘏᗢ<br>고양이가 없는 분들을 위해 준비했습니다!</p>
      </div>
      <div class="flex flex-row py-2">
        <button class="btn btn-ghost mr-2">Nyangmunity</button>
        <button class="btn btn-ghost mr-2">Tenor</button>
      </div>
      <div class="imageList border border-gray-400 rounded-md w-full h-[43rem] p-4 overflow-y-auto scroll-custom" @scroll="handleImageScroll">
        <MasonryGrid :images="images" />
      </div>
    </div>

    <!-- 메인 게시판 섹션 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <PostChat :posts="posts"></PostChat>

      <div class="flex flex-col p-2">
        <!-- 업로드 영역 -->
        <div class="h-full mx-2">
          <div
              class="upload-area border border-dashed rounded-md border-gray-500 p-2 relative"
              @drop="handleDrop"
              @dragover.prevent
              @click="handleClick"
          >
            <input
                type="file"
                ref="fileInput"
                class="hidden"
                @change="handleFileSelect"
            />
            <div v-if="uploadImage">
              <img :src="uploadImage" class="w-full h-16 object-cover rounded-md" />
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
