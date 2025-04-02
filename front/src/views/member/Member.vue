<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, reactive, ref} from "vue";
import axios from "axios";
import type {Member} from "@/interfaces/type";

let member = reactive<Member>({
  id: "",
  email: "",
  password: "",
  nickname: "",
  birthday: "",
});

const formData = new FormData();
const pwdCheck = ref("");
const newPassword = ref("");
const newPasswordChk = ref("");

const router = useRouter();

const userEdit = function () {
  if(newPassword.value !== newPasswordChk.value){
    alert("비밀번호가 다릅니다.")
  } else {
    formData.append("pwdCheck", pwdCheck.value);
    axios.post("/nm/members/pwdCheck", formData).then(response => {
      if (response.data) {
        member.password = newPassword.value;

        formData.append("nickname", member.nickname);
        formData.append("password",  member.password);
        formData.append("birthday", member.birthday);

        axios.post("/nm/members/edit", formData).then(() => {
          alert("정보 수정이 완료되었습니다.");
          router.replace({ name: "main" }).then(() => router.go(0));
        });
      } else {
        alert("비밀번호가 올바르지 않습니다.");
      }
    });
  }
};

const cancelUser = function () {
  formData.append("pwdCheck", pwdCheck.value);
  axios.post("/nm/members/pwdCheck", formData).then(response => {
    if (response.data) {
      alert("냥뮤니티를 이용해주셔서 감사했습니다.");
      axios.post("/nm/members/cancel", ).then(() => {
        router.replace({ name: "main" }).then(() => router.go(0));
      });
    } else {
      alert("비밀번호가 올바르지 않습니다.");
    }
  }).catch(error => {
    if(error.response) {
      alert(error.response.data.message);
      router.replace({name: "main"});
    }
  });
};

onMounted(async () => {
    await axios.get("/nm/members/profile").then(response => {
      member = response.data;
    }).catch(error => {
      if(error.response) {
        alert(error.response.data.message);
        router.replace({name: "main"});
      }
    });
});

</script>

<template>
  <div class="container login_page w-100 h-100 text-white text-center bootstrap snippets">
    <div class="content_area">
      <h3>User info</h3>
      <hr />
      <div class="member-info">
        <table>
          <tr>
            <td rowspan="6">
              <img class="thumbnail avatar img-circle img-thumbnail" src="/assets/images/cat_loading.gif" @mousedown.prevent/>
              <div type="button" class="btn-upload">파일 업로드하기</div>
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">이메일 : </a></td>
            <td class="w-50">
              <a class="w-100" id="email">{{member.email}}</a>
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">닉네임 : </a></td>
            <td class="w-50">
              <input class="w-100" id="nickname" name="nickname" type="text" v-model="member.nickname"/>
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">새 비밀번호 : </a></td>
            <td class="w-50">
              <input class="w-100" v-model="newPassword" id="newPassword" type="password" />
            </td>
          </tr>
          <tr>
            <td class="w-25"><a class="text-white">새 비밀번호 확인 : </a></td>
            <td class="w-50">
              <input class="w-100" v-model="newPasswordChk" id="newPasswordChk" type="password" />
            </td>
          </tr>
          <tr>
            <td>
              <input id="passwordCheck" type="password" placeholder="현재 비밀번호 입력" v-model="pwdCheck"/>
            </td>
            <td colspan="2">
              <a class="clButton btn btn-primary text-white m-1" @click="userEdit()">수정</a>
              <a class="clButton btn btn-danger text-white m-1" data-bs-toggle="modal" data-bs-target="#cancelUserModal">회원탈퇴</a>

              <div class="modal fade" id="cancelUserModal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h5 class="modal-title text-black" >탈퇴</h5>
                    </div>
                    <div class="modal-body text-black">
                      <b>정말로 탈퇴하시겠습니까? (* 다시 되돌릴 수 없습니다.)</b>
                    </div>
                    <div class="modal-footer">
                      <a type="button" class="clButton btn btn-secondary text-white m-1" data-bs-dismiss="modal">취소</a>
                      <a type="button" class="clButton btn btn-danger text-white m-1" @click="cancelUser()" data-bs-dismiss="modal">확인</a>
                    </div>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

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

.member-info {
  display: flex;
}
</style>
