<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import router from "@/router";

const posts = ref([]);

axios.get("/nm/boards?page=1&size=5").then((response) => {
  console.log(response)
  response.data.forEach((res: any) => {
    posts.value.push(res);
  });
});

const moveToRead = () => {
  router.push({name: "read"});
}
</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div>
        <router-link :to="{name: 'read', params: {postId: post.id}}">{{post.title}}</router-link>
      </div>
      <div>
        {{post.content}}
      </div>
    </li>
  </ul>
</template>

<style scoped>

</style>