<script setup lang="ts">

import {defineProps, onMounted, reactive, ref} from "vue";
import axios from "axios";
import router from "@/router";

import { Carousel } from 'bootstrap';
import {useCookies} from "vue3-cookies";

let isMouseDown = ref(false);
let lastX = ref<number | null>(0);

const likeCheck = reactive({value: false});
const writerCheck = reactive({value: false});
const userCheck = reactive({value: false});
const nickName = reactive({ value: "" });

const myCarouselElement = ref<HTMLElement|null>(null);
const { cookies } = useCookies();
const carousel = ref<Carousel>();

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
  boardImages: <any>[],
  createDate: "",
  writer: ""
});

const moveToEdit = () => {
  axios.post("/nm/user/check").then(() => {
    router.push({name: "edit", params: { postId: props.postId }})
  }).catch(error => {
    if (error.response) {
      alert(error.response.data.message);
    }
  });
}

function boardLikeCheck() {
  axios.post(`/nm/boards/like/check/${props.postId}`).then(response => {
    likeCheck.value = response.data;
  });
}
boardLikeCheck();

function boardLike() {
  axios.post(`/nm/boards/like/${props.postId}`).then(() => {
    boardLikeCheck();
  });
}

function handleMouseUp() {
  isMouseDown.value = false;
}

function handleMouseDown() {
  isMouseDown.value = true;
}

function handleMouseMove(event: MouseEvent) {
  if (isMouseDown.value) {
    const {clientX} = event;
    if (lastX.value) {
      if (lastX.value - clientX > 1) {
        showNextSlide();
      } else if (clientX - lastX.value > 1) {
        showPrevSlide();
      }
    }
    lastX.value = clientX;
  }else{
    const {clientX} = event;
    lastX.value = clientX;
  }
}

onMounted( async () => {
  await axios.get(`/nm/boards/${props.postId}`).then(response => {
    post.value = response.data;
    writerCheck.value = response.data.writerCheck;
  });

  if(cookies.get('SESSION') && cookies.get('SESSION') !== "") {
    await axios.post("/nm/user/check", ).then(response => {
      nickName.value = response.data;
      userCheck.value = true;
    })
  }

  myCarouselElement.value = document.querySelector('#imageSlider');
  if (myCarouselElement.value) {
    carousel.value = new Carousel(myCarouselElement.value);
  }
});

function showNextSlide() {
  carousel.value?.next();
}

function showPrevSlide() {
  carousel.value?.prev();
}

</script>

<template>
  <html @mouseup="handleMouseUp">
    <div class="container w-100 h-100 text-white text-center" @mouseup="handleMouseUp">
      <div class="content_area scroll" method="post">
        <h2>{{post.title}}</h2>
        <h6>작성자: {{post.writer}}</h6>
        <hr>
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
                 :class="{'active': index === 0}"
                 @mousedown="handleMouseDown"
                 @mousemove="handleMouseMove"
                 @mouseup="handleMouseUp">
              <img class="thumbnail" :id="`${boardImage.id}`" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" @mousedown.prevent />
            </div>
            <div v-else class="carousel-item active"
                 @mousedown="handleMouseDown"
                 @mousemove="handleMouseMove"
                 @mouseup="handleMouseUp">
              <img class="thumbnail" id="0" src="/assets/images/cat_loading.gif" @mousedown.prevent/>
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
        <hr>
        <div class="d-inline-flex" v-if="userCheck && userCheck.value === true">
          <a class="clButton btn btn-secondary text-white m-1" @click="$router.replace({name: 'boards'})">목록으로</a>
          <div v-if="writerCheck && writerCheck.value === true">
            <a class="clButton btn btn-primary text-white m-1" @click="moveToEdit()">수정</a>
          </div>
          <div v-if="likeCheck && likeCheck.value === true">
            <a class="clButton btn btn-danger text-white m-1" @click="boardLike()">❤</a>
          </div>
          <div v-else>
            <a class="clButton btn btn-danger text-white m-1" @click="boardLike()">🤍</a>
          </div>
        </div>
        <div class="d-inline-flex" v-else>
          <a class="clButton btn btn-secondary text-white m-1" @click="$router.replace({name: 'boards'})">목록으로</a>
          <a class="clButton btn btn-danger text-white m-1" data-bs-toggle="modal" data-bs-target="#boardLikeModal">🤍</a>

          <div class="modal fade" id="boardLikeModal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title text-black" >좋아요</h5>
                </div>
                <div class="modal-body text-black">
                  <b>로그인이 필요합니다.</b>
                </div>
                <div class="modal-footer">
                  <a type="button" class="clButton btn btn-primary text-white m-1" data-bs-dismiss="modal">확인</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </html>
</template>

<style scoped>
  .content_area {
    top: 1.5%;
    padding: 1vw;
    width: 100%;
    overflow-y: scroll;
    max-height: 83vh;
  }

  .thumbnail{
    max-height: 60vh;
    object-fit: cover;
  }

  .carousel-item {
    position: static;
  }

  .scroll::-webkit-scrollbar {
    display: none;
  }

  #imageSlider {
    margin-top: 1vw;
  }
</style>