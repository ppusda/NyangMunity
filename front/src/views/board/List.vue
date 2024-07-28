<script setup lang="ts">
import axios from "axios";
import { reactive, ref } from "vue";
import router from "@/router";
import paginate from "vuejs-paginate-next";
import { useClipboard } from '@vueuse/core';

const posts = reactive<any[]>([]);
const page = reactive({ value: 1 });
const pageCount = 5;
const totalPage = reactive({ value: 0 });

const getWriteTime = (time: any) => {
  let answer = "";

  let now = new Date();
  let writeTime = new Date(time);
  let calc = Math.floor((now.getTime() - writeTime.getTime()) / 1000);

  answer = calc + "초 전";
  if (calc >= 60) {
    calc = Math.floor(calc / 60);
    answer = calc + "분 전";
    if (calc >= 60) {
      calc = Math.floor(calc / 60);
      answer = calc + "시간 전";
      if (calc >= 24) {
        calc = Math.floor(calc / 24);
        answer = calc + "일 전";
        if (calc >= 30) {
          calc = Math.floor(calc / 30);
          answer = calc + "달 전";
          if (calc >= 12) {
            Math.floor(calc / 12);
            answer = calc + "년 전";
          }
        }
      }
    }
  }

  return answer;
}

const movePage = (pageValue: any) => {
  const springPageValue = pageValue - 1;
  axios.get(`/nm/boards?page=${springPageValue}&size=${pageCount}`).then(response => {
    totalPage.value = response.data.totalPages;
    posts.splice(0, posts.length);
    response.data.content.forEach((res: any) => {
      posts.push(res);
    });
  });
}

movePage(page.value);

const moveToWrite = () => {
  axios.post("/nm/user/check").then(() => {
    router.push({ name: "write" });
  }).catch(error => {
    if (error.response) {
      router.push({ name: "login" });
    }
  });
}

// 드래그 앤 드롭 영역 토글 상태
const showUploadArea = ref(false);
// 이미지 갤러리 토글 상태
const showGallery = ref(false);
// 이미지 리스트
const images = reactive<string[]>([]);

// 이미지 업로드 핸들러
const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const files = event.dataTransfer?.files;
  if (files) {
    for (let i = 0; i < files.length; i++) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        images.push(e.target.result);
      };
      reader.readAsDataURL(files[i]);
    }
  }
}

// 이미지 링크 복사 핸들러
const { copy } = useClipboard();
const copyLink = (link: string) => {
  copy(link);
  alert("링크가 복사되었습니다: " + link);
}

</script>

<template>
  <div class="w-screen h-screen flex p-2">
    <!-- 이미지 업로드 공간 -->
    <div class="w-1/5 bg-zinc-800 rounded-md text-white p-4 mr-2">
      <div v-if="showUploadArea" class="upload-area border border-dashed border-gray-500 mt-2 p-4" @drop="handleDrop" @dragover.prevent>
        <p class="text-center text-gray-400">여기로 이미지를 드래그 앤 드롭 하세요.</p>
      </div>
    </div>

    <!-- 채팅창 형식 게시판과 입력창 -->
    <div class="flex-1 flex flex-col bg-zinc-800 p-4 mx-2 rounded-md">
      <div class="flex-1 rounded-md overflow-auto mb-2 p-2 boardList">
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
      <div class="bg-zinc-800 rounded-md p-2">
        <div class="flex flex-row mb-1">
          <button class="btn btn-ghost" @click="showUploadArea = !showUploadArea"><i class="fa-solid fa-image"></i></button>
          <button class="btn btn-ghost" @click="showGallery = !showGallery"><i class="fa-solid fa-cat"></i></button>
        </div>
        <textarea placeholder="고양이 사진과 간단한 설명을 입력해주세요." class="textarea textarea-bordered textarea-md bg-zinc-900 w-full h-[7.5rem]"></textarea>
      </div>
    </div>

    <!-- Masonry 형식 이미지 제공 공간 -->
    <div class="w-1/5 bg-zinc-800 rounded-md text-white p-4 ml-2">
      <div v-if="showGallery" class="gallery mt-2 grid grid-cols-1 gap-2">
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

<style>
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
