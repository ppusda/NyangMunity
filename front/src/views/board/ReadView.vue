<script setup lang="ts">

import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
});

const post = ref({
  id: 0,
  title: "",
  content: "",
});

const moveToEdit = () => {
  router.push({name: "edit", params: { postId: props.postId }})
}

onMounted( () => {
  axios.get(`/nm/boards/${props.postId}`).then((response) => {
    post.value = response.data;
  });
})
</script>

<template>
  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area" method="post">
      <h2>{{post.title}}</h2>
      <div>{{post.content}}</div>

      <el-button type="warning" @click="moveToEdit()">수정</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>