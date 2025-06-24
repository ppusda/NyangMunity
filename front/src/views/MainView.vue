<script setup lang="ts">
import {onMounted, ref} from 'vue';
import axiosClient from "@/libs/axiosClient";
import type {PostLike} from "@/interfaces/type";

let likePostFlg = ref(false);
let likePost = ref<PostLike>({
  id: null,
  postImages: null,
  nickname: null,
  message: ""
});

function getMaxLikePost() {
  axiosClient.get("/images/likes").then((response) => {
    if (response.data.postImages && response.data.postImages.length > 0) {
      likePostFlg.value = true;
    } else {
      likePostFlg.value = false;
    }
    likePost.value = response.data;
  });
}

onMounted(async () => {
  await getMaxLikePost();
});
</script>

<template>
  <div class="flex flex-col lg:flex-row h-min w-full content-center">
    <div class="flex flex-col lg:flex-row text-white h-min w-full mt-5">
      <div class="hero min-w-min accent-neutral-900 w-full lg:w-1/2">
        <div class="hero-content flex-col lg:flex-row-reverse p-8 lg:p-36">
          <div class="flex-row text-center lg:text-left">
            <h1 class="text-4xl lg:text-7xl font-bold">Nyangmunity</h1>
            <p class="py-6 text-sm lg:text-base">당신이 가진 가장 귀여운 고양이 사진을 공유해보세요!</p>
            <router-link :to="{name: 'posts'}" class="btn btn-outline btn-ghost text-sm lg:text-base">
              <i class="fa-solid fa-arrow-right"></i>커뮤니티로 바로가기!
            </router-link>
          </div>
        </div>
      </div>
      <div class="hero min-w-min accent-neutral-900 w-full lg:w-1/2">
        <div class="hero-content flex-col lg:flex-row-reverse p-8 lg:p-36">
          <div class="w-full">
            <div v-if="likePostFlg" class="text-center">
              <img
                  v-if="likePost.postImages && likePost.postImages.length > 0"
                  class="rounded mx-auto w-full max-w-xs lg:max-w-none"
                  id="main_img"
                  :src="likePost.postImages[0].url"
              />
              <router-link
                  class="btn btn-outline btn-ghost mt-4 text-sm lg:text-base block"
                  :to="{name: 'read', params: {postId: likePost.id}}"
              >
                {{ likePost.message }}
              </router-link>
            </div>
            <div v-else class="w-full text-center">
              <img class="rounded-2xl mx-auto w-full max-w-xs lg:max-w-none" src="/assets/images/cat_loading.gif"/>
              <p class="py-3 text-sm lg:text-base">{{ likePost.message }}</p>
              <p class="btn btn-outline btn-ghost text-sm lg:text-base">
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