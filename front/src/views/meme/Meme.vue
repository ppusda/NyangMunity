<script setup lang="ts">

import {onMounted, onUnmounted, ref} from "vue";
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

interface Gif {
  title: string;
  media_formats: {
    gif: {
      url: string;
    };
  };
}

const gifs = ref<Gif[]>([]);
const memeList = ref();

const { VITE_APP_TENOR_SECRET_KEY } = import.meta.env;
const { VITE_APP_TENOR_CLIENT_KEY } = import.meta.env;
const limit = 36;

let posKey: string[] = [];
let currentPage = -1;


function getData(searchTerm: string, page: number): Promise<Gif[]> {
  const search_url = `https://tenor.googleapis.com/v2/search?q=${searchTerm}&key=${VITE_APP_TENOR_SECRET_KEY}&client_key=${VITE_APP_TENOR_CLIENT_KEY}&limit=${limit}&pos=${posKey[page]}`;

  return new Promise((resolve, reject) =>{
    const xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function () {
      if (xmlHttp.readyState === 4) {
        if (xmlHttp.status === 200) {
          const response_objects = JSON.parse(xmlHttp.responseText);
          const gifs = response_objects["results"];
          posKey.push(response_objects["next"]);
          resolve(gifs);
        }else{
          reject(xmlHttp.statusText);
        }
      }
    };

    xmlHttp.open("GET", search_url, true);
    xmlHttp.send(null)
  });
}

async function searchGifs(searchTerm: string, page:number): Promise<void> {
  try {
    const searchResults = await getData(searchTerm, page);
    gifs.value = gifs.value.concat(searchResults);
  } catch (error) {
    console.error(error);
  }
}

async function nextPage(): Promise<void> {
  currentPage++;
  await searchGifs("cat meme", currentPage);
}

function copyImageUrl(url: string): void {
  navigator.clipboard.writeText(url).then(() => {
      toast("Image Copied!", {
        autoClose: 3000,
      })
  }, (error) => {
    console.error("Failed to copy image URL: ", error);
  });
}

onMounted(() => {
  searchGifs("cat meme", currentPage);
  memeList.value.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  memeList.value.removeEventListener('scroll', handleScroll);
});

function handleScroll() {
  const nearBottom = memeList.value.offsetHeight + memeList.value.scrollTop >= memeList.value.scrollHeight - 500;
  if (nearBottom) {
    nextPage();
  }
}

</script>

<template>

  <div class="bg-zinc-800 w-screen rounded-2xl p-4 flex flex-col items-center">
    <h1 class="text-3xl font-bold text-primary mb-1.5">Nyang Meme</h1>
    <h1 class="text-white mb-3">이미지 클릭을 통해 복사하실 수 있습니다!</h1>
    <div class="bg-zinc-700 rounded-md sm:w-6/12 w-full h-[40rem] p-8 overflow-auto memeList" ref="memeList">
      <div class="masonry">
        <img v-for="(gif, index) in gifs" :id="`cmg_${index}`" :src="gif.media_formats.gif.url" alt="" @click=copyImageUrl(gif.media_formats.gif.url)>
      </div>
    </div>
  </div>

</template>

<style>
.masonry {
  max-width: 100%;  /* 컨테이너의 최대 너비를 설정 */
  margin: auto;  /* 컨테이너를 화면 중앙에 위치시킴 */
  column-count: 5;
  column-gap: 0.5em;  /* 이미지 간의 간격을 줄임 */
}

.masonry img {
  width: 100%;
  max-height: 100%;
  object-fit: cover;
  margin-top: 0.5em;
}

.memeList{
  overflow-y: scroll;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.memeList::-webkit-scrollbar {
  display:none;
}
</style>
