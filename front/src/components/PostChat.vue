<script setup lang="ts">

import {computed, onMounted, onUnmounted, ref, watch} from "vue";
import {infoToast} from '@/libs/toaster';
import {useClipboard} from '@vueuse/core';

import type {Post} from '@/interfaces/type';

// List.vue 데이터 설정
const props = defineProps({
  posts: Array<Post>,
});
const emit = defineEmits(['scrollTop']);

const postContainerRef = ref<HTMLElement | null>(null);

const handlePostScroll = (event: Event) => {
  if (postContainerRef.value && postContainerRef.value.scrollTop === 0) {
    emit('scrollTop');
  }
};

// 클립보드에 링크 복사
const {copy} = useClipboard();

const copyLink = (link: string) => {
  copy(link);
  infoToast("이미지 복사 완료!");
};

// 모달 상태 관리
const showModal = ref(false);
const currentPostIndex = ref(-1);
const currentImageIndex = ref(0);

// 현재 포스트의 이미지 배열
const currentImages = computed(() => {
  if (currentPostIndex.value >= 0 && props.posts && props.posts[currentPostIndex.value]) {
    return props.posts[currentPostIndex.value].postImages || [];
  }
  return [];
});

// 현재 이미지 URL
const currentImage = computed(() => {
  if (currentImages.value.length > 0 && currentImageIndex.value < currentImages.value.length) {
    return currentImages.value[currentImageIndex.value].url;
  }
  return '';
});

// 이미지 클릭 핸들러
const openImageModal = (postIndex: number, imageIndex: number) => {
  if (props.posts && props.posts[postIndex]) {
    currentPostIndex.value = postIndex;
    currentImageIndex.value = imageIndex;
    showModal.value = true;
  }
};
// 모달 닫기
const closeModal = () => {
  showModal.value = false;
  currentPostIndex.value = -1;
  currentImageIndex.value = 0;
};

// 이전 이미지로 이동
const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  } else {
    // 처음 이미지일 경우 마지막 이미지로 순환
    currentImageIndex.value = currentImages.value.length - 1;
  }
};

// 다음 이미지로 이동
const nextImage = () => {
  if (currentImageIndex.value < currentImages.value.length - 1) {
    currentImageIndex.value++;
  } else {
    // 마지막 이미지일 경우 첫 이미지로 순환
    currentImageIndex.value = 0;
  }
};

// 특정 이미지로 이동
const goToImage = (index: number) => {
  currentImageIndex.value = index;
};

// 키보드 이벤트 처리
const handleKeyDown = (event: KeyboardEvent) => {
  if (!showModal.value) return;

  if (event.key === 'ArrowLeft') {
    prevImage();
  } else if (event.key === 'ArrowRight') {
    nextImage();
  } else if (event.key === 'Escape') {
    closeModal();
  }
};

// 작성 시간 표시
const getWriteTime = (time: string) => {
  const now = new Date();
  const writeTime = new Date(time);

  // 로컬 시간대 기준으로 날짜 비교
  const nowDate = now.toLocaleDateString();
  const writeDate = writeTime.toLocaleDateString();

  const timeDifference = now.getTime() - writeTime.getTime();
  const seconds = Math.floor(timeDifference / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  const formatTime = (date: Date) => {
    const hour = date.getHours();
    const minute = date.getMinutes();
    const period = hour >= 12 ? '오후' : '오전';
    const formattedHour = hour % 12 || 12;
    return `${period} ${formattedHour}:${minute < 10 ? '0' + minute : minute}`;
  };

  // 작성 날짜가 오늘인 경우
  if (writeDate === nowDate) {
    return `오늘 ${formatTime(writeTime)}`;
  }
  // 작성 날짜가 어제인 경우
  else if (writeDate === new Date(now.getTime() - 86400000).toLocaleDateString()) {
    return `어제 ${formatTime(writeTime)}`;
  }
  // 그 외 날짜 표시
  else {
    const year = writeTime.getFullYear();
    const month = String(writeTime.getMonth() + 1).padStart(2, '0');
    const date = String(writeTime.getDate()).padStart(2, '0');
    return `${year}.${month}.${date}. ${formatTime(writeTime)}`;
  }
};

const scrollToBottom = (smooth = true) => {
  if (postContainerRef.value) {
    postContainerRef.value.scrollTo({
      top: postContainerRef.value.scrollHeight,
      behavior: smooth ? 'smooth' : 'auto'
    });
  }
};


// 컴포넌트 마운트 시 키보드 이벤트 리스너 등록
onMounted(() => {
  window.addEventListener('keydown', handleKeyDown);

  watch(
      () => props.posts,
      (newPosts) => {
        if (newPosts && newPosts.length > 0) {
          setTimeout(() => {
            scrollToBottom(true);
          }, 100);
        }
      },
      {deep: true, immediate: true}); // deep 옵션으로 배열 내부 변화도 감지
});

// 컴포넌트 언마운트 시 키보드 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown);
});

