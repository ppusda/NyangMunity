<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const email = ref("");
const password = ref("");

const login = function () {
  axios.post("/nm/user/login", {
    email: email.value,
    password: password.value
  }).then(() => {
    router.replace({name: "home"})
        .then(() => router.go(0))
  }).catch(error => {
    if(error.response){
      alert(error.response.data.message);
    } else {
      alert("계정이 올바르지 않습니다.");
    }
  });
}

</script>

<template>
  <input value="true" type="text" name="loginState" id="loginState" hidden="true"/>

  <div class="container login_page w-100 h-100 text-white text-center">
    <form class="content_area d-flex justify-content-center">
      <table>
        <tr>
          <td class="w-25 me-2">ID :</td> <td class="w-auto"><el-input id="email" v-model="email" type="text"></el-input></td>
        </tr>
        <tr>
          <td class="w-25 me-2">PW :</td> <td class="w-auto"><el-input id="password" v-model="password" type="password"></el-input></td>
        </tr>
        <tr>
          <td colspan="2" class="w-auto pe">
            <a class="clButton btn btn-primary mt-2" @click="login">커뮤니티 로그인</a>
          </td>
        </tr>
        <tr>
          <td colspan="2" class="w-auto">
            <a class="clButton btn btn-warning pt-1 text-white" href="https://kauth.kakao.com/oauth/authorize?client_id=78effdc7d3e403e800af579ee0059fef&redirect_uri=http://localhost:8080/loginKakao&response_type=code">
              카카오 로그인
            </a>
          </td>
        </tr>
        <tr>
          <td colspan="2" style="font-size: 1.5vh">--------------- 처음이신가요? ---------------</td>
        </tr>
        <tr>
          <td colspan="2"> <RouterLink to="/user/join">회원가입</RouterLink></td>
        </tr>
      </table>
    </form>
  </div>

</template>

<style scoped>
  .content_area {
    top: 15%;
  }

  .login_page{
    font-size: 0.9vw;
  }

  .clButton {
    font-size: 0.9vw;
    width: -webkit-fill-available;
  }

</style>