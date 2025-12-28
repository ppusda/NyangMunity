<script setup lang="ts">
import {ref, computed, watch} from 'vue';
import {useClipboard} from '@vueuse/core';
import {infoToast, warningToast} from '@/libs/toaster';
import axiosClient from '@/libs/axiosClient';
import type {Post} from '@/interfaces/type';
import store from '@/stores/store';

const props = defineProps<{
  post: Post | null;
}>();

const emit = defineEmits(['close']);

// 로그인 상태
const isLogin = computed(() => store.state.isLogin);

// 현재 이미지 인덱스
const currentIndex = ref(0);

// 현재 이미지
const currentImage = computed(() => {
  if (!props.post?.postImages || props.post.postImages.length === 0) return null;
  return props.post.postImages[currentIndex.value];
});

// 좋아요 상태
const likedImages = ref<Record<string, boolean>>({});

// 초기화 함수
const initializeLikes = () => {
  if (props.post?.postImages) {
    props.post.postImages.forEach(img => {
      if (img.id) {
        likedImages.value[img.id] = img.likeState;
      }
    });
  }
  currentIndex.value = 0;
};

// props.post가 변경될 때마다 초기화
watch(() => props.post, () => {
  if (props.post) {
    initializeLikes();
  }
}, {immediate: true});

// 현재 이미지 좋아요 상태
const isLiked = computed(() => {
  const imageId = currentImage.value?.id;
  return imageId ? likedImages.value[imageId] || false : false;
});

// 이전 이미지
const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};

// 다음 이미지
const nextImage = () => {
  if (props.post?.postImages && currentIndex.value < props.post.postImages.length - 1) {
    currentIndex.value++;
  }
};

// 좋아요 토글
const toggleLike = async () => {
  if (!isLogin.value) {
    warningToast('로그인이 필요합니다.');
    return;
  }

  const imageId = currentImage.value?.id;
  if (!imageId) {
    warningToast('이미지 정보를 찾을 수 없습니다.');
    return;
  }

  try {
    const response = await axiosClient.post('/images/likes', {imageId});
    likedImages.value[imageId] = response.data.state;
    infoToast(response.data.state ? '좋아요를 눌렀습니다!' : '좋아요를 취소했습니다!');
  } catch (error) {
    warningToast('좋아요 처리에 실패했습니다.');
  }
};

// URL 복사
const {copy} = useClipboard();
const copyImageUrl = () => {
  const url = currentImage.value?.url;
  if (!url) {
    warningToast('이미지 URL을 찾을 수 없습니다.');
    return;
  }
  copy(url);
  infoToast('이미지 URL이 복사되었습니다!');
};

// 모달 닫기
const closeModal = () => {
  emit('close');
};

// 키보드 이벤트
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape') closeModal();
  if (e.key === 'ArrowLeft') prevImage();
  if (e.key === 'ArrowRight') nextImage();
};

// 작성 시간 포맷
const formatTime = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
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
    <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm"
        @click.self="closeModal"
        @keydown="handleKeydown"
        tabindex="0"
    >
      <!-- 모달 컨테이너 -->
      <div
          class="relative w-full max-w-5xl h-full max-h-[90vh] flex flex-col md:flex-row bg-zinc-900 rounded-2xl overflow-hidden shadow-2xl animate-fadeIn">

        <!-- 왼쪽: 이미지 영역 -->
        <div class="relative flex-1 bg-black flex items-center justify-center">
          <img
              v-if="currentImage"
              :src="currentImage.url"
              class="max-w-full max-h-full object-contain"
          />

          <!-- 이미지 네비게이션 -->
          <div v-if="post && post.postImages && post.postImages.length > 1"
               class="absolute inset-0 flex items-center justify-between px-4 pointer-events-none">
            <button
                v-if="currentIndex > 0"
                @click="prevImage"
                class="w-10 h-10 rounded-full bg-black/50 backdrop-blur-sm text-white flex items-center justify-center hover:bg-black/70 transition-all pointer-events-auto"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                   stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>
            <button
                v-if="post.postImages && currentIndex < post.postImages.length - 1"
                @click="nextImage"
                class="w-10 h-10 rounded-full bg-black/50 backdrop-blur-sm text-white flex items-center justify-center hover:bg-black/70 transition-all pointer-events-auto"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                   stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>

          <!-- 이미지 카운터 -->
          <div v-if="post && post.postImages && post.postImages.length > 1"
               class="absolute top-4 right-4 px-3 py-1 bg-black/50 backdrop-blur-sm text-white text-sm rounded-full">
            {{ currentIndex + 1 }} / {{ post.postImages.length }}
          </div>

          <!-- 닫기 버튼 -->
          <button
              @click="closeModal"
              class="absolute top-4 left-4 w-10 h-10 rounded-full bg-black/50 backdrop-blur-sm text-white flex items-center justify-center hover:bg-black/70 transition-all"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                 stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <!-- 오른쪽: 정보 영역 -->
        <div class="w-full md:w-96 flex flex-col bg-zinc-900">
          <!-- 헤더 -->
          <div class="p-6 border-b border-zinc-800">
            <div class="flex items-center gap-3">
              <div
                  class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white text-lg font-bold">
                {{ post?.writer?.charAt(0).toUpperCase() }}
              </div>
              <div>
                <p class="text-white font-semibold">{{ post?.writer }}</p>
                <p class="text-gray-400 text-sm">{{ formatTime(post?.createDate || '') }}</p>
              </div>
            </div>
          </div>

          <!-- 본문 -->
          <div class="flex-1 overflow-y-auto p-6">
            <div class="mb-6">
              <h3 class="text-white font-semibold mb-2">설명</h3>
              <p v-if="post?.content" class="text-gray-300">{{ post.content }}</p>
              <p v-else class="text-gray-500 italic">설명이 없습니다.</p>
            </div>
          </div>

          <!-- 액션 버튼 -->
          <div class="p-6 border-t border-zinc-800">
            <div class="flex gap-3">
              <button
                  @click="toggleLike"
                  :class="[
                  'flex-1 flex items-center justify-center gap-2 py-3 rounded-xl transition-all font-medium',
                  isLiked
                    ? 'bg-red-500 text-white hover:bg-red-600'
                    : 'bg-zinc-800 text-white hover:bg-zinc-700'
                ]"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :fill="isLiked ? 'currentColor' : 'none'"
                     viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
                좋아요
              </button>
              <button
                  @click="copyImageUrl"
                  class="flex-1 flex items-center justify-center gap-2 py-3 bg-zinc-800 text-white hover:bg-zinc-700 rounded-xl transition-all font-medium"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                </svg>
                URL 복사
              </button>
            </div>
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

/* 스크롤바 커스텀 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #18181b;
}

::-webkit-scrollbar-thumb {
  background: #3f3f46;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #52525b;
}
</style>