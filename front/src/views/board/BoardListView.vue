<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";

const posts = ref([]);

axios.get("/nm/boards?page=1&size=5").then((response) => {
  console.log(response)
  response.data.forEach((res: any) => {
    posts.value.push(res);
  });
});
</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div>
        {{post.title}}
      </div>
      <div>
        {{post.content}}
      </div>
    </li>
  </ul>
</template>

<style scoped>

</style>