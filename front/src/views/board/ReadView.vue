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

      <div id="imageSlider" class="carousel slide" data-bs-ride="true">
        <div class="carousel-indicators">
          <button v-for="index in post.boardImages.length"
                  :key="index"
                  type="button"
                  data-bs-target="#imageSlider"
                  :data-bs-slide-to="index-1"
                  :class="{'active': index-1 === 0}"
                  aria-current="true">
          </button>
        </div>
        <div class="carousel-inner">
          <div v-if="post.boardImages && post.boardImages.length > 0"
               v-for="(boardImage, index) in post.boardImages"
               class="carousel-item"
               :class="{'active': index === 0}">
            <img class="thumbnail d-block w-100" :id="`${boardImage.id}`" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
          </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#imageSlider" data-bs-slide="prev">
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#imageSlider" data-bs-slide="next">
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
    top: 1.5%;
    padding: 1vw;
    max-width: 90vw;
  }
  .thumbnail{
    max-height: 60vh;
    object-fit: cover;
  }
</style>