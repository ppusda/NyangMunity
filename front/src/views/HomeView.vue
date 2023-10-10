<script setup lang="ts">
import axios from "axios";
import {defineComponent, reactive, ref} from 'vue';

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
  <div class="container login_page w-100 h-100 text-white text-center">
    <div class="content_area">
      <div class="overlay d-flex justify-content-center align-content-center w-100 h-100 cat_page">
        <template v-if="check.value === true">
          <img id="main_img" :src="`data:image/jpeg;base64,${post.boardImages[rdNum.value].imageBytes}`"/>
        </template>
        <template v-else>
          <img class="thumbnail" src="/assets/images/cat_loading.gif"/>
        </template>
      </div>
      <div class="today_cat">
        <template v-if="check.value === true">
          <router-link :to="{name: 'read', params: {postId: post.bid}}">가장 인기많은 글은 {{ post.nickName }} 님의 글 입니다!</router-link>
        </template>
        <template v-else>
          <router-link :to="{name: 'boards'}">커뮤니티 바로가기</router-link>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>

  #main_img{
    max-height: 60vh;
    object-fit: cover;
  }

  .content_area {
    top: 1.5%;
    width: 100%;
    height: 80%
  }

  .today_cat {
    margin: 1vw;
  }
</style>
