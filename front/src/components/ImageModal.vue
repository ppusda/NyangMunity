<script setup lang="ts">
import {ref, computed} from 'vue';
import {useClipboard} from '@vueuse/core';
import {infoToast, warningToast} from '@/libs/toaster';
import axiosClient from '@/libs/axiosClient';
import type {Post} from '@/interfaces/type';
import store from '@/stores/store';
import router from '@/router';

const props = defineProps<{
  post: Post | null;
}>();

const emit = defineEmits(['close']);

// 로그인 상태
const isLogin = computed(() => store.state.isLogin);

const image = computed(() => {
  // Images 갤러리인 경우
  if (props.post?.url) {
    return {
      id: props.post.id,
      url: props.post.url,
      likeState: props.post.likeState || false
    };
  }
  // Posts인 경우
  return props.post?.postImages?.[0];
});

// 작성자 (Images는 없음)
const writer = computed(() => {
  if (props.post?.url) return 'Gallery';
  return props.post?.writer || 'Unknown';
});

// 좋아요 상태 (메인)
const isLiked = ref(image.value?.likeState || false);

// 이모지 반응 상태 (추가)
const emojiReactions = ref<Record<string, number>>({});
const myEmojiReactions = ref<Set<string>>(new Set()); // 내가 누른 이모지
const availableEmojis = [
  {emoji: '😂', label: '웃김'},
  {emoji: '❤️', label: '사랑'},
  {emoji: '🔥', label: '멋짐'},
  {emoji: '😱', label: '놀람'},
  {emoji: '😻', label: '귀여움'}
];

// 좋아요 토글
const toggleLike = async () => {
  if (!isLogin.value) {
    warningToast('로그인이 필요합니다.');
    await router.push({name: 'login'});
    return;
  }

  const imageId = image.value?.id;
  if (!imageId) return;

  try {
    const response = await axiosClient.post('/images/likes', {imageId});
    isLiked.value = response.data.state;
    infoToast(response.data.state ? '좋아요!' : '좋아요 취소');
  } catch (error) {
    warningToast('좋아요 처리에 실패했습니다.');
  }
};

// 이모지 반응 추가/제거
const toggleEmojiReaction = async (emoji: string) => {
  if (!isLogin.value) {
    warningToast('로그인이 필요합니다.');
    await router.push({name: 'login'});
    return;
  }

  const imageId = image.value?.id;
  if (!imageId) return;

  try {
    // TODO: 실제 API 연결 시 사용
    // const response = await axiosClient.post('/images/reactions', { imageId, emoji });

    // 임시: 로컬 상태만 업데이트
    const hasReacted = myEmojiReactions.value.has(emoji);

    if (hasReacted) {
      // 이미 반응했으면 제거
      myEmojiReactions.value.delete(emoji);
      if (emojiReactions.value[emoji]) {
        emojiReactions.value[emoji]--;
        if (emojiReactions.value[emoji] <= 0) {
          delete emojiReactions.value[emoji];
        }
      }
      infoToast('반응 취소');
    } else {
      // 새로 반응 추가
      myEmojiReactions.value.add(emoji);
      if (!emojiReactions.value[emoji]) {
        emojiReactions.value[emoji] = 0;
      }
      emojiReactions.value[emoji]++;
      infoToast(`${emoji} 반응 추가!`);
    }
  } catch (error) {
    warningToast('반응 처리에 실패했습니다.');
  }
};

// 이모지 반응 여부 확인
const hasEmojiReaction = (emoji: string) => {
  return myEmojiReactions.value.has(emoji);
};

// URL 복사
const {copy} = useClipboard();
const copyImageUrl = () => {
  const url = image.value?.url;
  if (url) {
    copy(url);
    infoToast('이미지 URL이 복사되었습니다!');
  }
};

// 모달 닫기
const closeModal = () => {
  emit('close');
};

// 키보드 이벤트
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape') closeModal();
};

// 작성 시간 포맷
const formatTime = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) return `${days}일 전`;
  if (hours > 0) return `${hours}시간 전`;
  if (minutes > 0) return `${minutes}분 전`;
  return '방금 전';
};
</script>

