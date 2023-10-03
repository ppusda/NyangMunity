<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";
import {useCookies} from "vue3-cookies";

const router = useRouter();
const { cookies } = useCookies();

const post = ref<any>({});

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
});

axios.post("/nm/user/check").then(() => {
    axios.get(`/nm/boards/${props.postId}`).then((response) => {
      post.value = response.data;
    });
}).catch(error => {
    if (error.response) {
      alert(error.response.data.message);
      router.replace({name: "home"});
    }
});

const edit = () => {
  axios.patch(`/nm/boards/${props.postId}`, post.value).then(() => {
    router.replace({name: "boards"});
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
        <a class="clButton btn btn-secondary text-white m-1" @click="$router.go(-1)">취소</a>
        <a class="clButton btn btn-primary text-white m-1" @click="edit()">글 수정</a>
        <a class="clButton btn btn-danger text-white m-1" data-bs-toggle="modal" data-bs-target="#boardDeleteModal">글 삭제</a>

        <div class="modal fade" id="boardDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title text-black" >삭제</h5>
              </div>
              <div class="modal-body text-black">
                <b>정말로 삭제하시겠습니까?</b>
              </div>
              <div class="modal-footer">
                <a type="button" class="clButton btn btn-secondary text-white m-1" data-bs-dismiss="modal">취소</a>
                <a type="button" class="clButton btn btn-danger text-white m-1" @click="remove()" data-bs-dismiss="modal">삭제</a>
              </div>
            </div>
          </div>
        </div>
      </div>


    </div>
  </div>

</template>

<style>

</style>