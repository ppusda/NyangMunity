<script setup lang="ts">
import {onMounted, ref} from 'vue';
import axiosClient from "@/libs/axiosClient";
import type {PostLike} from "@/interfaces/type";

let likePostFlg = ref(false);
let likePost = ref<PostLike>({
  id: null,
  imageInfo: null,
  nickname: null,
  message: ""
});

async function getMaxLikePost() {
  try {
    const response = await axiosClient.get("/images/likes");
    if (response.data && response.data.imageInfo) {
      likePost.value = response.data;
      likePostFlg.value = true;
    } else {
      likePost.value.message = "아직 좋아요를 많이 받은 글이 없습니다!";
      likePostFlg.value = false;
    }
  } catch (error) {
    likePost.value.message = "이미지를 불러오는데 실패했습니다.";
    likePostFlg.value = false;
  }
}

onMounted(async () => {
  await getMaxLikePost();
});
</script>

<template>
  <div class="min-h-screen flex flex-col items-center bg-zinc-900">
    <div class="flex flex-col lg:flex-row h-min w-full content-center">
      <div class="flex flex-col lg:flex-row text-white h-min w-full mt-5">
        <div class="hero min-w-min accent-neutral-900 w-full lg:w-1/2">
          <div class="hero-content flex-col lg:flex-row-reverse p-8 lg:p-36">
            <div class="flex-row text-center lg:text-left">
              <h1 class="text-4xl lg:text-7xl font-bold">Nyangmunity</h1>
              <p class="py-6 text-sm lg:text-base">당신이 가진 가장 귀여운 고양이 사진을 공유해보세요!</p>
            </div>
          </div>
        </div>
        <div class="hero min-w-min accent-neutral-900 w-full lg:w-1/2">
          <div class="hero-content flex-col lg:flex-row-reverse p-8 lg:p-36">
            <div class="w-full">
              <div v-if="likePostFlg && likePost.imageInfo" class="text-center">
                <img
                    class="rounded-2xl mx-auto w-full max-w-xs lg:max-w-none"
                    id="main_img"
                    :src="likePost.imageInfo.url"
                />
                <p class="py-3 text-sm lg:text-base">{{ likePost.message }}</p>
              </div>
              <div v-else class="w-full text-center mt-3">
                <img class="rounded-2xl mx-auto w-full max-w-xs lg:max-w-none" src="/assets/images/cat_loading.gif"/>
                <p class="py-3 text-sm lg:text-base">{{ likePost.message }}</p>
              </div>
              <div class="text-center mt-1">
                <p class="btn btn-outline btn-ghost text-sm lg:text-base">
                  <router-link :to="{name: 'posts'}">
                    메인 페이지의 주인공에 도전해보세요!
                  </router-link>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
