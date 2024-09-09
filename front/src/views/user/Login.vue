<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import store from "@/stores/store";
import router from "@/router";

const email = ref("");
const password = ref("");

const login = function () {
  axios.post("http://localhost:8080/user/login", {
    email: email.value,
    password: password.value
  }, { withCredentials: true })
  .then(response => {
    store.dispatch('login', {
      id: response.data.id,
      nickname: response.data.nickname
    });

    router.replace({ name: "main" });
  })
  .catch(error => {
    if (error.response) {
      alert(error.response.data.message);
    } else {
      alert("계정이 올바르지 않습니다.");
    }
  });
}

</script>

<template>
  <div class="w-screen rounded-2xl p-12 justify-center">
    <div class="flex flex-col justify-center items-center">
      <img id="logo" class="h-[80px]" src="/src/images/logo.png" alt="logo">
      <h1 class="text-3xl font-bold text-white mb-12">냥뮤니티에 로그인하세요!</h1>
      <form class="bg-zinc-800 p-5 rounded-md sm:w-[25rem] w-[26rem]">
        <div class="flex flex-col m-3">
          <label for="email" class="text-white mb-1">아이디</label>
          <input type="email" name="email" id="email" v-model="email" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="이메일을 입력해주세요.">
        </div>
        <div class="flex flex-col m-3">
          <label for="password" class="text-white mb-1">비밀번호</label>
          <input type="password" name="password" id="password" v-model="password" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="비밀번호를 입력해주세요.">
        </div>
        <div class="flex flex-col justify-center mt-8 m-3">
          <a @click="login" class="btn btn-outline btn-primary w-full rounded-md text-white mb-1.5"><i class="fa-solid fa-door-open"></i> 로그인</a>
          <div class="flex flex-row justify-center m-3 mb-0">
            <a class="btn btn-outline btn-warning p-3 rounded-md w-4/12 mr-3"><i class="fa-solid fa-comment fa-flip-horizontal"></i></a>
            <a class="btn btn-outline btn-ghost p-3 rounded-md text-white w-4/12 mr-3"><i class="fa-brands fa-google"></i></a>
            <a class="btn btn-outline btn-ghost p-3 rounded-md text-black w-4/12"><i class="fa-brands fa-github"></i></a>
          </div>
        </div>
      </form>
    </div>
    <div class="flex flex-col justify-center items-center mt-3">
      <div class="bg-zinc-800 px-5 py-3 rounded-md sm:w-[25rem] w-[26rem]">
        <div class="flex flex-col m-1.5 text-center">
          <h6 class="text-md font-bold text-white mb-3">처음 이용하시나요?</h6>
          <router-link to="/user/join" class="btn btn-outline btn-ghost w-full p-3 rounded-md text-white"><i class="fa-solid fa-user-plus"></i>회원가입</router-link>
        </div>
      </div>
    </div>
  </div>

</template>

<style scoped>

</style>