<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/90 backdrop-blur-sm"
         @click.self="closeModal"
         @keydown="handleKeydown"
         tabindex="0">

      <div
          class="relative w-full max-w-6xl h-full max-h-[90vh] flex flex-col md:flex-row bg-zinc-900 rounded-2xl overflow-hidden shadow-2xl animate-fadeIn">

        <!-- 왼쪽: 이미지 영역 -->
        <div class="relative flex-1 bg-black flex items-center justify-center">
          <img v-if="image" :src="image.url" class="max-w-full max-h-full object-contain"/>
        </div>

        <!-- 오른쪽: 정보 영역 -->
        <div class="w-full md:w-96 bg-zinc-900 flex flex-col">
          <!-- 헤더 -->
          <div class="p-4 border-b border-zinc-800 flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div
                  class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold">
                {{ writer.charAt(0).toUpperCase() }}
              </div>
              <div>
                <p class="text-white font-medium">{{ writer }}</p>
                <p class="text-gray-400 text-xs">{{ formatTime(post?.createDate || new Date().toISOString()) }}</p>
              </div>
            </div>
            <button @click="closeModal" class="text-gray-400 hover:text-white transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                   stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>

          <!-- 내용 (Posts만) -->
          <div class="flex-1 p-4 overflow-y-auto">
            <p v-if="post?.content" class="text-white mb-6">{{ post.content }}</p>
            <p v-else-if="!post?.url" class="text-gray-500 italic mb-6">설명이 없습니다.</p>
            
            <!-- 반응 통계 -->
            <div v-if="Object.keys(emojiReactions).length > 0 || isLiked" class="mb-6">
              <p class="text-gray-400 text-xs mb-3">반응</p>
              <div class="flex flex-wrap gap-2">
                <!-- 좋아요 (메인) -->
                <div
                    v-if="isLiked"
                    class="flex items-center gap-1 bg-red-500/20 text-red-500 px-3 py-1 rounded-full"
                >
                  <span class="text-lg">❤️</span>
                  <span class="text-sm font-medium">좋아요</span>
                </div>

                <!-- 이모지 반응들 -->
                <div
                    v-for="(count, emoji) in emojiReactions"
                    :key="emoji"
                    class="flex items-center gap-1 bg-zinc-800 px-3 py-1 rounded-full"
                >
                  <span class="text-lg">{{ emoji }}</span>
                  <span class="text-gray-300 text-sm">{{ count }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 좋아요 버튼 (메인) -->
          <div class="p-4 border-t border-zinc-800">
            <button
                @click="toggleLike"
                class="w-full flex items-center justify-center gap-2 py-3 rounded-xl transition-all"
                :class="isLiked
                ? 'bg-red-500 text-white hover:bg-red-600'
                : 'bg-zinc-800 text-gray-400 hover:bg-zinc-700 hover:text-white'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :fill="isLiked ? 'currentColor' : 'none'"
                   viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
              </svg>
              <span class="font-medium">{{ isLiked ? '좋아요 취소' : '좋아요' }}</span>
            </button>
          </div>

          <!-- 추가 이모지 반응 선택 -->
          <div class="p-4 border-t border-zinc-800">
            <p class="text-gray-400 text-xs mb-2">추가 반응</p>
            <div class="flex gap-1.5">
              <button
                  v-for="item in availableEmojis"
                  :key="item.emoji"
                  @click="toggleEmojiReaction(item.emoji)"
                  :class="[
                    'flex-1 h-10 rounded-lg flex items-center justify-center text-lg transition-all hover:scale-110',
                    hasEmojiReaction(item.emoji)
                      ? 'bg-blue-600 text-white ring-2 ring-blue-400'
                      : 'bg-zinc-800 hover:bg-zinc-700'
                  ]"
                  :title="item.label">
                {{ item.emoji }}
              </button>
            </div>
          </div>

          <!-- URL 복사 -->
          <div class="p-4 border-t border-zinc-800">
            <button
                @click="copyImageUrl"
                class="w-full flex items-center justify-center gap-2 py-3 bg-zinc-800 text-gray-400 hover:bg-zinc-700 hover:text-white rounded-xl transition-all"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                   stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
              </svg>
              <span class="font-medium">이미지 URL 복사</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out;
}
</style>