defineExpose({scrollToBottom});
</script>

<template>
  <div
      ref="postContainerRef"
      class="border border-gray-400 border-md rounded-md overflow-auto p-4 m-4 scroll-custom h-[35rem]"
      @scroll="handlePostScroll"
  >
    <ul class="w-full flex flex-col-reverse">
      <li class="p-4 bg-zinc-800 rounded-md" v-for="post in posts" :key="post.id">
        <div class="flex flex-row text-center items-center">
          <p class="text-xl text-white mr-3">{{ post.writer }}</p>
          <p class="text-xs">{{ getWriteTime(post.createDate) }}</p>
        </div>

        <div class="mt-3">
          <p class="text-white mr-3">{{ post.content }}</p>
        </div>

        <!-- 이미지 섹션 -->
        <div class="mt-3" v-if="post.postImages && post.postImages.length > 0">
          <div :class="['grid-layout', post.postImages.length === 2 ? 'two-images' : 'three-images']">
            <template v-for="(image, imageIndex) in post.postImages.slice(0, 3)" :key="image.id">
              <div
                  :class="['relative','image-container',
                  post.postImages.length === 2 ? `two-image-${imageIndex}`: (imageIndex === 0 ? 'first-image' : imageIndex === 1 ? 'second-image' : 'third-image')]"
                  @click="openImageModal(props.posts?.indexOf(post) ?? 0, imageIndex)"
              >
                <img
                    :src="image.url"
                    alt="Type Image"
                    class="absolute inset-0 w-full h-full object-cover rounded-md"
                />
                <!-- 더보기 표시 -->
                <div
                    v-if="imageIndex === 2 && post.postImages.length > 3"
                    class="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center text-white font-bold text-lg rounded-md"
                >
                  +{{ post.postImages.length - 3 }}
                </div>
              </div>
            </template>
          </div>
        </div>
      </li>
    </ul>
  </div>

  <!-- 이미지 케러셀 모달 -->
  <div
      v-if="showModal"
      class="fixed inset-0 bg-black bg-opacity-90 flex items-center justify-center z-50 transition-opacity duration-200"
      @click="closeModal"
  >
    <!-- 메인 이미지 컨테이너 -->
    <div class="relative max-w-4xl max-h-screen p-4 w-full" @click.stop>
      <!-- 왼쪽 화살표 -->
      <button
          @click.stop="prevImage"
          class="absolute left-2 top-1/2 -translate-y-1/2 bg-black bg-opacity-50 text-white w-12 h-12 rounded-full flex items-center justify-center z-10 hover:bg-opacity-70 transition-all duration-200"
          aria-label="이전 이미지"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>

      <!-- 이미지 -->
      <div class="flex justify-center items-center h-full">
        <img
            :src="currentImage"
            alt="Full size image"
            class="max-w-full max-h-[50vh] object-contain transition-opacity duration-300"
            :key="currentImageIndex"
        />
      </div>

      <!-- 오른쪽 화살표 -->
      <button
          @click.stop="nextImage"
          class="absolute right-2 top-1/2 -translate-y-1/2 bg-black bg-opacity-50 text-white w-12 h-12 rounded-full flex items-center justify-center z-10 hover:bg-opacity-70 transition-all duration-200"
          aria-label="다음 이미지"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
        </svg>
      </button>

      <!-- 컨트롤 패널 - 상단 오른쪽에 배치 -->
      <div class="absolute top-4 right-4 flex space-x-2">
        <button
            @click.stop="copyLink(currentImage)"
            class="bg-black bg-opacity-60 text-white px-3 py-2 rounded-md flex items-center justify-center hover:bg-opacity-80 transition-all duration-200"
            aria-label="이미지 링크 복사"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24"
               stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3"/>
          </svg>
        </button>

        <!-- 닫기 버튼 -->
        <button
            @click.stop="closeModal"
            class="bg-black bg-opacity-60 text-white px-3 py-2 rounded-md flex items-center justify-center hover:bg-opacity-80 transition-all duration-200"
            aria-label="모달 닫기"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24"
               stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- 인디케이터 -->
      <div class="absolute bottom-6 left-0 right-0 flex justify-center space-x-2">
        <button
            v-for="(image, index) in currentImages"
            :key="image.id ?? index"
            @click.stop="goToImage(index)"
            :class="['w-2.5', 'h-2.5', 'rounded-full', 'transition-all', 'duration-200',
            index === currentImageIndex ? 'bg-white scale-110' : 'bg-gray-400 bg-opacity-60']"
            :aria-label="`이미지 ${index + 1}로 이동`"
        ></button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 이미지 그리드 레이아웃 - 공통 */
