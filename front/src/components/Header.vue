<script setup lang="ts">
import {computed} from 'vue';
import {useStore} from 'vuex';
import {logout} from "@/utils/account";

const store = useStore();

const isLogin = computed(() => !!store.state.memberId);
const nickname = computed(() => store.state.nickname);

</script>

<template>
  <header>
    <div class="sticky top-0 h-20 navbar bg-zinc-800 z-10">
      <div class="navbar-start">
        <router-link :to="{name: 'main'}" class="content-center btn btn-ghost"> <img id="logo" class="h-[60px]" src="/src/images/logo.png" alt="logo"> </router-link>
      </div>
      <div class="navbar-center">
      </div>
      <div class="navbar-end">
        <div v-if="isLogin" class="dropdown dropdown-end">
          <div tabindex="0" role="button" class="btn btn-ghost rounded-btn"> {{nickname}}님, 환영합니다!</div>
          <ul tabindex="0" class="menu menu-md dropdown-content mt-5 z-[1] p-3 shadow rounded-box w-52 bg-zinc-800">
            <li><router-link :to="{name: 'info'}"><i class="fa-solid fa-address-card" />마이페이지</router-link></li>
            <li><a @click="logout"><i class="fa-solid fa-door-closed" />로그아웃</a></li>
          </ul>
        </div>
        <div v-else>
          <router-link :to="{ name: 'login' }" class="btn btn-ghost text-sm">
            <i class="fa-solid fa-door-open" />로그인
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>

</style>