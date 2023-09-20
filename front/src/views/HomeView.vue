<script lang="ts">
import axios from "axios";
import { defineComponent } from 'vue';

export default defineComponent ({
  name: 'home',
  methods: {
    community: function () {
      axios.get("/nm/community");
    }
  }
})
</script>

<template>
  <div class="container login_page w-100 h-100 text-white text-center">
    <div class="content_area">
      <div class="overlay d-flex justify-content-center align-content-center w-100 h-100 cat_page">
        <img id="main_img" src="/src/images/computer_cat.gif"/>
      </div>
      <div class="today_cat">
        <a> 오늘의 고양이 NyangMunity!</a>
      </div>
    </div>
  </div>
</template>

<style scoped>

  #main_img{
    max-height: 60vh;
    object-fit: cover;
  }

  .content_area {
    top: 1.5%;
    width: 100%;
    height: 80%
  }

  .today_cat {
    margin: 1vw;
  }
</style>
