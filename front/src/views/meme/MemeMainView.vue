<script setup lang="ts">

import {ref} from "vue";

const limit = ref<number>();

function httpGetAsync(theUrl: string, callback: (responseText: string) => void ): void {
  const xmlHttp = new XMLHttpRequest();

  xmlHttp.onreadystatechange = function () {
    if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
      callback(xmlHttp.responseText);
    }
  };

  xmlHttp.open("GET", theUrl, true);

  xmlHttp.send(null)
}

function tenorCallback_search(responsetext: string): void {
  const response_objects = JSON.parse(responsetext);
  const top_10_gifs = response_objects["results"];

  for(let i = 0; i < top_10_gifs.length; i++) {
    const cmgif = document.getElementById(`cmg_${i}`) as HTMLImageElement;
    cmgif.src = top_10_gifs[i]["media_formats"]["gif"]["url"];
  }
}

function grab_data(): void {
  const apikey = "AIzaSyBOopogP9plIccrEkvlnutu5M4-Ws6tnt0";
  const clientkey = "NyangMeme";
  const lmt = 15;

  const search_term = "cat";
  const search_url = `https://tenor.googleapis.com/v2/search?q=${search_term}&key=${apikey}&client_key=${clientkey}&limit=${lmt}`;

  httpGetAsync(search_url, tenorCallback_search);
}

grab_data()

const cmgifs = document.getElementsByClassName("cmgs");

// Add types for the document object
declare global {
  interface Document {
    getElementById<T extends keyof HTMLElementTagNameMap>(
        id: T
    ): HTMLElementTagNameMap[T] | null;
  }
}

for(const cmgif of cmgifs){
  if (cmgif instanceof HTMLImageElement) {
    cmgif.setAttribute("style", "width:498px;height:372px;");
  }
}

</script>

<template>

  <div class="container content_area w-100 h-100 text-white text-center">
    <img v-for="index in 15" class="cmgs m-1" :key="`cmg_${index - 1}`" :id="`cmg_${index - 1}`" src="" alt="" style="width:220px;height:164px;">
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
</style>