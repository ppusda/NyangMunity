<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header/>
    <router-view/>
  </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {isTokenExist, saveMemberInfo} from "@/utils/account";

import Header from "@/components/Header.vue";
import "./assets/main.css";


onMounted(() => {
  if (isTokenExist()) {
    const memberInfo = localStorage.getItem("member");
    saveMemberInfo(JSON.parse(memberInfo!!));
  }
});
</script>

<style>

</style>