.grid-layout {
  display: grid;
  gap: 8px;
  width: 100%;
  max-width: 600px; /* 최대 너비 제한 */
  margin: 0; /* 왼쪽 정렬로 변경 */
}

/* 이미지 그리드 레이아웃 - 3개 이미지용 */
.three-images {
  grid-template-columns: 3fr 2fr;
  grid-template-rows: 1fr 1fr;
  aspect-ratio: 5/3; /* 가로:세로 비율 조정 */
}

/* 이미지 그리드 레이아웃 - 2개 이미지용 */
.two-images {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr;
  aspect-ratio: 2/1; /* 가로:세로 비율 조정 */
}

/* 첫 번째, 두 번째, 세 번째 이미지 스타일 */
.first-image {
  grid-column: 1;
  grid-row: 1 / span 2;
  aspect-ratio: 1/1; /* 정사각형 비율 유지 */
}

.second-image {
  grid-column: 2;
  grid-row: 1;
}

.third-image {
  grid-column: 2;
  grid-row: 2;
}

/* 2개 이미지일 때 스타일 */
.two-image-0, .two-image-1 {
  aspect-ratio: 1/1; /* 정사각형 비율 유지 */
}

.two-image-0 {
  grid-column: 1;
  grid-row: 1;
}

.two-image-1 {
  grid-column: 2;
  grid-row: 1;
}

/* 이미지 컨테이너 공통 스타일 */
.image-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  transition: transform 0.2s;
}

/* 호버 효과 */
.image-container:hover {
  transform: scale(0.98);
}

/* 이미지 정사각형으로 맞추기 */
img {
  object-fit: cover;
  width: 100%;
  height: 100%;
}

/* 모달 내의 이미지는 원본 비율 유지 */
.max-w-full.max-h-\[50vh\] {
  object-fit: contain;
}

/* 케러셀 화살표 애니메이션 */
button:hover svg {
  transform: scale(1.1);
  transition: transform 0.2s;
}

/* 모달 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.fixed {
  animation: fadeIn 0.2s ease-in-out;
}

.scroll-custom {
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a; /* 스크롤바 색상과 트랙 색상 */
}

</style>