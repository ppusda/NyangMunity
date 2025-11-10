<script setup lang="ts">
import {computed, nextTick, onMounted, reactive, ref} from 'vue';
import {infoToast, warningToast} from '@/libs/toaster';

import MasonryGrid from "@/components/MasonryGrid.vue";
import PostChat from "@/components/PostChat.vue";

import type {Image, Post} from '@/interfaces/type';

import 'vue3-toastify/dist/index.css';
import axiosClient from "@/libs/axiosClient";
import s3Client from "@/libs/s3Client";
import router from "@/router";
import store from "@/stores/store";

// 로그인 상태 확인
const isLogin = computed(() => store.state.isLogin);

// 패널 상태 관리를 위한 변수
const isImagePanelCollapsed = ref(window.innerWidth < 768);
const isInputAreaCollapsed = ref(window.innerWidth < 768);

// 이미지 제공자 상태
const providers = reactive<string[]>([]);
const selectedProvider = reactive({value: "Nyangmunity"});

// 게시물 및 페이지네이션 상태
const posts = reactive<Post[]>([]);
const postPage = reactive({value: 1});
const postTotalPage = reactive({value: 0});
const postContainerRef = ref<HTMLElement | null>(null);
const postChatRef = ref(null);

// 게시물 작성
const content = ref<string>("");

// 이미지 및 페이지네이션 상태
const images = reactive<Image[]>([]);
const imagePage = reactive({value: 0});
const imageTotalPage = reactive({value: 0});

// 업로드 이미지
const uploadImageList = reactive<Image[]>([]);
const selectedPreviewImage = ref<string | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);
const MAX_UPLOAD_IMAGES = 5;

// 게시물 가져오기
const getPosts = async (page: number, init: boolean) => {
  if (postTotalPage.value !== 0 && page >= postTotalPage.value) return;

  try {
    const response = await axiosClient.get(`/posts?page=${page - 1}&size=10`);
    postTotalPage.value = response.data.totalPages;

    if (init) {
      posts.splice(0, posts.length, ...response.data.content);
      await nextTick();
      setTimeout(() => {
        scrollToPostBottom(true);
      }, 50);
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

// 게시물 업로드
const writePost = async () => {
  if (!isLogin.value) {
    await router.replace({name: "login"});
    return;
  }

  const uploadedImageIds: string[] = await uploadImages();
  if (uploadedImageIds.length <= 0) {
    warningToast("이미지는 필수로 입력해야 합니다.");
    return;
  }

  axiosClient.post('/posts', {
    content: content.value,
    postImageIds: uploadedImageIds,
  }).then(() => {
    uploadImageList.splice(0, uploadImageList.length);  // 업로드 후 초기화
    content.value = "";  // 내용 초기화
    getPosts(postPage.value, true).then(() => {
      setTimeout(() => {
        scrollToPostBottom(true);
      }, 50);
    });
  });
};

// 이미지 업로드
const uploadImages = async () => {
  const uploadedImageIds: string[] = [];

  try {
    for (const image of uploadImageList) {
      if (image.source === "gallery") {
        uploadedImageIds.push(image.id as string);
      } else if (image.source === "upload") {
        // Presigned URL 받기
        const response = await axiosClient.get(`/images/upload?filename=${image.filename}`);
        const {id, uploadUrl} = response.data;

        // S3 업로드
        const blob = dataUrlToBlob(image.url);
        await s3Client.put(uploadUrl, blob, {
          headers: {
            'Content-Type': blob.type || 'image/jpeg'
          }
        });

        uploadedImageIds.push(id);
      }
    }
  } catch (error) {
    console.error('이미지 업로드 실패:', error);
    warningToast('이미지 업로드에 실패했습니다.');
    throw error;  // 상위에서 처리하도록
  }

  return uploadedImageIds;
};

const dataUrlToBlob = (dataUrl: string): Blob => {
  const arr = dataUrl.split(',');
  const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/jpeg';
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);

  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }

  return new Blob([u8arr], {type: mime});
};

// 스크롤 이벤트 핸들러 (위로 스크롤시 이전 페이지 로드)
const handlePostScroll = (event: Event) => {
  const element = event.target as HTMLElement;
  if (element.scrollTop === 0) {
    postPage.value += 1;
    getPosts(postPage.value, false);
  }
};

const scrollToPostBottom = (smooth = true) => {
  if (postContainerRef.value) {
    postContainerRef.value.scrollTo({
      top: postContainerRef.value.scrollHeight,
      behavior: smooth ? 'smooth' : 'auto'
    });
  }
};


// Provider 가져오기
const getProviders = async () => {
  const response = await axiosClient.get(`/images/providers`);
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
    infoToast("이미지가 선택되었습니다.")
  } else {
    warningToast("이미 선택된 이미지 입니다.")
  }
};

