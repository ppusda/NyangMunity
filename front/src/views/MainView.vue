<script setup lang="ts">
import axios from "axios";
import {reactive, ref} from 'vue';

const check = reactive({ value: false });
const post = ref({
  bid: 0,
  boardImages: <any>[],
  nickName: ""
});

let rdNum = reactive({ value: 0 });

axios.post("/nm/boards/like", ).then(response => {
  check.value = true;
  post.value = response.data;
  rdNum.value = Math.floor(Math.random() * post.value.boardImages.length);
}).catch(() => {
  check.value = false;
});

</script>

<template>
  <div class="h-100">
    <div class="bg-zinc-800 w-screen rounded-2xl p-8">
      <div class="flex flex-col items-center justify-center">
        <div v-if="check.value === true">
          <img class="rounded" id="main_img" :src="`data:image/jpeg;base64,${post.boardImages[rdNum.value].imageBytes}`"/>
        </div>
        <div v-else>
          <img class="rounded-2xl" src="/assets/images/cat_loading.gif"/>
        </div>
      </div>
      <div class="flex flex-col items-center justify-center mt-5">
        <template v-if="check.value === true">
          <router-link :to="{name: 'read', params: {postId: post.bid}}">가장 인기많은 글은 {{ post.nickName }} 님의 글 입니다!</router-link>
        </template>
        <template v-else>
          <a class="btn btn-outline btn-primary">
            <i class="fa-solid fa-arrow-right"></i>
            <router-link :to="{name: 'boards'}">커뮤니티로 바로가기!</router-link>
          </a>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
