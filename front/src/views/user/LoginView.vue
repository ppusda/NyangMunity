<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const email = ref("");
const password = ref("");

const { VITE_APP_KAKAO_CLIENT_KEY } = import.meta.env;
const { VITE_APP_KAKAO_REDIRECT_URL } = import.meta.env;

const login = function () {
  axios.post("/nm/user/login", {
    email: email.value,
    password: password.value
  }).then(() => {
    router.replace({name: "main"})
        .then(() => router.go(0))
  }).catch(error => {
    if(error.response){
      alert(error.response.data.message);
    } else {
      alert("계정이 올바르지 않습니다.");
    }
  });
}

</script>

<template>
  <input value="true" type="text" name="loginState" id="loginState" hidden="true"/>

  <div class="h-100">
    <div class="bg-zinc-800 w-screen rounded-2xl p-16">
      <div class="flex flex-col justify-center items-center">
        <h1 class="text-3xl font-bold text-white mb-12">로그인</h1>
        <form class="bg-zinc-700 p-5 rounded-md sm:w-6/12 w-full">
          <div class="flex flex-col m-3">
            <label for="email" class="text-white mb-1">아이디</label>
            <input type="email" name="email" id="email" v-model="email" class="input input-bordered border-zinc-500 bg-zinc-700 w-full p-3 rounded-md text-white" placeholder="이메일을 입력해주세요.">
          </div>
          <div class="flex flex-col m-3">
            <label for="password" class="text-white mb-1">비밀번호</label>
            <input type="password" name="password" id="password" v-model="password" class="input input-bordered border-zinc-500 bg-zinc-700 w-full p-3 rounded-md text-white" placeholder="비밀번호를 입력해주세요.">
          </div>
          <div class="flex flex-col justify-center mt-10 m-3">
            <a @click="login" class="btn btn-outline btn-primary w-full rounded-md text-white mb-1.5"><i class="fa-solid fa-door-open"></i> 로그인</a>
            <a class="btn btn-outline btn-warning w-full p-3 rounded-md text-black" :href="`https://kauth.kakao.com/oauth/authorize?client_id=${VITE_APP_KAKAO_CLIENT_KEY}&redirect_uri=${VITE_APP_KAKAO_REDIRECT_URL}&response_type=code`">
              <i class="fa-solid fa-comment fa-flip-horizontal"></i>
              카카오 로그인
            </a>
          </div>
          <div class="mt-3 mb-3 text-white m-5 text-center">
            <p>------------- 처음이신가요? -------------</p>
          </div>
          <div class="flex flex-col m-3">
            <a class="btn btn-outline btn-ghost w-full p-3 rounded-md text-white">
              <i class="fa-solid fa-user-plus"></i>
              <router-link to="/user/join">회원가입</router-link>
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>

</template>

<style scoped>

</style>