// 이미지 가져오기
const getImages = async (pageValue: number) => {
  if (imageTotalPage.value !== 0 && pageValue >= imageTotalPage.value) return;

  const response = await axiosClient.get(`/images?page=${pageValue}&provider=${selectedProvider.value}`);
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

// 패널 토글 함수 (모바일에서는 하나만 열리도록)
const toggleImagePanel = () => {
  const isMobile = window.innerWidth < 768;

  if (isMobile) {
    // 모바일에서는 이미지 패널을 열 때 입력 영역을 닫음
    if (isImagePanelCollapsed.value) {
      isInputAreaCollapsed.value = true;
    }
  }

  isImagePanelCollapsed.value = !isImagePanelCollapsed.value;
};

const toggleInputArea = () => {
  const isMobile = window.innerWidth < 768;

  if (isMobile) {
    // 모바일에서는 입력 영역을 열 때 이미지 패널을 닫음
    if (isInputAreaCollapsed.value) {
      isImagePanelCollapsed.value = true;
    }
  }

  isInputAreaCollapsed.value = !isInputAreaCollapsed.value;
};

// Vue 컴포넌트가 마운트될 때 스크롤 이벤트 리스너 추가
onMounted(() => {
  getImages(imagePage.value);
  const imageListElement = document.querySelector('.imageList');
  imageListElement?.addEventListener('scroll', handleImageScroll);

  getProviders();
  getPosts(postPage.value, true);
  postContainerRef.value?.addEventListener('scroll', handlePostScroll);

  // 화면 크기 변경 감지 - 좀 더 정교하게 설정
  window.addEventListener('resize', () => {
    const isMobile = window.innerWidth < 768;

    // 모바일에서는 패널들을 접기
    if (isMobile) {
      isImagePanelCollapsed.value = true;
      isInputAreaCollapsed.value = true;
    }
  });
});

</script>

<template>
  <div class="w-screen h-screen flex flex-col overflow-hidden">
    <!-- 데스크톱: 가로 배치, 모바일: 세로 배치 -->
    <div class="flex-1 flex flex-col md:flex-row p-2 overflow-hidden">

      <!-- 왼쪽 이미지 패널과 토글 버튼 (데스크톱에서만 보임) -->
      <div class="hidden md:flex relative h-full">
        <!-- 이미지 패널 - 접히면 완전히 사라짐 -->
        <div :class="[
          'flex flex-col bg-zinc-800 rounded-md transition-all duration-300 h-full overflow-hidden',
          isImagePanelCollapsed ? 'w-0 p-0 opacity-0 m-0' : 'w-64 md:w-80 lg:w-96 p-4 mx-2'
          ]">
          <div class="text-white px-4 py-2">
            <p>고양이 짤</p>
            <p class="text-xs text-gray-400">나만 고양이 없어... ᓚᘏᗢ<br>고양이가 없는 분들을 위해 준비했습니다!</p>
          </div>
          <div class="flex flex-row flex-wrap py-2">
            <button v-for="provider in providers" class="btn btn-ghost mr-2 mb-2"
                    @click="handleProviderClick(provider)">
              {{ provider }}
            </button>
          </div>
          <div class="imageList border border-gray-400 rounded-md w-full flex-1 p-4 overflow-y-auto scroll-custom"
               @scroll="handleImageScroll">
            <MasonryGrid :images="images" @select-image="selectImageFromMasonry"/>
          </div>
        </div>

        <!-- 이미지 패널 토글 버튼 -->
        <button @click="toggleImagePanel"
                class="absolute top-1/2 -translate-y-1/2 left-0 z-10 bg-zinc-700 hover:bg-zinc-600 text-white rounded-r-md h-12 w-6 flex items-center justify-center">
          <span v-if="isImagePanelCollapsed">→</span>
          <span v-else>←</span>
        </button>
      </div>

      <div class="flex flex-col overflow-hidden w-full">
        <!-- 메인 콘텐츠 영역 -->
        <div class="flex-1 bg-zinc-800 p-4 mx-2 rounded-md overflow-hidden">
          <PostChat
              ref="postChatRef"
              :posts="posts"
              :isInputAreaCollapsed="isInputAreaCollapsed"
              @scrollTop="() => { postPage.value += 1; getPosts(postPage.value, false); }"
          ></PostChat>
        </div>

        <!-- 입력 영역 (하단) -->
        <div class="flex flex-col relative">
          <!-- 토글 버튼 -->
          <div>
            <button @click="toggleInputArea"
                    class="hidden md:flex absolute bottom-0 right-1/2 transform translate-x-1/2 z-20 bg-zinc-700 hover:bg-zinc-600 text-white rounded-t-md h-6 w-12 flex items-center justify-center transition-colors">
              <span v-if="isInputAreaCollapsed">↑</span>
              <span v-else>↓️</span>
            </button>
          </div>

          <!-- 입력 영역 -->
          <div :class="[
        'bg-zinc-800 rounded-md transition-all duration-300 overflow-hidden mx-2',
        isInputAreaCollapsed ? 'h-0 p-0 opacity-0' : 'h-48 p-4 mt-3'
      ]">
            <!-- 업로드 영역 -->
            <div>
              <div class="upload-area border border-dashed rounded-md border-gray-500 p-2 relative mb-2"
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
                    <img :src="img.url" class="w-full h-full object-cover rounded-md"/>
                    <button
                        @click.stop="removeUploadImage(img)"
                        class="absolute -top-1 -right-1 bg-red-500 hover:bg-red-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs font-bold transition-colors"
                    >
                      ✕
                    </button>
                  </div>
                </div>
                <div v-else class="p-4">
                  <p class="text-center text-gray-400 text-sm">이미지를 선택하거나 드래그해서 첨부하세요!</p>
                </div>
              </div>
            </div>

            <!-- 텍스트 입력과 전송 버튼 -->
            <div class="flex flex-row gap-2">
              <div class="flex-1">
            <textarea
                v-model="content"
                placeholder="간단한 설명을 입력해주세요."
                maxlength="100"
                class="textarea textarea-bordered bg-zinc-900 text-white placeholder-gray-400 w-full h-16 resize-none focus:border-zinc-600 focus:outline-none"
            ></textarea>
              </div>
              <div class="flex items-end">
                <button
                    @click="writePost"
                    class="btn btn-primary h-16 px-4 bg-blue-600 hover:bg-blue-700 border-blue-600 hover:border-blue-700 text-white transition-colors"
                    :disabled="!uploadImageList.length"
                >
                  전송
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 모바일 이미지 패널 -->
    <div class="md:hidden flex flex-col relative">
      <!-- 이미지 패널 -->
      <div :class="[
        'bg-zinc-800 rounded-md transition-all duration-300 overflow-hidden mx-2 mb-2',
        isImagePanelCollapsed ? 'h-0 p-0 opacity-0' : 'h-64 p-4'
      ]">
        <div class="flex flex-col h-full">
          <!-- 프로바이더 버튼들 -->
          <div class="flex flex-row flex-wrap gap-2 mb-3">
            <button
                v-for="provider in providers"
                :key="provider"
                :class="[
                'btn btn-sm px-3 py-1 rounded-md transition-colors text-sm',
                selectedProvider.value === provider
                  ? 'bg-zinc-600 text-white'
                  : 'btn-ghost text-gray-300 hover:bg-zinc-700'
              ]"
                @click="handleProviderClick(provider)">
              {{ provider }}
            </button>
          </div>

          <!-- 이미지 그리드 -->
          <div class="imageList border border-gray-500 rounded-md flex-1 p-3 overflow-y-auto scroll-custom">
            <MasonryGrid :images="images" @select-image="selectImageFromMasonry"/>
          </div>
        </div>
      </div>
    </div>

    <!-- 모바일 하단 바 -->
    <div class="md:hidden bg-zinc-800 mx-2 mb-2 rounded-md">
      <div class="flex justify-end gap-1">
        <!-- 이미지 패널 토글 버튼 -->
        <button
            @click="toggleImagePanel"
            :class="[
              'flex items-center justify-center w-10 h-10 rounded-md transition-all duration-200',
              !isImagePanelCollapsed
                ? 'bg-blue-600 hover:bg-blue-700 text-white shadow-lg'
                : 'bg-zinc-800 hover:bg-zinc-700 text-gray-300'
            ]">
          <span class="text-sm">📷</span>
        </button>

        <!-- 입력 영역 토글 버튼 -->
        <button
            @click="toggleInputArea"
            :class="[
              'flex items-center justify-center w-10 h-10 rounded-md transition-all duration-200',
              !isInputAreaCollapsed
                ? 'bg-blue-600 hover:bg-blue-700 text-white shadow-lg'
                : 'bg-zinc-800 hover:bg-zinc-700 text-gray-300'
            ]">
          <span class="text-sm">✏️</span>
        </button>
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
  min-height: 80px;
  max-height: 100px;
  overflow-y: auto;
}

.upload-area:hover {
  border-color: #71717a;
}

.scroll-custom {
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a;
}

.scroll-custom::-webkit-scrollbar {
  width: 6px;
}

.scroll-custom::-webkit-scrollbar-track {
  background: #27272a;
  border-radius: 3px;
}

.scroll-custom::-webkit-scrollbar-thumb {
  background: #52525b;
  border-radius: 3px;
}

.scroll-custom::-webkit-scrollbar-thumb:hover {
  background: #71717a;
}

@media (max-width: 768px) {
  .w-0 {
    width: 0 !important;
    min-width: 0 !important;
  }

  .h-0 {
    height: 0 !important;
    min-height: 0 !important;
  }
}
</style>