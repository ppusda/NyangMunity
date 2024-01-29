<script setup lang="ts">
import {onMounted, reactive} from 'vue';
import { useCookies } from 'vue3-cookies';
import axios from "axios";

let nickName = reactive({ value: "" });
const { cookies } = useCookies();

onMounted(async () => {
  if(cookies.get('SESSION') && cookies.get('SESSION') !== "") {
    await axios.post("/nm/user/check", ).then(response => {
        nickName.value = response.data;
    })
  }
});

</script>

<template>
  <header>
    <div class="sticky top-0 h-20 navbar bg-zinc-800 z-10 rounded-b-2xl">
      <div class="navbar-start">
        <div class="dropdown">
          <label tabindex="0" class="btn btn-ghost btn-circle">
            <i class="fa-solid fa-bars"></i>
          </label>
          <ul tabindex="0" class="menu menu-md dropdown-content mt-5 z-[1] p-3 shadow rounded-box w-52 bg-zinc-800">
            <li><a><i class="fa-solid fa-house"></i><router-link :to="{name: 'home'}">메인 페이지</router-link></a></li>
            <li><a><i class="fa-solid fa-comments"></i><router-link :to="{name: 'boards'}">커뮤니티</router-link></a></li>
            <li><a><i class="fa-solid fa-image"></i><router-link :to="{name: 'meme'}">고양이 밈</router-link></a></li>
          </ul>
        </div>
      </div>
      <div class="navbar-center">
        <a class="content-center h-100 btn btn-ghost">
          <router-link :to="{name: 'home'}"> <img id="logo" class="h-[60px]" src="/src/images/logo.png" alt="logo"> </router-link>
        </a>
      </div>
      <div class="navbar-end">
        <div v-if="nickName && nickName.value" class="dropdown dropdown-end">
          <div tabindex="0" role="button" class="btn btn-ghost rounded-btn"> 님, 환영합니다!</div>
          <ul tabindex="0" class="menu menu-md dropdown-content mt-5 z-[1] p-3 shadow rounded-box w-52 bg-zinc-800">
            <li><a><i class="fa-solid fa-address-card"></i>마이페이지</a></li>
            <li><a><i class="fa-solid fa-door-closed"></i>로그아웃</a></li>
          </ul>
        </div>
        <div v-else>
          <a class="btn btn-ghost text-sm">
            <i class="fa-solid fa-door-open"></i>
            <RouterLink to="/user/login">로그인</RouterLink>
          </a>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>

</style>