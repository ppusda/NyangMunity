<script setup lang="ts">

import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
  imageId: {
    type: [Number, String],
    require: true,
  }
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

function imageLike() {
  axios.post(`/nm/image/like/${props.imageId}`).then(() => {
  });
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


      <div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="true">
        <div class="carousel-indicators">
          <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
          <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="1" aria-label="Slide 2"></button>
          <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="2" aria-label="Slide 3"></button>
        </div>
        <div class="carousel-inner">
          <div class="carousel-item active" v-if="post.boardImages && post.boardImages.length > 0" v-for="boardImage in post.boardImages">
            <img class="thumbnail d-block w-100" :id="`${boardImage.id}`" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
          </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="prev">
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="next">
          <span class="carousel-control-next-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Next</span>
        </button>
      </div>

      <div>
        <a class="clButton btn btn-secondary text-white m-1" @click="$router.go(-1)">취소</a>
        <a class="clButton btn btn-primary text-white m-1" @click="moveToEdit()">수정</a>
        <a class="clButton btn btn-danger text-white m-1" @click="imageLike()">❤</a>
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