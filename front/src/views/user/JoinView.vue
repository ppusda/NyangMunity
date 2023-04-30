<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import router from "@/router";

const email = ref("")
const password = ref("")
const passwordChk = ref("")
const birthday = ref("")
const nickname = ref("")

const joinConfirm = function () {
  if(password.value != passwordChk.value){
    alert("비밀번호가 다릅니다.")
  }else{
    axios.post("/nm/user/join", {
      email: email.value,
      password: password.value,
      birthday: birthday.value,
      nickname: nickname.value,
    }).then(() => {
      router.replace({name: "home"})
    })
  }
}

</script>

<template>
  <div class="join_page">
    <form class="content_area">
      <table>
        <tr>
          <td class="w-50"><a class="text-white">이메일 : </a></td> <td class="w-auto"><input id="email" v-model="email" name="email" type="text"></td>
        </tr>
        <tr>
          <td class="w-50"><a class="text-white">비밀번호 : </a></td> <td class="w-auto"><input v-model="password" id="password" type="password"></td>
        </tr>
        <tr>
          <td class="w-50"><a class="text-white">비밀번호 확인 : </a></td> <td class="w-auto"><input v-model="passwordChk" id="passwordChk" name="passwordChk" type="password"></td>
        </tr>
        <tr>
          <td class="w-50"><a class="text-white">생일 : </a></td> <td class="w-auto"><input v-model="birthday" id="birthday" name="birthday" type="date"/></td>
        </tr>
        <tr>
          <td class="w-50"><a class="text-white">닉네임 : </a></td> <td class="w-auto"><input v-model="nickname" id="nickname" name="nickname" type="text"></td>
        </tr>
        <tr>
          <td colspan="2">
            <a class="clButton btn btn-secondary text-white" @click="$router.go(-1)">취소</a>
            <a class="clButton btn btn-primary m-3" @click="joinConfirm">회원가입</a>
          </td>
        </tr>
      </table>
    </form>
  </div>
</template>

<style scoped>
  .content_area {
    top: 15%;
  }

  .join_page{
    width: 100%;
    height: 100%;
    text-align: center;
    font-size: 0.9vw;
  }

  #birthday{
    width: 100%;
  }
</style>
