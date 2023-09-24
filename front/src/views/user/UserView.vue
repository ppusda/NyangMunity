<script setup lang="ts">
import { useRouter } from "vue-router";
import {onMounted, ref} from "vue";
import { useCookies } from "vue3-cookies";
import axios from "axios";

let cookieValue = ref("");
const user = ref({
  id: 0,
  email: "",
  password: "",
  nickname: "",
  birthday: "",
});

const { cookies } = useCookies();
cookieValue.value = cookies.get("SESSION");

const router = useRouter();

const userLogout = function () {
  axios.post("/nm/user/logout").then(() => {
    cookies.remove("SESSION");
    router.replace({ name: "home" }). then(() => router.go(0));
  });
};

onMounted(async () => {
    await axios.post("/nm/user/info",
        {SID: cookieValue.value,}).then(response => {
      user.value = response.data;
    });
});

</script>

<template>
  <div
    class="container login_page w-100 h-100 text-white text-center bootstrap snippets"
  >
    <div class="content_area">
      <h3>User info</h3>
      <hr />
      <div class="user-info">
        <table>
          <tr>
            <td rowspan="6">
              <img
                class="thumbnail avatar img-circle img-thumbnail"
                src="/assets/images/cat_loading.gif"
                @mousedown.prevent
              />
              <div type="button" class="btn-upload">파일 업로드하기</div>
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">이메일 : </a></td>
            <td class="w-50">
              <a class="w-100" id="email" name="email"></a>
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">닉네임 : </a></td>
            <td class="w-50">
              <input class="w-100" id="nickname" name="nickname" type="text" />
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">새 비밀번호 : </a></td>
            <td class="w-50">
              <input class="w-100" id="newPassword" type="password" />
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">새 비밀번호 확인 : </a></td>
            <td class="w-50">
              <input class="w-100" id="newPasswordChk" type="password" />
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">생일 : </a></td>
            <td class="w-50">
              <input class="w-100" id="birthday" name="birthday" type="date" />
            </td>
          </tr>
          <tr>
            <td>
              <input
                id="password"
                type="password"
                placeholder="현재 비밀번호 입력"
              />
            </td>
            <td colspan="2">
              <a class="clButton btn btn-primary text-white m-1">수정</a>
              <a
                class="clButton btn btn-secondary text-white m-1"
                @click="userLogout()"
                >로그아웃</a
              >
              <a class="clButton btn btn-danger text-white m-1">회원탈퇴</a>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<!--<div class="alert alert-info alert-dismissable">-->
<!--<a class="panel-close close" data-dismiss="alert">×</a>-->
<!--<i class="fa fa-coffee"></i>-->
<!--This is an <strong>.alert</strong>. Use this to show important messages to the user.-->
<!--</div>-->

<style scoped>
.btn-upload {
  width: 150px;
  height: 30px;
  background: #333;
  border: 1px solid rgb(77, 77, 77);
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  display: inline-block;
  margin-top: 0.5vw;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
}
</style>
