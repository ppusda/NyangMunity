<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";

const email = ref("")
const password = ref("")

const login = function () {
  axios.post("/nm/user/login", {
    email: email.value,
    password: password.value
  });
}

</script>

<template>
  <input value="true" type="text" name="loginState" id="loginState" hidden="true"/>

  <div class="container login_page w-100 h-100 text-white text-center">
    <form id="login_form" method="post">
      <table>
        <tr>
          <td>ID :</td> <td><el-input id="email" v-model="email" type="text"></el-input></td>
        </tr>
        <tr>
          <td>PW :</td> <td><el-input id="password" v-model="password" type="password"></el-input></td>
        </tr>
        <tr>
          <td colspan="2">
            <a id="loginBtn" @click="login">커뮤니티 로그인</a>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <a href="https://kauth.kakao.com/oauth/authorize?client_id=78effdc7d3e403e800af579ee0059fef&redirect_uri=http://localhost:8080/loginKakao&response_type=code">
              <img class="w-100" id="kakaoLoginIamge" src="/src/images/kakao_login_medium_wide.png"/>
            </a>
          </td>
        </tr>
        <tr>
          <td colspan="2" style="font-size: 2px">--------------- 처음이신가요? ---------------</td>
        </tr>
        <tr>
          <td colspan="2"> <RouterLink to="/user/join">회원가입</RouterLink></td>
        </tr>
      </table>
    </form>
  </div>

</template>

<style scoped>
  .login_page{
    font-size: 0.9vw;
  }

  #login_form{
    display: inline-block;
    position: relative;
    top: 15%;
    background: #333;
    padding: 5vw 15vw;
    border-radius: 15px;
  }

  #kakaoLoginIamge{
    height: 2vw;
    margin: 0.5vw;
  }

  #loginBtn{
    margin-top: 0.5vw;
    display: flex;
    flex-direction: column;
    padding: 0.3vw;
    background: rgb(24, 24, 24, 0.5);
    border-radius: 8px;
  }

</style>