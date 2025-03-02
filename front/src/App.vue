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

  onMounted(() => {
    const user = localStorage.getItem('user');
    if (user) {
      const parsedUser = JSON.parse(user);
      store.dispatch('login', parsedUser);
    }
  });
</script>

<style>

</style>