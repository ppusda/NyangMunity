<script setup lang="ts">
import {onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosClient from '@/libs/axiosClient';
import {saveMemberInfo} from "@/utils/account";

onMounted(async () => {
  const route = useRoute();
  const router = useRouter();

  const provider = route.params.provider as string;
  const code = route.query.code as string;

  if (!provider || !code) {
    alert('잘못된 접근입니다.');
    router.replace({name: 'main'});
    return;
  }

  try {
    const response = await axiosClient.get(`/auth/${provider}Login`, {
      params: {code},
    });

    const memberInfoResponse = response.data;
    saveMemberInfo(memberInfoResponse);

    // 로그인 성공 후 메인 페이지로 이동
    await router.replace({name: 'main'});

  } catch (error) {
    console.error(`${provider} 로그인 실패:`, error);
    alert('로그인에 실패했습니다. 다시 시도해주세요.');
    router.replace({name: 'login'});
  }
});
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-zinc-900">
    <div class="text-center">
      <img src="/src/images/cat_loading.gif" alt="Loading..." class="w-24 h-24 mx-auto mb-4 rounded-md">
      <p class="text-white text-lg">로그인 중입니다...</p>
    </div>
  </div>
</template>
