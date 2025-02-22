<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import MasonryGrid from "@/components/MasonryGrid.vue";
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

// 작성 시간 표시
const getWriteTime = (time: string) => {
  const now = new Date();
  const writeTime = new Date(time);

  // 로컬 시간대 기준으로 날짜 비교
  const nowDate = now.toLocaleDateString();
  const writeDate = writeTime.toLocaleDateString();

  const timeDifference = now.getTime() - writeTime.getTime();
  const seconds = Math.floor(timeDifference / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  const formatTime = (date: Date) => {
    const hour = date.getHours();
    const minute = date.getMinutes();
    const period = hour >= 12 ? '오후' : '오전';
    const formattedHour = hour % 12 || 12;
    return `${period} ${formattedHour}:${minute < 10 ? '0' + minute : minute}`;
  };

  // 작성 날짜가 오늘인 경우
  if (writeDate === nowDate) {
    return `오늘 ${formatTime(writeTime)}`;
  }
  // 작성 날짜가 어제인 경우
  else if (writeDate === new Date(now.getTime() - 86400000).toLocaleDateString()) {
    return `어제 ${formatTime(writeTime)}`;
  }
  // 그 외 날짜 표시
  else {
    const year = writeTime.getFullYear();
    const month = String(writeTime.getMonth() + 1).padStart(2, '0');
    const date = String(writeTime.getDate()).padStart(2, '0');
    return `${year}.${month}.${date}. ${formatTime(writeTime)}`;
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

// 이미지 가져오기
const getImages = async (pageValue: number) => {
  if (imageTotalPage.value !== 0 && pageValue >= imageTotalPage.value) return;

  const response = await axios.get(`/nm/image/?page=${pageValue}`);
  imageTotalPage.value = response.data.totalPages;

  const newImages = response.data.content.filter((newImage: Image) => !images.some(image => image.id === newImage.id));

  // Vue의 반응형 객체는 배열 메서드로 직접 수정해야 함
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
        <MasonryGrid :items="images" />
      </div>
    </div>

    <!-- 메인 게시판 섹션 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <div
          ref="postContainerRef"
          class="border border-gray-400 border-md rounded-md overflow-auto p-4 m-4 scroll-custom h-[36rem]"
          @scroll="handlePostScroll"
      >
        <ul class="w-full flex flex-col-reverse">
          <li class="p-4 bg-zinc-800 rounded-md" v-for="post in posts" :key="post.id">
            <div class="flex flex-row text-center items-center">
              <p class="text-xl text-white mr-3">{{ post.writer }}</p>
              <p class="text-xs">{{ getWriteTime(post.createDate) }}</p>
            </div>
            <div class="mt-3">
              <p class="text-white mr-3">{{ post.content }}</p>
            </div>
          </li>
        </ul>
      </div>

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
