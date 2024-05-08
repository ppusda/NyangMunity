<script setup lang="ts">
import {defineProps, onMounted, reactive, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";
import {useCookies} from "vue3-cookies";

const router = useRouter();
const { cookies } = useCookies();

const post = ref({
  id: 0,
  title: "",
  content: "",
  boardImages: <any>[],
  createDate: ""
});

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
});

let removeList :any[] = reactive([]);


axios.post("/nm/user/check").then(() => {
    axios.get(`/nm/boards/${props.postId}`).then((response) => {
      post.value = response.data;
    });
}).catch(error => {
    if (error.response) {
      alert(error.response.data.message);
      router.replace({name: "main"});
    }
});

const edit = () => {
  const fileInput = document.getElementById('imgInput') as HTMLInputElement;

  if(post.value.title && post.value.title != '') {
    const formData = new FormData();
    formData.append('title', post.value.title);
    formData.append('content', post.value.content);
    Array.from(removeList ?? []).forEach((id) =>{
      formData.append('removeList', id);
    });
    Array.from(fileInput.files ?? []).forEach((file) =>{
      formData.append('boardImages', file)
    });

    axios.patch(`/nm/boards/${props.postId}`, formData).then(() => {
      router.replace({name: "boards"});
    });
  } else {
    alert("제목은 필수 입력사항입니다.");
  }
}

const remove = () => {
  axios.delete(`/nm/boards/${props.postId}`).then(() => {
    router.replace({name: "boards"})
  });
}

const imageUpload = () => {
  const fileDOM = document.getElementById("imgInput") as HTMLInputElement;

  if (fileDOM && !fileDOM.hasAttribute("listener")) {
    fileDOM.setAttribute("listener", "true");
    fileDOM?.addEventListener("change", () => {
      const preview = document.getElementById("previewDiv");
      const extension = fileDOM.value.substring(fileDOM.value.lastIndexOf(".")+1, fileDOM.value.length).toLowerCase();
      if(extension != "jpg" && extension != "png" &&  extension != "gif" &&  extension != "bmp" && extension != "jpeg" && extension != "JPEG") {
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

const removeImage = (id:any) => {
  removeList.push(id);
  const img = document.getElementById('img'+id) as HTMLImageElement;
  const imgRemoveButton = document.getElementById('imgRemoveBtn'+id) as HTMLButtonElement;

  img.remove();
  imgRemoveButton.remove();
}

</script>

<template>

  <div class="container w-100 h-100 text-white text-center">
    <div class="content_area scroll" method="post">
      <div>
        <el-input v-model="post.title"/>
      </div>
      <div class="mt-2 d-inline-flex" id="previewDiv">
      </div>
      <div class="mt-2">
        <label for="imgInput">
          <div type="button" class="btn-upload" @click="imageUpload">파일 업로드하기</div>
        </label>
        <input type="file" class="imgInput" name="imgInput" id="imgInput" accept="image/*" multiple>
      </div>
      <div class="mt-2">
        <el-input v-model="post.content" input-style="max-height: 200px" type="textarea" rows="3"></el-input>
      </div>
      <hr>
      <div class="mt-2 d-inline-flex" id="imgDiv" v-for="boardImage in post.boardImages">
        <img class="image-box" :id="`img${boardImage.id}`" :src="`data:image/jpeg;base64,${boardImage.imageBytes}`">
        <a :id="`imgRemoveBtn${boardImage.id}`" @click="removeImage(boardImage.id)" class="clButton m-1">❌</a>
      </div>
      <hr>
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
.content_area {
  top: 1.5%;
  padding: 1vw;
  width: 100%;
  overflow-y: scroll;
  max-height: 83vh;
}

.image-box {
  max-width: 4vw;
  max-height: 4vw;
  object-fit: cover;
  margin: 0.5vw;
}
.scroll::-webkit-scrollbar {
  display: none;
}

</style>