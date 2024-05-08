<script setup lang="ts">

import axios from "axios";
import {reactive} from "vue";
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
  // 페이지 번호를 Spring에 맞게 조정
  const springPageValue = pageValue - 1;
  axios.get("/nm/boards?page="+springPageValue+"&size="+pageCount, ).then(response => {
    totalPage.value = response.data.totalPages;
    posts.splice(0, posts.length);
    response.data.content.forEach((res: any) => {
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
        router.push({ name: "login" });
      }
  });
}

</script>

<template>
  <div class="bg-zinc-800 w-screen rounded-2xl p-8 flex flex-col items-center">
    <div class="bg-zinc-700 rounded-md sm:w-6/12 w-full h-[35rem] p-12 overflow-auto boardList">
      <ul class="w-full">
        <li class="mb-4 p-8 bg-white rounded-md shadow" v-for="post in posts" :key="post.id">
          <div>
            <h3 class="font-bold text-xl mb-2">
              <router-link class="text-blue-500 hover:text-blue-800" :to="{name: 'read', params: {postId: post.id}}">{{post.title}}</router-link>
            </h3>
            <p class="badge badge-primary badge-outline mr-1.5">{{post.writer}}</p>
            <p class="badge badge-primary badge-outline">{{getWriteTime(post.createDate)}}</p>
          </div>
          <div class="mt-2 flex flex-wrap w-full h-full">
            <router-link class="w-full h-full" :to="{name: 'read', params: {postId: post.id}}">
              <div class="w-full h-full" v-if="post.boardImages && post.boardImages.length > 0" v-for="(boardImage, index) in post.boardImages.slice(0, Math.min(3, post.boardImages.length))">
                <div v-if="index <= 1">
                  <img class="w-full h-full object-contain rounded-md" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`" />
                </div>
                <div class="w-full h-24 flex items-center justify-center text-white bg-gray-400 rounded-md" v-else>
                  +{{ post.boardImages.length - 2 }}
                </div>
              </div>
              <div class="flex flex-row justify-center w-full h-80 border" v-else>
                <img class="w-full h-full object-contain rounded-md" src="/assets/images/cat_loading.gif"/>
              </div>
            </router-link>
          </div>
        </li>
      </ul>
    </div>

    <div class="flex sm:flex-row flex-col justify-between items-center my-4 w-full md:w-2/3 lg:w-1/2">
      <div>
        <paginate
            :pageCount="totalPage.value"
            :elementClass="'btn btn-primary'"
            :clickHandler="movePage">
        </paginate>
      </div>
      <div>
        <a class="btn sm:mt-0 mt-3 btn-outline btn-primary" @click="moveToWrite()">새로 글 쓰기</a>
      </div>
    </div>
  </div>

</template>

<style>
  .boardList{
    overflow-y: scroll;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  .boardList::-webkit-scrollbar {
    display:none;
  }

  .pagination {
    display: flex;
    justify-content: center;
    list-style: none;
    padding: 0;
  }

  .pagination li {
    margin: 0 1.5px;
  }

  .pagination li a {
    padding: 5px 15px;
    border: 1px solid #ccc;
    border-radius: 4px;
    text-decoration: none;
    color: #ccc;
  }

  .pagination li.active a {
    background-color: #007bff;
    color: #fff;
  }

  .pagination li.disabled a {
    color: #666;
  }

</style>