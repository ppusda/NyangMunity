<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import router from "@/router";

const email = ref("")
const password = ref("")
const passwordChk = ref("")
const birthday = ref("")
const nickname = ref("")

const joinConfirm = function () {
  if(password.value != passwordChk.value){
    alert("비밀번호가 다릅니다.")
  }else{
    axios.post("/nm/member/join", {
      email: email.value,
      password: password.value,
      birthday: birthday.value,
      nickname: nickname.value,
    }).then(() => {
      router.replace({name: "main"})
    })
  }
}

</script>

<template>
  <div class="w-screen rounded-2xl p-12">
    <div class="flex flex-col justify-center items-center">
      <img id="logo" class="h-[80px]" src="/src/images/logo.png" alt="logo">
      <h1 class="text-3xl font-bold text-white mb-12">냥뮤니티 회원가입</h1>
      <form class="bg-zinc-800 p-5 rounded-md sm:w-[25rem] w-[26rem]">
        <div class="flex flex-col m-3">
          <label for="email" class="text-white mb-1">아이디</label>
          <input type="email" name="email" id="email" v-model="email" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="이메일을 입력해주세요.">
        </div>
        <div class="flex flex-col m-3">
          <label for="password" class="text-white mb-1">비밀번호</label>
          <input type="password" name="password" id="password" v-model="password" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="비밀번호를 입력해주세요.">
        </div>
        <div class="flex flex-col m-3">
          <label for="passwordChk" class="text-white mb-1">비밀번호 확인</label>
          <input type="password" name="passwordChk" id="passwordChk" v-model="passwordChk" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="비밀번호를 재입력해주세요.">
        </div>
        <div class="flex flex-col m-3">
          <label for="nickname" class="text-white mb-1">닉네임</label>
          <input type="text" name="nickname" id="nickname" v-model="nickname" class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-3 rounded-md text-white" placeholder="닉네임을 입력해주세요.">
        </div>
        <div class="flex flex-row justify-center mt-10 m-3 w-full gap-2 pr-5">
          <div class="flex-1">
            <a class="btn btn-outline btn-error p-3 rounded-md text-black w-full" @click="$router.go(-1)">취소</a>
          </div>
          <div class="flex-1">
            <a class="btn btn-outline btn-primary p-3 rounded-md text-white w-full" @click="joinConfirm">완료</a>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>

</style>
