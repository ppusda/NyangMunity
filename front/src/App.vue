<script lang="ts">
  import Header from "./views/commons/Header.vue";
  import MainView from "./views/MainView.vue";

import "./assets/main.css";

export default {
  name: 'App',
  components: {
    Header,
    MainView
  }
}
</script>

<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900">
    <Header />
    <router-view />
  </div>
</template>

<style>

</style>