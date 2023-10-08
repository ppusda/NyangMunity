<script setup lang="ts">

import axios from "axios";
import {onMounted, reactive, ref} from "vue";
import router from "@/router";
import paginate from "vuejs-paginate-next";

const posts = reactive<any[]>([]);

const page = reactive({ value: 1 });
const pageCount = 5;
const totalPage = reactive({ value: 0 });

const getWriteTime = (time: any) => {
  let answer = "";

  let now = new Date();
  let writeTime = new Date(time);
  let calc = Math.floor((now.getTime() - writeTime.getTime()) / 1000);

  answer = calc + "초 전";
  if(calc >= 60) {
    calc = Math.floor(calc / 60);
    answer = calc + "분 전";
    if(calc >= 60) {
      calc = Math.floor(calc / 60);
      answer = calc + "시간 전";
      if(calc >= 24) {
        calc = Math.floor(calc / 24);
        answer = calc + "일 전";
        if(calc >= 30) {
          calc = Math.floor(calc / 30);
          answer = calc + "달 전";
          if(calc >= 12) {
            Math.floor(calc / 12);
            answer = calc + "년 전";
          }
        }
      }
    }
  }

  return answer;

}

const movePage = (pageValue: any) => {
  axios.get("/nm/boards?page="+pageValue+"&size="+pageCount, ).then(response => {
    totalPage.value = Math.ceil(response.data.totalCnt / pageCount);
    posts.splice(0, posts.length);
    response.data.boardList.forEach((res: any) => {
      posts.push(res);
    });
  });
}

movePage(page.value);

const moveToWrite = () => {
  axios.post("/nm/user/check").then(() => {
      router.push({ name: "write" });
  }).catch(error => {
      if (error.response) {
        router.push({ name: "login" })
      }
  });
}

</script>

<template>
  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area w-100">
      <ul class="boardList list-group">
        <li class="board list-group-item" v-for="post in posts" :key="post.id">
          <div>
            <h3>
              <router-link :to="{name: 'read', params: {postId: post.id}}">{{post.title}}</router-link>
            </h3>
            <h6>작성자: {{post.writer}} - {{getWriteTime(post.createDate)}}</h6>
          </div>
          <div class="d-inline-flex">
            <div class="d-flex justify-content-center align-items-center" v-if="post.boardImages && post.boardImages.length > 0" v-for="(boardImage, index) in post.boardImages.slice(0, Math.min(3, post.boardImages.length))">
              <div class="thumbnailDiv" v-if="index <= 1">
                <img class="thumbnail" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
              </div>
              <div v-else>
                <div class="thumbnail more-images">+{{ post.boardImages.length - 2 }}</div>
              </div>
            </div>
            <div class="d-inline" v-else>
              <img class="thumbnail" src="/assets/images/cat_loading.gif"/>
            </div>
          </div>
        </li>
      </ul>
      <div class="d-inline-flex pagination-container">
        <paginate
          :pageCount="totalPage.value"
          :containerClass="'pagination'"
          :clickHandler="movePage">
        </paginate>
      </div>
      <a class="clButton btn btn-primary text-white m-3" @click="moveToWrite()">새로 글 쓰기</a>
    </div>
  </div>
</template>

<style scoped>
.content_area {
  max-height: 80vh;
  top: 2%;
}

.thumbnail{
  max-width: 128px;
  max-height: 128px;
}

.boardList{
  overflow-y: scroll;
  max-height: 63vh;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.boardList::-webkit-scrollbar {
  display:none;
}

.content_area{
  padding: 3vw 6vw;
}

.clButton{
  color: white;
}

.more-images{
  position: absolute;
  justify-content: center;
  align-items: center;
  display: flex;
  color: white;
  bottom: 4.5%;
  left: 57.5%;
  padding: 5px 10px;
  background: rgba(0, 0, 0, 0.5);
}

.thumbnailDiv{
  width: 128px;
}

</style>