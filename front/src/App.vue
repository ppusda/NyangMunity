<script>
import { RouterLink, RouterView } from "vue-router";
import MainHeader from './views/commons/MainHeader.vue';
import HomeView from './views/HomeView.vue';
import MainFooter from "./views/commons/MainFooter.vue";

import main from"./assets/main.css";

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
  #app {
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.7);
  }

  html, body {
    height: 100%;
    margin: 0;
    padding: 0;
    overflow: hidden;
  }

  a {
    text-decoration: none !important;
    color: hsl(236, 99%, 70%);
    transition: 0.4s;
    cursor: pointer;
  }
</style>