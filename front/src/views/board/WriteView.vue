<script setup lang="ts">
import {ref} from "vue";

const count = ref(0) // 반응형 변수로 추가

import axios from "axios";
import {useRouter} from "vue-router";
import homeView from "@/views/HomeView.vue";

const title = ref("")
const content = ref("")

const router = useRouter()

const write = function () {
  axios.post("/nm/boards/write", {
    title: title.value,
    content: content.value
  })
  .then(() => {
    router.replace({name: "home"})
  })
}
</script>

<template>
  <div>
    <el-input v-model="title" placeholder="제목을 입력해주세요" />
  </div>

  <div class="mt-2">
    <el-input v-model="content" type="textarea" rows="15"></el-input>
  </div>

  <div class="mt-2">
    <button type="primary" @click="write">글 작성완료</button>
  </div>
</template>

<style>

</style>