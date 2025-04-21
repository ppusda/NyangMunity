<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header/>
    <router-view/>
  </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {isTokenExist, logout, saveMemberInfo} from "@/utils/account";

import Header from "@/components/Header.vue";
import "./assets/main.css";
import {warningToast} from "@/libs/toaster";


onMounted(() => {
  if (isTokenExist()) {
    const memberInfo = localStorage.getItem("member");
    saveMemberInfo(JSON.parse(memberInfo!!));
  } else {
    logout();
    warningToast("토큰이 만료되었습니다.")
  }
});
</script>

<style>

</style>