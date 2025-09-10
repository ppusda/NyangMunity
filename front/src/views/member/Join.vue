<script setup lang="ts">
import {ref} from "vue";
import router from "@/router";
import axiosClient from "@/libs/axiosClient";

const email = ref("");
const password = ref("");
const passwordChk = ref("");
const birthday = ref("");
const nickname = ref("");

const joinConfirm = () => {
  if (password.value !== passwordChk.value) {
    alert("비밀번호가 다릅니다.");
  } else {
    axiosClient.post("/members/join", {
      email: email.value,
      password: password.value,
      birthday: birthday.value,
      nickname: nickname.value,
    }).then(() => {
      router.replace({name: "main"});
    });
  }
};
</script>

<template>
  <div class="px-4 py-12 bg-zinc-900 flex justify-center">
    <div class="w-full max-w-md">
      <div class="flex flex-col items-center mb-8">
        <img id="logo" class="h-20 mb-4" src="/src/assets/images/logo.png" alt="logo"/>
        <h1 class="text-3xl font-bold text-white text-center">냥뮤니티 회원가입</h1>
      </div>

      <form class="bg-zinc-800 p-6 rounded-md shadow-md space-y-4">
        <!-- 이메일 -->
        <div>
          <label for="email" class="text-white mb-1 block">아이디</label>
          <input
              type="email"
              id="email"
              v-model="email"
              placeholder="이메일을 입력해주세요."
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
          />
        </div>

        <!-- 비밀번호 -->
        <div>
          <label for="password" class="text-white mb-1 block">비밀번호</label>
          <input
              type="password"
              id="password"
              v-model="password"
              placeholder="비밀번호를 입력해주세요."
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
          />
        </div>

        <!-- 비밀번호 확인 -->
        <div>
          <label for="passwordChk" class="text-white mb-1 block">비밀번호 확인</label>
          <input
              type="password"
              id="passwordChk"
              v-model="passwordChk"
              placeholder="비밀번호를 재입력해주세요."
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
          />
        </div>

        <!-- 닉네임 -->
        <div>
          <label for="nickname" class="text-white mb-1 block">닉네임</label>
          <input
              type="text"
              id="nickname"
              v-model="nickname"
              placeholder="닉네임을 입력해주세요."
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
          />
        </div>

        <!-- 생년월일 -->
        <div>
          <label for="birthday" class="text-white mb-1 block">생년월일</label>
          <input
              type="date"
              id="birthday"
              v-model="birthday"
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white"
          />
        </div>

        <!-- 버튼 -->
        <div class="flex flex-col sm:flex-row justify-between gap-3 pt-4">
          <button
              type="button"
              class="btn btn-outline btn-error w-full sm:w-1/2 text-black p-3 rounded-md"
              @click="$router.go(-1)">
            취소
          </button>
          <button
              type="button"
              class="btn btn-outline btn-primary w-full sm:w-1/2 text-white p-3 rounded-md"
              @click="joinConfirm">
            완료
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
</style>
