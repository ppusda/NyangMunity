<script>
import { RouterLink, RouterView } from "vue-router";
import MainHeader from './views/commons/MainHeader.vue';
import HomeView from './views/HomeView.vue';
import MainFooter from "./views/commons/MainFooter.vue";

export default {
  name: 'App',
  components: {
    MainHeader,
    HomeView,
    MainFooter
  }
}
</script>

<template>
  <div id ="app">
    <MainHeader/>
    <router-view/>
    <MainFooter/>
  </div>
</template>

<style>
</style>