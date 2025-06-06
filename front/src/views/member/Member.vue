<script setup lang="ts">
import {useRouter} from "vue-router";
import {computed, onMounted, reactive, ref} from "vue";
import type {Member} from "@/interfaces/type";
import axiosClient from "@/libs/axiosClient";
import store from "@/stores/store";
import {infoToast, warningToast} from "@/libs/toaster";
import {logout} from "@/utils/account";

const router = useRouter();
const showCancelModal = ref(false);

let member = reactive<Member>({
  id: "",
  email: "",
  password: "",
  nickname: "",
});

const currentPassword = ref("");
const newPassword = ref("");
const newPasswordConfirm = ref("");

const userEdit = function () {
  if (newPassword.value !== newPasswordConfirm.value) {
    warningToast("비밀번호가 다릅니다.");
  } else {
    axiosClient.patch("/members/profile", {
      'nickname': member.nickname,
      'newPassword': newPassword.value,
      'currentPassword': currentPassword.value
    }).then(() => {
      infoToast("정보 수정이 완료되었습니다.");
      router.replace({name: "main"});
    });
  }
};

const cancelUser = function () {
  // 로그아웃 선 진행
  logout();
  
  axiosClient.post("/members/cancel", {
    'currentPassword': currentPassword.value
  }).then(() => {
    infoToast("냥뮤니티를 이용해주셔서 감사했습니다.");
    router.replace({name: "main"});
  });
};

onMounted(async () => {
  const email = computed(() => store.state.email);
  const nickname = computed(() => store.state.nickname);

  if (email.value && nickname.value) {
    member.email = email.value;
    member.nickname = nickname.value;
  } else {
    await axiosClient.get("/members/profile").then(response => {
      member = response.data;
    }).catch(() => {
      router.replace({name: "main"});
    });
  }
});

</script>
<template>
  <div class="w-screen rounded-2xl p-8 flex justify-center">
    <div class="bg-zinc-800 p-6 rounded-md sm:w-[30rem] w-[32rem]">
      <div class="text-center mb-6">
        <h3 class="text-2xl font-bold text-white">Member info</h3>
        <hr class="border-gray-600 mt-3"/>
      </div>

      <div class="flex flex-col items-center mb-6">
        <img class="w-24 h-24 rounded-full object-cover mb-2"
             src="/assets/images/cat_loading.gif"
             @mousedown.prevent/>
        <button class="btn btn-outline btn-ghost text-sm text-white px-4 py-1 rounded-md">
          파일 업로드하기
        </button>
      </div>

      <div class="flex flex-col space-y-4">
        <div class="flex flex-row items-center">
          <label class="text-white w-32">이메일 :</label>
          <p id="email" class="text-white flex-1">{{ member.email }}</p>
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
              type="password"/>
        </div>

        <div class="flex flex-row items-center">
          <label for="newPasswordChk" class="text-white w-32">비밀번호 확인 :</label>
          <input
              class="flex-1 input input-bordered border-zinc-500 bg-zinc-900 p-2 rounded-md text-white"
              v-model="newPasswordConfirm"
              id="newPasswordChk"
              type="password"/>
        </div>

        <div class="pt-4 border-t border-gray-600">
          <input
              id="passwordCheck"
              type="password"
              placeholder="현재 비밀번호 입력"
              v-model="currentPassword"
              class="input input-bordered border-zinc-500 bg-zinc-900 w-full p-2 rounded-md text-white mb-4"/>

          <div class="flex justify-center gap-3">
            <button
                class="btn btn-outline btn-primary text-white px-6 py-2 rounded-md"
                @click="userEdit">
              <i class="fa-solid fa-check"></i> 수정
            </button>
            <button
                class="btn btn-outline btn-error text-white px-6 py-2 rounded-md"
                @click="showCancelModal = true">
              <i class="fa-solid fa-user-slash"></i> 회원탈퇴
            </button>
          </div>
        </div>
      </div>

      <!-- 회원탈퇴 모달 -->
      <div v-if="showCancelModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="modal-content bg-zinc-800 w-[90%] sm:w-[24rem] rounded-md">
          <div class="modal-header border-b border-gray-600 px-4 py-3">
            <h5 class="modal-title text-white font-bold">탈퇴</h5>
          </div>
          <div class="modal-body text-white px-4 py-4">
            <b>정말로 탈퇴하시겠습니까? <br>(* 다시 되돌릴 수 없습니다.)</b>
          </div>
          <div class="modal-footer border-t border-gray-600 px-4 py-3 flex justify-end gap-2">
            <button
                class="btn btn-outline btn-ghost text-white px-4 py-2 rounded-md"
                @click="showCancelModal = false">
              취소
            </button>
            <button
                class="btn btn-outline btn-error text-white px-4 py-2 rounded-md"
                @click="() => { cancelUser(); showCancelModal = false; }">
              확인
            </button>
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