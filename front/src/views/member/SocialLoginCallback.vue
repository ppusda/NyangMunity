<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import axiosClient from '@/libs/axiosClient';

onMounted(async () => {
  const route = useRoute();
  const router = useRouter();
  const store = useStore();

  const provider = route.params.provider as string;
  const code = route.query.code as string;

  if (!provider || !code) {
    alert('잘못된 접근입니다.');
    router.replace({ name: 'main' });
    return;
  }

  try {
    const response = await axiosClient.get(`/auth/${provider}Login`, {
      params: { code },
    });

    const { memberInfoResponse, memberTokens } = response.data;
    const { accessToken, refreshToken } = memberTokens;

    // Vuex store에 토큰과 멤버 정보 저장
    await store.dispatch('saveTokens', { accessToken, refreshToken });
    await store.dispatch('login', memberInfoResponse);

    // axios 기본 헤더에 accessToken 설정 (쿠키와 별개로 API 요청 시 사용)
    axiosClient.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

    // 로그인 성공 후 메인 페이지로 이동
    await router.replace({ name: 'main' });

  } catch (error) {
    console.error(`${provider} 로그인 실패:`, error);
    alert('로그인에 실패했습니다. 다시 시도해주세요.');
    router.replace({ name: 'login' });
  }
});
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-zinc-900">
    <div class="text-center">
      <img src="/src/images/cat_loading.gif" alt="Loading..." class="w-24 h-24 mx-auto mb-4">
      <p class="text-white text-lg">로그인 중입니다...</p>
    </div>
  </div>
</template>
