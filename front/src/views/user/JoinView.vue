<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";

const email = ref("")
const password = ref("")
const passwordChk = ref("")

const join = function () {
  if(password != passwordChk){
    alert("비밀번호가 다릅니다.")
  }else{
    axios.post("/nm/user/login", {
    });
  }
}

</script>

<template>
  <div class="join_page">
    <form id="join_form">
      <table>
        <tr>
          <td><a>이메일 : </a></td> <td><input id="email" v-model="email" name="email" type="text"></td>
        </tr>
        <tr>
          <td><a>비밀번호 : </a></td> <td><input id="password" name="password" type="password"></td>
        </tr>
        <tr>
          <td><a>비밀번호 확인 : </a></td> <td><input id="passwordCheck" name="passwordCheck" type="password"></td>
        </tr>
        <tr>
          <td><a>생일 : </a></td> <td><input id="birthday" name="birthday" type="date"/></td>
        </tr>
        <tr>
          <td><a>닉네임 : </a></td> <td><input id="nickname" name="nickname" type="text"></td>
        </tr>
        <tr>
          <td> <a class="joinBtn" @click="join">회원가입</a></td>
          <td><a class="joinBtn" @click="$router.go(-1)">취소</a></td>
        </tr>
      </table>
    </form>
  </div>
</template>

<style scoped>
</style>
