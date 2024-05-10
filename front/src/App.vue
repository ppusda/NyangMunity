<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header />
    <router-view />
  </div>
</template>

<script setup lang="ts">
  import Header from "@/components/Header.vue";
  import "./assets/main.css";
</script>

<style>

</style>