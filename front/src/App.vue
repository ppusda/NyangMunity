<script lang="ts">
import Header from './views/commons/Header.vue';
import HomeView from './views/HomeView.vue';
import Footer from "./views/commons/Footer.vue";

import "./assets/main.css";

export default {
  name: 'App',
  components: {
    Header,
    HomeView,
    Footer
  }
}
</script>

<template>
  <div id="app" class="flex flex-col h-screen justify-between">
    <Header/>
    <router-view/>
    <Footer/>
  </div>
</template>

<style>

</style>