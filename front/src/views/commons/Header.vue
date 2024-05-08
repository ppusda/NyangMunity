<script setup lang="ts">
import {onMounted, reactive} from 'vue';
import axios from "axios";

let nickName = reactive({ value: "" });


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
            <li><router-link :to="{name: 'main'}"><i class="fa-solid fa-house"></i>메인 페이지</router-link></li>
            <li><router-link :to="{name: 'boards'}"><i class="fa-solid fa-comments"></i>커뮤니티</router-link></li>
            <li><router-link :to="{name: 'meme'}"><i class="fa-solid fa-image"></i>고양이 밈</router-link></li>
          </ul>
        </div>
      </div>
      <div class="navbar-center">
        <router-link :to="{name: 'main'}" class="content-center h-100 btn btn-ghost"> <img id="logo" class="h-[60px]" src="/src/images/logo.png" alt="logo"> </router-link>
      </div>
      <div class="navbar-end">
        <div v-if="nickName && nickName.value" class="dropdown dropdown-end">
          <div tabindex="0" role="button" class="btn btn-ghost rounded-btn"> 님, 환영합니다!</div>
          <ul tabindex="0" class="menu menu-md dropdown-content mt-5 z-[1] p-3 shadow rounded-box w-52 bg-zinc-800">
            <li><router-link :to="{name: 'info'}"><i class="fa-solid fa-address-card"></i>마이페이지</router-link></li>
            <li><i class="fa-solid fa-door-closed"></i>로그아웃</li>
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