<script setup lang="ts">

import {onMounted, ref} from "vue";

interface Gif {
  title: string;
  media_formats: {
    gif: {
      url: string;
    };
  };
}

const gifs = ref<Gif[]>([]);
const apikey = "AIzaSyBOopogP9plIccrEkvlnutu5M4-Ws6tnt0";
const clientkey = "NyangMeme";
const limit = 15;
let posKey: string[] = []
let currentPage = -1;

function getData(searchTerm: string, page: number): Promise<void> {
  const search_url = `https://tenor.googleapis.com/v2/search?q=${searchTerm}&key=${apikey}&client_key=${clientkey}&limit=${limit}&pos=${posKey[page]}`;

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
    gifs.value = searchResults;
  } catch (error) {
    console.error(error);
  }
}

onMounted(() => {
  searchGifs("cat meme", currentPage);
});

async function nextPage(): Promise<void> {
  currentPage++;
  imageLoading();
  await searchGifs("cat meme", currentPage);
}

async function prevPage(): Promise<void> {
  if (currentPage >= 0) {
    currentPage--;
    imageLoading();
    await searchGifs("cat meme", currentPage);
  }else{
    await searchGifs("cat meme", -1);
  }
}

async function imageLoading(): Promise<void> {
  for(let i=0; i<15; i++){
    gifs.value[i].media_formats.gif.url = "src/images/cat_loading.gif"
  }
}//https://gifer.com/en/Uond gif 사용

function copyImageUrl(url: string): void {
  navigator.clipboard.writeText(url).then(() => {
    showToast("Image Copied!")
  }, (error) => {
    console.error("Failed to copy image URL: ", error);
  });
}

function showToast(message: string): void {
  const toast = document.createElement('div');
  toast.textContent = message;
  toast.style.position = 'fixed';
  toast.style.bottom = '50%';
  toast.style.left = '50%';
  toast.style.transform = 'translateX(-50%)';
  toast.style.padding = '10px';
  toast.style.backgroundColor = '#333';
  toast.style.color = '#fff';
  toast.style.borderRadius = '5px';
  toast.style.opacity = '0.5';
  toast.style.transition = 'opacity 0.5s ease';

  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '1';
  }, 100);

  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 500);
  }, 1500);
}

</script>

<template>

  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area w-100">
      <img v-for="(gif, index) in gifs" class="cmgs m-1 h-100 w-100" :id="`cmg_${index - 1}`" :src="gif.media_formats.gif.url" alt="" @click=copyImageUrl(gif.media_formats.gif.url)>
      <div class="mt-2">
        <a class="clButton btn btn-secondary text-white m-1" @click="prevPage">이전페이지</a>
        <a class="clButton btn btn-primary text-white m-1" @click="nextPage">다음페이지</a>
      </div>
    </div>
  </div>

  <div>
    <b></b>
  </div>

</template>

<style scoped>
  .content_area {
    top: 1.5%;
    padding: 1vw;
  }

  .cmgs {
    max-width: 220px;
    max-height: 164px;
  }

  .content_area{
    overflow-y: scroll;
    max-height: 80vh;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  .content_area::-webkit-scrollbar {
    display:none;
  }

</style>