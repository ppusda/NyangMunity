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


  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area" method="post">
      <el-button type="primary"><router-link to="/boards/write">새로 글 쓰기</router-link></el-button>
      <ul>
        <li v-for="post in posts" :key="post.id">
          <div>
            <router-link :to="{name: 'read', params: {postId: post.id}}">{{post.title}}</router-link>
          </div>
          <div>
            {{post.content}}
          </div>
          <div>
            {{post.boardImages[0]}}
          </div>
        </li>
      </ul>
    </div>
  </div>

</template>

<style scoped>
</style>