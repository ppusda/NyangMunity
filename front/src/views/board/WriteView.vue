<script setup lang="ts" defer>
import {ref} from "vue";

import axios from "axios";
import {useRouter} from "vue-router";
import homeView from "@/views/HomeView.vue";

const title = ref("");
const content = ref("");

const router = useRouter();

const write = function () {
  axios.post("/nm/boards/write", {
    title: title.value,
    content: content.value
  })
  .then(() => {
    router.replace({name: "home"})
  })
};

// window.onload = () => {
//   const fileDOM = document.getElementById('imgInput') as HTMLInputElement;
//   const preview = document.querySelector('.image-box') as HTMLImageElement;
//
//   fileDOM?.addEventListener('change', () => {
//   if (!fileDOM.files) {
//     return;
//   }
//   const imageSrc = URL.createObjectURL(fileDOM.files[0]);
//   preview!.src = imageSrc
// });
// }

// // window.onload 사용해서 해결.. 결국엔 로드되지 않은 것을 불러오려 하여 발생한 이슈였음.
// // script 상의 위치가 바뀌어도 해결되지 않았던 이유는 vue여서 일까?

window.onload = () => {
  const fileDOM = document.getElementById('imgInput') as HTMLInputElement;

  fileDOM?.addEventListener('change', () => {
    const preview = document.getElementById('previewDiv');
    preview!.innerHTML = '';
    if (!fileDOM.files) {
      return;
    }
    for(let i = 0; i < 3; i++){
      const urls = URL.createObjectURL(fileDOM.files[i]);
      document.getElementById("previewDiv")!.innerHTML += '<img class="image-box" src="'+urls+'">';
    }
    if (fileDOM.files.length > 3) {
      document.getElementById("previewDiv")!.innerHTML += '<h6>+'+(fileDOM.files.length-3)+' More...</h6>';
    }
  });
}


</script>

<template>

  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area" method="post">
      <div>
        <el-input v-model="title" placeholder="제목을 입력해주세요" />
      </div>
      <div class="mt-2" id="previewDiv">
      </div>
      <div class="mt-2">
        <label for="imgInput">
          <div class="btn-upload">파일 업로드하기</div>
        </label>
        <input type="file" name="imgInput" class="imgInput" id="imgInput" multiple>
      </div>
      <div class="mt-2">
        <el-input v-model="content" type="textarea" rows="3"></el-input>
      </div>
      <div class="mt-2">
        <el-button type="primary" @click="write">글 작성완료</el-button>
      </div>
    </div>
  </div>
</template>

<style>
  .btn-upload {
    width: 150px;
    height: 30px;
    background: #333;
    border: 1px solid rgb(77,77,77);
    border-radius: 10px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .btn-upload:hover {
    background: rgb(77,77,77);
    color: #fff;
  }

  #imgInput {
    display: none;
  }

  .image-box {
    max-width: 8vw;
    max-height: 8vw;
  }
</style>