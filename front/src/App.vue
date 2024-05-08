<script lang="ts">
  import Header from "./views/commons/Header.vue";
  import MainView from "./views/MainView.vue";
  import Footer from "./views/commons/Footer.vue";

import "./assets/main.css";

export default {
  name: 'App',
  components: {
    Header,
    MainView,
    Footer
  }
}
</script>

<template>
  <div id="app" class="flex flex-col h-screen bg-zinc-900 justify-between">
    <Header />
    <router-view />
    <Footer />
  </div>
</template>

<style>

</style>