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
  boardImages: [],
  createDate: ""
});

const moveToEdit = () => {
  router.push({name: "edit", params: { postId: props.postId }})
}

onMounted( () => {
  axios.get(`/nm/boards/${props.postId}`).then((response) => {
    console.log(response.data)
    post.value = response.data;
  });
})
</script>

<template>
  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area" method="post">
      <h2>{{post.title}}</h2>
      <div>{{post.content}}</div>
      <div class="d-inline" v-if="post.boardImages && post.boardImages.length > 0" v-for="boardImage in post.boardImages">
        <img class="thumbnail" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
      </div>
      <div>
        <a class="clButton btn btn-secondary text-white m-1" @click="$router.go(-1)">취소</a>
        <a class="clButton btn btn-primary text-white m-1" @click="moveToEdit()">수정</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .content_area {
    padding: 1vw;
  }
  .thumbnail{
    max-width: 25vw;
    max-height: 60vh;
  }
</style>