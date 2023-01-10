<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";

const email = ref("")
const password = ref("")

const login = function () {
  axios.post("http://localhost:8080/user/login", {
    email: email.value,
    password: password.value
  });
}

</script>

<template>
  <input value="true" type="text" name="loginState" id="loginState" hidden="true"/>

  <div class="login_page">
    <form id="login_form" method="post">
      <table>
        <tr>
          <td>ID :</td> <td><el-input id="email" v-model="email" type="text"></el-input></td>
        </tr>
        <tr>
          <td>PW :</td> <td><el-input id="password" v-model="password" type="password"></el-input></td>
        </tr>
        <tr>
          <td colspan="2"> <a id="loginBtn" @click="login">커뮤니티 로그인</a> </td>
        </tr>
        <tr>
          <td colspan="2">
            <a href="https://kauth.kakao.com/oauth/authorize?client_id=78effdc7d3e403e800af579ee0059fef&redirect_uri=http://localhost:8080/loginKakao&response_type=code">
              <img id="kakaoLoginIamge" src="/src/images/kakao_login_medium_wide.png"/>
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

</style>