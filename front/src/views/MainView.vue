<script setup lang="ts">
import {ref} from 'vue';
import axiosClient from "@/libs/axiosClient";
import type {PostLike} from "@/interfaces/type";

let likePostFlg = ref(false);
let likePost = ref<PostLike>({
  id: null,
  postImages: null,
  nickname: null,
  message: ""
});

axiosClient.get("/posts/likes").then((response) => {
  if (response.data.id !== null) {
    likePostFlg.value = true;
  } else {
    likePostFlg.value = false;
  }
  likePost.value = response.data;
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
            <router-link :to="{name: 'posts'}" class="btn btn-outline btn-ghost">
              <i class="fa-solid fa-arrow-right"></i>커뮤니티로 바로가기!
            </router-link>
          </div>
        </div>
      </div>
      <div class="hero min-w-min accent-neutral-900">
        <div class="hero-content flex-col lg:flex-row-reverse ml-5 p-36">
          <div>
            <div v-if="likePostFlg">
              <img
                  v-if="likePost.postImages && likePost.postImages.length > 0"
                  class="rounded"
                  id="main_img"
                  :src="likePost.postImages[0].url"
              />
              <router-link
                  class="btn btn-outline btn-ghost"
                  :to="{name: 'read', params: {postId: likePost.id}}"
              >
                {{ likePost.message }}
              </router-link>
            </div>
            <div v-else class="w-4/6 text-center">
              <img class="rounded-2xl" src="/assets/images/cat_loading.gif"/>
              <p class="py-3">{{ likePost.message }}</p>
              <p class="btn btn-outline btn-ghost">
                메인 페이지의 주인공에 도전해보세요!
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>