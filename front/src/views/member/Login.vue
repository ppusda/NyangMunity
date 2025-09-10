<script setup lang="ts">
import {ref} from "vue";
import {login} from "@/utils/account";
import router from "@/router";
import axiosClient from "@/libs/axiosClient";

const email = ref<string>("");
const password = ref<string>("");

const loginAction = async () => {
  if (await login(email.value, password.value)) {
    await router.replace({name: "main"});
  }
};

const requestKakaoLogin = () => {
  axiosClient.get("/auth/kakao/url").then((result) => {
    window.location.href = result.data;
  });
};

const requestGoogleLogin = () => {
  axiosClient.get("/auth/google/url").then((result) => {
    window.location.href = result.data;
  });
};
</script>

<template>
  <div class="min-h-screen px-4 py-10 flex flex-col items-center bg-zinc-900">
    <img id="logo" class="h-20 mb-6" src="/src/assets/images/logo.png" alt="logo"/>
    <h1 class="text-2xl sm:text-3xl font-bold text-white text-center mb-8">
      냥뮤니티에 로그인하세요!
    </h1>

    <form class="bg-zinc-800 p-6 rounded-md w-full max-w-sm shadow-md">
      <div class="flex flex-col mb-4">
        <label for="email" class="text-white mb-1">아이디</label>
        <input
            type="email"
            name="email"
            id="email"
            v-model="email"
            class="border border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
            placeholder="이메일을 입력해주세요."
        />
      </div>
      <div class="flex flex-col mb-6">
        <label for="password" class="text-white mb-1">비밀번호</label>
        <input
            type="password"
            name="password"
            id="password"
            v-model="password"
            class="border border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
            placeholder="비밀번호를 입력해주세요."
        />
      </div>
      <div class="flex flex-col space-y-2">
        <button
            type="button"
            @click="loginAction"
            class="btn btn-outline btn-primary w-full rounded-md text-white"
        >
          <i class="fa-solid fa-door-open mr-2"></i> 로그인
        </button>
        <div class="grid grid-cols-2 gap-x-2">
          <button
              type="button"
              class="btn btn-outline btn-warning w-full rounded-md"
              @click="requestKakaoLogin"
          >
            <i class="fa-solid fa-comment fa-flip-horizontal"></i>
          </button>
          <button
              type="button"
              class="btn btn-outline btn-ghost w-full rounded-md text-white"
              @click="requestGoogleLogin"
          >
            <i class="fa-brands fa-google"></i>
          </button>
        </div>
      </div>
    </form>

    <div class="bg-zinc-800 px-5 py-4 mt-6 rounded-md w-full max-w-sm text-center">
      <h6 class="text-white font-bold mb-3">처음 이용하시나요?</h6>
      <router-link
          to="/member/join"
          class="btn btn-outline btn-ghost w-full rounded-md text-white"
      >
        <i class="fa-solid fa-user-plus mr-2"></i> 회원가입
      </router-link>
    </div>
  </div>
</template>

<style scoped>
</style>
