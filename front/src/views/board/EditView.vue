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

</script>

<template>
  <div>
    <el-input v-model="post.title"/>
  </div>

  <div class="mt-2">
    <el-input v-model="post.content" type="textarea" rows="15"></el-input>
  </div>

  <div class="mt-2">
    <button type="primary" @click="edit()">수정 완료</button>
  </div>
</template>

<style>

</style>