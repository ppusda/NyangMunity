<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header />
    <router-view />
  </div>
</template>

<script setup lang="ts">
  import {warningToast} from "@/libs/toaster";
  import {onMounted} from "vue";
  import {logout} from "@/utils/account";

  import store from "@/stores/store";

  import Header from "@/components/Header.vue";
  import "./assets/main.css";
  import axiosClient from "@/libs/axiosClient";


  onMounted(() => {
    const member = localStorage.getItem('member');
    if (member) {
      axiosClient.get("/members/check").then(response => {
        if (response.data.result) {
          const memberData = {
            id: response.data.memberId,
            nickname: response.data.nickname,
            isLogin: true
          };

          store.dispatch('login', memberData);
        } else { // 토큰 재검증 및 로그인 갱신 필요
          warningToast("재검증 필요.");
          store.dispatch('logout');
        }
      }).catch(() => {
        warningToast("로그인이 만료되었습니다.");
        logout();
      });
    }
  });
</script>

<style>

</style>