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
  searchGifs("cat", currentPage);
});

async function nextPage(): Promise<void> {
  currentPage++;
  await searchGifs("cat", currentPage);
}

async function prevPage(): Promise<void> {
  if (currentPage >= 0) {
    currentPage--;
    await searchGifs("cat", currentPage);
  }else{
    await searchGifs("cat", -1);
  }
}

</script>

<template>

  <div class="container content_area w-100 h-100 text-white text-center">
    <img v-for="(gif, index) in gifs" class="cmgs m-1 h-100 w-100" :id="`cmg_${index - 1}`" :src="gif.media_formats.gif.url" alt="">
    <div class="mt-2">
      <a class="clButton btn btn-secondary text-white m-1" @click="prevPage">이전페이지</a>
      <a class="clButton btn btn-primary text-white m-1" @click="nextPage">다음페이지</a>
    </div>
  </div>

</template>

<style scoped>
  .content_area {
    display: inline-block;
    position: relative;
    top: 5%;
    background: #333;
    padding: 1vw;
    border-radius: 15px;
  }

  .cmgs {
    max-width: 220px;
    max-height: 164px;
  }
</style>