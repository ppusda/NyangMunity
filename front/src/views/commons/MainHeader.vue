<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue';
import { useCookies } from 'vue3-cookies';
import axios from "axios";

let cookieValue = reactive({ value: "" });
let nickName = reactive({ value: "" });
const { cookies } = useCookies();

onMounted(async () => {
  cookieValue.value = cookies.get('SESSION');
  if(cookieValue.value && cookieValue.value !== "") {
    await axios.post("/nm/user/check", {SID: cookieValue.value,}).then(response => {
        nickName.value = response.data.nickname;
    })
  }
});

</script>

<template>
  <header>
    <nav class="navbar navbar-dark navbar-expand-lg header shadow-lg sticky-top">
      <div class="container">
        <a class="navbar-brand align-middle" id="img_logo" href="/">
          <img id="logo" src="/src/images/logo.png" alt="" height="72">
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarContent">
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0 fw-bold">
            <li class="nav-item">
              <a v-if="nickName && nickName.value" class="nav-link">
                <RouterLink to="/user/info">{{nickName.value}} 님, 환영합니다!</RouterLink>
              </a>
              <a v-else class="nav-link">
                <RouterLink to="/user/login">로그인</RouterLink>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link">
                <RouterLink to="/boards/">커뮤니티</RouterLink>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link">
                <RouterLink to="/meme">짤</RouterLink>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>

<style scoped>
  .header {
    background-color: #333;
    border-radius: 0px 0px 15px 15px;
  }

  #menuDivUl {
    list-style: none;
  }
</style>