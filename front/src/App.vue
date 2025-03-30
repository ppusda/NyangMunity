<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header />
    <router-view />
  </div>
</template>

<script setup lang="ts">
  import Header from "@/components/Header.vue";
  import "./assets/main.css";
  import {onMounted} from "vue";
  import store from "@/stores/store";
  import axios from "axios";
  import {warningToast} from "@/utils/toaster";

  onMounted(() => {
    const user = localStorage.getItem('user');
    if (user) {
      axios.get("/nm/members/check", ).then(response => {
        if (response.data.result) {
          const userData = {
            id: response.data.memberId,
            nickname: response.data.nickname,
            isLogin: true
          };

          store.dispatch('login', userData);
          localStorage.setItem('user', JSON.stringify(userData));
        } else { // 토큰 재검증 및 로그인 갱신 필요
          store.dispatch('logout');
        }
      }).catch(() => {
        warningToast("로그인이 만료되었습니다.");
        store.dispatch('logout');
      });
    }
  });
</script>

<style>

</style>