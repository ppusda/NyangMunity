<script setup lang="ts">
import {reactive, ref} from 'vue';
import axiosClient from "@/libs/axiosClient";

const check = reactive({ value: false });
const post = ref({
  bid: 0,
  postImages: <any>[],
  nickName: ""
});

let rdNum = reactive({ value: 0 });

axiosClient.post("/posts/like", ).then(response => {
  check.value = true;
  post.value = response.data;
  rdNum.value = Math.floor(Math.random() * post.value.postImages.length);
}).catch(() => {
  check.value = false;
});

</script>

<template>
  <div class="flex flex-row h-min w-full content-center">
    <div class="flex flex-row text-white h-min w-full mt-5">
      <div class="hero min-w-min accent-neutral-900">
        <div class="hero-content flex-col lg:flex-row-reverse ml-5 p-36">
          <div class="flex-row">
            <h1 class="text-7xl font-bold">Nyangmunity</h1>
            <p class="py-6">당신이 가진 가장 귀여운 고양이 사진을 공유해보세요!</p>
            <router-link :to="{name: 'posts'}" class="btn btn-outline btn-ghost"><i class="fa-solid fa-arrow-right"></i>커뮤니티로 바로가기!</router-link>
          </div>
        </div>
      </div>
      <div class="hero min-w-min accent-neutral-900">
        <div class="hero-content flex-col lg:flex-row-reverse ml-5 p-36">
          <div>
            <div v-if="check.value === true">
              <img class="rounded" id="main_img" :src="`data:image/jpeg;base64,${post.postImages[rdNum.value].imageBytes}`"/>
              <router-link :to="{name: 'read', params: {postId: post.bid}}" class="btn btn-outline btn-ghost">가장 인기많은 글은 {{ post.nickName }} 님의 이미지 입니다!</router-link>
            </div>
            <div v-else class="w-4/6 text-center">
              <img class="rounded-2xl" src="/assets/images/cat_loading.gif"/>
              <p class="py-3">아직 오늘의 인기 이미지가 없습니다!</p>
              <p class="btn btn-outline btn-ghost">메인 페이지의 주인공에 도전해보세요!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
