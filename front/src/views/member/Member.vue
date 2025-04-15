<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, reactive, ref} from "vue";
import type {Member} from "@/interfaces/type";
import axiosClient from "@/libs/axiosClient";

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
    axiosClient.post("/nm/members/pwdCheck", formData).then(response => {
      if (response.data) {
        member.password = newPassword.value;

        formData.append("nickname", member.nickname);
        formData.append("password", member.password);
        formData.append("birthday", member.birthday);

        axiosClient.post("/nm/members/edit", formData).then(() => {
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
  axiosClient.post("/nm/members/pwdCheck", formData).then(response => {
    if (response.data) {
      alert("냥뮤니티를 이용해주셔서 감사했습니다.");
      axiosClient.post("/nm/members/cancel", ).then(() => {
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
    await axiosClient.get("/nm/members/profile").then(response => {
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
  <div class="w-screen rounded-2xl p-8 flex justify-center">
    <div class="bg-zinc-800 p-6 rounded-md sm:w-[30rem] w-[32rem]">
      <div class="text-center mb-6">
        <h3 class="text-2xl font-bold text-white">User info</h3>
        <hr class="border-gray-600 mt-3" />
      </div>

      <div class="flex flex-col items-center mb-6">
        <img class="w-24 h-24 rounded-full object-cover mb-2"
             src="/assets/images/cat_loading.gif"
             @mousedown.prevent />
        <button class="btn btn-outline btn-ghost text-sm text-white px-4 py-1 rounded-md">
          파일 업로드하기
        </button>
      </div>

      <div class="flex flex-col space-y-4">
        <div class="flex flex-row items-center">
          <label class="text-white w-32">이메일 :</label>
          <p id="email" class="text-white flex-1">{{member.email}}</p>
        </div>

        <div class="flex flex-row items-center">
          <label for="nickname" class="text-white w-32">닉네임 :</label>
          <input
              class="flex-1 input input-bordered border-zinc-500 bg-zinc-900 p-2 rounded-md text-white"
              id="nickname"
              name="nickname"
              type="text"
              v-model="member.nickname"/>
        </div>

        <div class="flex flex-row items-center">
          <label for="newPassword" class="text-white w-32">새 비밀번호 :</label>
          <input
              class="flex-1 input input-bordered border-zinc-500 bg-zinc-900 p-2 rounded-md text-white"
              v-model="newPassword"
              id="newPassword"
              type="password" />
        </div>

        <div class="flex flex-row items-center">
          <label for="newPasswordChk" class="text-white w-32">비밀번호 확인 :</label>
          <input
              class="flex-1 input input-bordered border-zinc-500 bg-zinc-900 p-2 rounded-md text-white"
              v-model="newPasswordChk"
              id="newPasswordChk"
              type="password" />
        </div>

        <div class="pt-4 border-t border-gray-600">
          <input
              id="passwordCheck"
              type="password"
              placeholder="현재 비밀번호 입력"
              v-model="pwdCheck"
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-2 rounded-md text-white mb-4" />

          <div class="flex justify-center gap-3">
            <button
                class="btn btn-outline btn-primary text-white px-6 py-2 rounded-md"
                @click="userEdit()">
              <i class="fa-solid fa-check"></i> 수정
            </button>
            <button
                class="btn btn-outline btn-error text-white px-6 py-2 rounded-md"
                data-bs-toggle="modal"
                data-bs-target="#cancelUserModal">
              <i class="fa-solid fa-user-slash"></i> 회원탈퇴
            </button>
          </div>
        </div>
      </div>

      <!-- 회원탈퇴 모달 -->
      <div class="modal fade" id="cancelUserModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
          <div class="modal-content bg-zinc-700">
            <div class="modal-header border-gray-600">
              <h5 class="modal-title text-white font-bold">탈퇴</h5>
            </div>
            <div class="modal-body text-white">
              <b>정말로 탈퇴하시겠습니까? (* 다시 되돌릴 수 없습니다.)</b>
            </div>
            <div class="modal-footer border-gray-600">
              <button
                  class="btn btn-outline btn-ghost text-white px-4 py-2 rounded-md"
                  data-bs-dismiss="modal">
                취소
              </button>
              <button
                  class="btn btn-outline btn-error text-white px-4 py-2 rounded-md"
                  @click="cancelUser()"
                  data-bs-dismiss="modal">
                확인
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-outline {
  border-width: 1px;
}

.modal-content {
  border-radius: 0.5rem;
}

.modal-header, .modal-footer {
  padding: 0.75rem 1rem;
}

.modal-body {
  padding: 1.5rem;
}
</style>