<script setup lang="ts">
import {reactive, ref} from 'vue';
import { useClipboard } from '@vueuse/core';
import axios from 'axios';
import router from '@/router';

// 게시물 및 페이지네이션 상태
const posts = reactive<any[]>([]);
const page = reactive({ value: 1 });
const pageCount = 5;
const totalPage = reactive({ value: 0 });

// 업로드 이미지
const uploadImage = ref<string | null>(null);

// 갤러리 이미지 리스트
const images = reactive<string[]>([]);

// 게시물 작성 시간 계산 함수
const getWriteTime = (time: any) => {
  let answer = '';
  const now = new Date();
  const writeTime = new Date(time);
  let calc = Math.floor((now.getTime() - writeTime.getTime()) / 1000);

  answer = `${calc}초 전`;
  if (calc >= 60) {
    calc = Math.floor(calc / 60);
    answer = `${calc}분 전`;
    if (calc >= 60) {
      calc = Math.floor(calc / 60);
      answer = `${calc}시간 전`;
      if (calc >= 24) {
        calc = Math.floor(calc / 24);
        answer = `${calc}일 전`;
        if (calc >= 30) {
          calc = Math.floor(calc / 30);
          answer = `${calc}달 전`;
          if (calc >= 12) {
            calc = Math.floor(calc / 12);
            answer = `${calc}년 전`;
          }
        }
      }
    }
  }
  return answer;
};

// 특정 페이지의 게시물 가져오기
const movePage = (pageValue: any) => {
  const springPageValue = pageValue - 1;
  axios.get(`/nm/boards?page=${springPageValue}&size=${pageCount}`).then(response => {
    totalPage.value = response.data.totalPages;
    posts.splice(0, posts.length, ...response.data.content);
  });
};

movePage(page.value);

// 게시물 작성 페이지로 이동
const moveToWrite = () => {
  axios.post('/nm/user/check').then(() => {
    router.push({ name: 'write' });
  }).catch(error => {
    if (error.response) {
      router.push({ name: 'login' });
    }
  });
};

// 이미지 드래그 앤 드롭 업로드 핸들러
const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const files = event.dataTransfer?.files;
  if (files && files.length > 0) {
    const file = files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      uploadImage.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 클립보드에 링크 복사
const { copy } = useClipboard();
const copyLink = (link: string) => {
  copy(link);
  alert(`링크가 복사되었습니다: ${link}`);
};
</script>

<template>
  <div class="w-screen h-screen flex p-2">
    <!-- 왼쪽 사이드바 -->
    <div class="flex flex-col w-1/5 bg-zinc-800 p-4 mx-2 rounded-md transition-all duration-300">
      <div class="h-3/5 text-white p-4 mb-8">
        <p>냥뮤니티 이미지</p>
        <p class="text-xs text-gray-400">냥뮤니티 인기 이미지를 살펴보세요!</p>
      </div>
      <div class="h-2/5 bg-zinc-800 rounded-md text-white p-4 mr-2">
        <p>이미지 업로드</p>
        <p class="text-xs text-gray-400">당신만의 이미지를 업로드해보세요!</p>
        <div class="upload-area border border-dashed border-gray-500 mt-4 p-4" @drop="handleDrop" @dragover.prevent>
          <div v-if="uploadImage" class="mr-2">
            <img :src="uploadImage" class="w-full h-32 object-cover rounded-md" />
          </div>
          <div v-else>
            <p class="text-center text-gray-400">Drag & Drop</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 메인 게시판 섹션 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <div class="flex-1 border border-gray-400 rounded-md overflow-auto p-4 m-4 boardList h-5/6">
        <ul class="w-full">
          <li class="mb-4 p-4 bg-gray-100 rounded-md shadow" v-for="post in posts" :key="post.id">
            <div>
              <h3 class="font-bold text-xl mb-2">
                <router-link class="text-blue-500 hover:text-blue-800" :to="{ name: 'read', params: { postId: post.id } }">{{ post.title }}</router-link>
              </h3>
              <p class="badge badge-primary badge-outline mr-1.5">{{ post.writer }}</p>
              <p class="badge badge-primary badge-outline">{{ getWriteTime(post.createDate) }}</p>
            </div>
            <div class="mt-2 flex flex-wrap w-full h-full">
              <router-link class="w-full h-full" :to="{ name: 'read', params: { postId: post.id } }">
                <div class="w-full h-full" v-if="post.boardImages && post.boardImages.length > 0" v-for="(boardImage, index) in post.boardImages.slice(0, Math.min(3, post.boardImages.length))">
                  <div v-if="index <= 1">
                    <img class="w-full h-full object-contain rounded-md" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
                  </div>
                  <div class="w-full h-24 flex items-center justify-center text-white bg-gray-400 rounded-md" v-else>
                    +{{ post.boardImages.length - 2 }}
                  </div>
                </div>
                <div class="flex flex-row justify-center w-full h-80 border" v-else>
                  <img class="w-full h-full object-contain rounded-md" src="/assets/images/cat_loading.gif" />
                </div>
              </router-link>
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
    <div class="bg-zinc-800 w-1/5 rounded-md text-white p-4 ml-2 transition-all duration-300">
      <div class="text-white p-4">
        <p>고양이 짤</p>
        <p class="text-xs text-gray-400">고양이가 없는 분들을 위해 준비했습니다!</p>
      </div>
      <div class="gallery p-4 grid grid-cols-1 gap-2">
        <div v-for="(img, index) in images" :key="index" class="relative group">
          <img :src="img" class="w-full h-full object-cover rounded-md" @click="copyLink(img)" />
          <div class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
            <p class="text-white">클릭하여 링크 복사</p>
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

.boardList {
  overflow-y: scroll;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.boardList::-webkit-scrollbar {
  display: none;
}

.gallery {
  display: grid;
  gap: 1rem;
}

.gallery .group {
  position: relative;
}

.gallery .group img {
  transition: transform 0.3s;
}

.gallery .group:hover img {
  transform: scale(1.1);
}

.gallery .group .group-hover\:opacity-100 {
  transition: opacity 0.3s;
}
</style>
