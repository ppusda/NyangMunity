<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from 'vue';
import { useClipboard } from '@vueuse/core';
import axios from 'axios';
import { toast } from "vue3-toastify";
import 'vue3-toastify/dist/index.css';

interface Post {
  id: string;
  content: string;
  createDate: string;
  uid: number;
  writer: string;
}

interface Gif {
  id: string;
  url: string;
}

// 게시물 및 페이지네이션 상태
const posts = reactive<Post[]>([]);
const postPage = reactive({ value: 1 });
const postTotalPage = reactive({ value: 0 });
const postContainerRef = ref<HTMLElement | null>(null);

// 밈 및 페이지네이션 상태
const memes = reactive<Gif[]>([]);
const memePage = reactive({ value: 1 });
const memeTotalPage = reactive({ value: 0 });

// 업로드 이미지
const uploadImage = ref<string | null>(null);

// 게시물 가져오기
const getPosts = async (page: number, init: boolean) => {
  if (postTotalPage.value !== 0 && page >= postTotalPage.value) return;

  try {
    const response = await axios.get(`/nm/boards?page=${page - 1}&size=10`);
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

// 밈 이미지 가져오기
const getMemeImages = (pageValue: number) => {
  if (memeTotalPage.value !== 0 && pageValue >= memeTotalPage.value) return;

  axios.get(`/nm/meme?page=${pageValue}`).then(response => {
    memeTotalPage.value = response.data.totalPages;
    const newMemes = response.data.content.filter((newMeme: Gif) => !memes.some(meme => meme.id === newMeme.id));
    memes.push(...newMemes);
  });
};

// 스크롤 이벤트 핸들러
const handleMemeScroll = (event: Event) => {
  const element = event.target as HTMLElement;
  if (element.scrollHeight - element.scrollTop === element.clientHeight) {
    // 스크롤이 맨 아래에 도달했을 때 다음 페이지 로드
    getMemeImages(memePage.value++);
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

// 클립보드에 링크 복사
const { copy } = useClipboard();
const copyLink = (link: string) => {
  copy(link);
  toast("이미지 복사 완료!", {
    autoClose: 2000, theme: "dark"
  });
};

// Vue 컴포넌트가 마운트될 때 스크롤 이벤트 리스너 추가
onMounted(() => {
  getMemeImages(0); // 첫 페이지 로드
  const memeListElement = document.querySelector('.memeList');
  memeListElement?.addEventListener('scroll', handleMemeScroll);

  getPosts(postPage.value, true);
  postContainerRef.value?.addEventListener('scroll', handlePostScroll);
});
</script>


<template>
  <div class="w-screen h-screen flex p-2">
    <div class="flex flex-col w-1/5 bg-zinc-800 p-4 mx-2 rounded-md transition-all duration-300">
      <div class="h-3/5 bg-zinc-800 rounded-md text-white p-4 mr-2">
        <p>냥뮤니티 이미지</p>
        <p class="text-xs text-gray-400">냥뮤니티 인기 이미지를 살펴보세요!</p>
      </div>
      <div class="h-3/5 bg-zinc-800 rounded-md text-white p-4 mr-2">
        <p>이미지 업로드</p>
        <p class="text-xs text-gray-400">당신만의 고양이 이미지를 업로드해보세요!</p>
        <div
            class="upload-area border border-dashed border-gray-500 mt-4 p-4 relative"
            @drop="handleDrop"
            @dragover.prevent
            @click="$refs.fileInput.click()"
        >
          <input
              type="file"
              ref="fileInput"
              class="hidden"
              multiple
              @change="handleFileSelect"
          />
          <div v-if="uploadImage">
            <img :src="uploadImage" class="w-full h-32 object-cover rounded-md" />
          </div>
          <div v-else>
            <p class="text-center text-gray-400">Drag & Drop<br>or<br>Click to Upload</p>
          </div>
        </div>
        <button class="btn btn-ghost w-full border border-gray-400 mt-2">변환</button>
        <hr class="my-3 border-gray-500">
        <div class="flex flex-row text-center justify-center items-center">
          <p class="text-sm text-white mr-3 flex items-center">URL</p>
          <input class="input h-[2rem] w-full bg-zinc-800 rounded-md border border-gray-400" readonly>
        </div>
        <p class="text-xs text-gray-400 mt-2">* 게시글 작성에 이용하지 않을 시 이미지는 자동 삭제됩니다.</p>
      </div>
    </div>

    <!-- 메인 게시판 섹션 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <div
          ref="postContainerRef"
          class="border border-gray-400 border-md rounded-md overflow-auto p-4 m-4 scroll-custom h-[38rem]"
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
      <!-- 입력창 영역 -->
      <div class="flex flex-row h-1/6">
        <div class="bg-zinc-800 rounded-md p-2 w-11/12">
          <textarea placeholder="고양이 사진과 설명을 입력해주세요." maxlength="100" class="textarea textarea-bordered textarea-md bg-zinc-900 w-full h-[7.5rem] resize-none"></textarea>
        </div>
        <div class="h-full p-2 w-1/12">
          <button class="btn btn-ghost h-full border border-gray-400">전송</button>
        </div>
      </div>
    </div>

    <!-- 오른쪽 사이드바 -->
    <div class="bg-zinc-800 w-1/5 rounded-md text-white p-4 ml-2 transition-all duration-300 h-full">
      <div class="text-white p-4">
        <p>고양이 짤</p>
        <p class="text-xs text-gray-400">나만 고양이 없어... ᓚᘏᗢ<br>고양이가 없는 분들을 위해 준비했습니다!</p>
      </div>
      <div class="border border-gray-400 rounded-md w-full h-[43rem] p-8 memeList overflow-y-auto scroll-hidden">
        <div class="masonry h-full">
          <div v-for="meme in memes" class="masonry-item group relative" @click="copyLink(meme.url)">
            <img :src="meme.url" :id="`cmg_${meme.id}`" class="w-full h-full object-cover rounded-md" />
            <div class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <p class="text-xs text-white">클릭하여 링크 복사</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.upload-area {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.scroll-hidden{
  overflow-y: scroll;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scroll-hidden::-webkit-scrollbar {
  display: none;
}

.masonry {
  display: table-cell;
  margin: auto;
  column-count: 2;
  column-gap: 0.5em;
}

.masonry::-webkit-scrollbar {
  display: none; /* Chrome, Safari에서 스크롤바 숨기기 */
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 0.5em;
}

.masonry img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.group {
  position: relative;
}

.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

.group-hover\:opacity-100 {
  opacity: 0;
  transition: opacity 0.3s;
}

.scroll-custom {
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a; /* 스크롤바 색상과 트랙 색상 */
}

</style>
