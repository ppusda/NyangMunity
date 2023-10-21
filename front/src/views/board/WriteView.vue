<script setup lang="ts" defer>
import {ref} from "vue";

import axios from "axios";
import {useRouter} from "vue-router";
import homeView from "@/views/HomeView.vue";

const title = ref("");
const content = ref("");

const router = useRouter();

const write = function (e: Event) {
  e.preventDefault();

  if(title.value && title.value != "") {
    const fileInput = document.getElementById("imgInput") as HTMLInputElement;

    const formData = new FormData();
    formData.append("title", title.value);
    formData.append("content", content.value);
    Array.from(fileInput.files ?? []).forEach((file) =>{
      formData.append("boardImages", file);
    });

    axios.post("/nm/boards/write", formData, {
    }).then(() => {
        router.replace({ name: "boards" });
    }).catch(error => {
      if (error.response) {
        alert(error.response.data.message);
      }else {
        alert("비정상적인 접근입니다.");
      }
      router.replace({name: "home"});
    })
  } else {
    alert("제목은 필수 입력사항입니다.");
  }
  
};

const imageUpload = () => {
  const fileDOM = document.getElementById("imgInput") as HTMLInputElement;
  
  if (fileDOM && !fileDOM.hasAttribute("listener")) {
    fileDOM.setAttribute("listener", "true");
    fileDOM?.addEventListener("change", () => {
      const preview = document.getElementById("previewDiv");
      const extension = fileDOM.value.substring(fileDOM.value.lastIndexOf(".")+1, fileDOM.value.length).toLowerCase();
      if(extension != "jpg" && extension != "png" &&  extension != "gif" &&  extension != "bmp" && extension != "JPEG") {
        preview!.innerHTML = '';
        fileDOM!.value = '';
        alert("지원되지 않는 확장자입니다.");
        return;
      } else{
        preview!.innerHTML = '';
        if (!fileDOM.files) {
          return;
        }
        if (fileDOM.files.length <= 10) {
          for(let i = 0; i < 3; i++){
            const urls = URL.createObjectURL(fileDOM.files[i]);
            document.getElementById("previewDiv")!.innerHTML += '<img class="image-box" src="'+urls+'">';
          }
          if (fileDOM.files.length > 3) {
            document.getElementById("previewDiv")!.innerHTML += '<h6>+'+(fileDOM.files.length-3)+' More...</h6>';
          }
          return;
        }else {
          alert("이미지는 최대 10개까지만 입력 가능합니다.");
          preview!.innerHTML = '';
          fileDOM!.value = '';
          return;
        }
      }
    });
  }
}

// // window.onload 사용해서 해결.. 결국엔 로드되지 않은 것을 불러오려 하여 발생한 이슈였음.
// // script 상의 위치가 바뀌어도 해결되지 않았던 이유는 vue여서 일까?

</script>

<template>

  <div class="container w-100 h-100 text-white text-center">
    <form class="content_area scroll">
      <div>
        <el-input v-model="title" placeholder="제목을 입력해주세요" />
      </div>
      <div class="mt-2" id="previewDiv">
      </div>
      <div class="mt-2">
        <label for="imgInput">
          <div type="button" class="btn-upload" @click="imageUpload">파일 업로드하기</div>
        </label>
        <input type="file" class="imgInput" name="imgInput" id="imgInput" accept="image/*" multiple>
      </div>
      <div class="mt-2">
        <el-input v-model="content" input-style="max-height: 200px" type="textarea" rows="3"></el-input>
      </div>
      <div class="mt-2">
        <a class="clButton btn btn-secondary text-white m-1" @click="$router.go(-1)">취소</a>
        <a class="clButton btn btn-primary text-white m-1" @click="write">글 작성완료</a>
      </div>
    </form>
  </div>
</template>

<style>
  .content_area {
    top: 1.5%;
    padding: 1vw;
    width: 100%;
    overflow-y: scroll;
    max-height: 83vh;
  }

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
    max-width: 4vw;
    max-height: 4vw;
  }

  .scroll::-webkit-scrollbar {
    display: none;
  }

</style>