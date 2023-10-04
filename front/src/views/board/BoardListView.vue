<script setup lang="ts">

import axios from "axios";
import {onMounted, reactive, ref} from "vue";
import router from "@/router";
import paginate from "vuejs-paginate-next";

const posts = reactive<any[]>([]);

const page = reactive({ value: 1 });
const pageCount = 5;
const totalPage = reactive({ value: 0 });

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

const clickCallback = (page: any) => {
  console.log(page)
}

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
        <li class="list-group-item" v-for="post in posts" :key="post.id">
          <div>
            <router-link :to="{name: 'read', params: {postId: post.id}}">{{post.title}}</router-link>
          </div>
          <div>
            {{post.content}}
          </div>
          <div class="d-inline" v-if="post.boardImages && post.boardImages.length > 0" v-for="boardImage in post.boardImages">
            <img class="thumbnail" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
          </div>
          <div class="d-inline" v-else>
            <img class="thumbnail" src="/assets/images/cat_loading.gif"/>
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

.pagination{
  margin-top: 1rem;
}
</style>