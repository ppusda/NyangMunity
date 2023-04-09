<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const router = useRouter();

const post = ref([]);

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
});

axios.get(`/nm/boards/${props.postId}`).then((response) => {
  post.value = response.data;
});

const edit = () => {
  axios.patch(`/nm/boards/${props.postId}`, post.value).then(() => {
    router.replace({name: "boards"})
  });
}

const remove = () => {
  axios.delete(`/nm/boards/${props.postId}`).then(() => {
    router.replace({name: "boards"})
  });
}

</script>

<template>

  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area" method="post">
      <div>
        <el-input v-model="post.title"/>
      </div>

      <div class="mt-2">
        <el-input v-model="post.content" type="textarea" rows="15"></el-input>
      </div>

      <div class="mt-2">
        <el-button type="primary" @click="edit()">글 수정</el-button>
        <el-button type="danger" @click="remove()">글 삭제</el-button>
      </div>
    </div>
  </div>
</template>

<style>

</style>