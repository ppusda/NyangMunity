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
      <div class="overlay d-flex justify-content-center align-content-center w-100 h-100 cat_page">
        <img id="main_img" src="/src/images/computer_cat.gif"/>
      </div>
</template>

<style scoped>

  #main_img{
    max-width: 100%;
    height: 100%;
  }
</style>
