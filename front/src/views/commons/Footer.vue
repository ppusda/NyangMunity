<script setup lang="ts">

import { ref } from 'vue'

const GITHUB_URL = ref("https://github.com/ppusda");
const BLOG_URL = ref("https://ppusda.tistory.com");
</script>

<template>
  <footer>
    <div class="p-1.5 pr-3 pl-3 text-neutral-content bg-zinc-800 rounded-t-2xl">
      <div class="flex flex-row justify-between items-center">
        <div class="justify-start">
          <b class="text-sm"> &copy; Nyangmunity Edited By ppusda</b>
        </div>
        <div class="flex flex-row justify-end">
          <a class="btn btn-ghost" :href="GITHUB_URL"><i class="fa-brands fa-github fa-2xl"></i></a>
          <a class="btn btn-ghost" :href="BLOG_URL"><i class="fa-solid fa-blog fa-2xl"></i></a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style>

</style>