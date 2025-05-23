<script setup lang="ts">
import {computed, nextTick, onMounted, onUnmounted, ref, watch} from "vue";
import {infoToast} from '@/libs/toaster';
import {useClipboard} from '@vueuse/core';

import type {Post} from '@/interfaces/type';

// List.vue 데이터 설정
const props = defineProps({
  posts: Array<Post>,
  isInputAreaCollapsed: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['scrollTop']);

const postContainerRef = ref<HTMLElement | null>(null);

const handlePostScroll = (event: Event) => {
  // 현재 스크롤 위치와 요소들의 위치를 기반으로 현재 가시적인 포스트 확인
  if (!postContainerRef.value) return;

  const container = postContainerRef.value;
  const postElements = container.querySelectorAll('li');

  // 중앙에 위치한 요소 찾기
  const containerCenter = container.scrollTop + container.clientHeight / 2;

  let closestElement: Element | null = null; // 명시적 타입 지정
  let closestDistance = Infinity;

  postElements.forEach((el) => {
    const rect = el.getBoundingClientRect();
    const elementCenter = rect.top + rect.height / 2;
    const distance = Math.abs(elementCenter - window.innerHeight / 2);

    if (distance < closestDistance) {
      closestDistance = distance;
      closestElement = el;
    }
  });

  // 가장 가까운 요소에 활성화 클래스 추가
  if (closestElement) {
    postElements.forEach(el => el.classList.remove('post-active'));
    (closestElement as HTMLElement).classList.add('post-active'); // 타입 캐스팅 추가

    // 스크롤이 맨 위에 닿았을 때 이벤트 발생
    if (container.scrollTop === 0) {
      emit('scrollTop');
    }
  }
};

const handlePostWheel = (event: WheelEvent, postId: string) => {
  // 현재 포스트 인덱스 찾기
  const postIndex = props.posts?.findIndex(p => p.id === postId) ?? -1;

  // 스크롤 방향에 따라 이전/다음 포스트로 이동
  if (event.deltaY > 0 && postIndex > 0) { // 아래로 스크롤
    navigateToPost(postIndex - 1);
    event.preventDefault();
  } else if (event.deltaY < 0 && postIndex < (props.posts?.length ?? 0) - 1) { // 위로 스크롤
    navigateToPost(postIndex + 1);
    event.preventDefault();
  }
};

// 클립보드에 링크 복사
const {copy} = useClipboard();

const copyLink = (link: string) => {
  copy(link);
  infoToast("이미지 복사 완료!");
};

// 현재 표시 중인 이미지 인덱스 (각 포스트별로 관리)
const currentImageIndices = ref<Record<string, number>>({});

// 좋아요 상태 관리
const likedImages = ref<Record<string, boolean>>({});

const toggleLike = (imageId: string) => {
  likedImages.value[imageId] = !likedImages.value[imageId];
  infoToast(likedImages.value[imageId] ? "좋아요를 눌렀습니다!" : "좋아요를 취소했습니다!");
};

// 이미지 전환 방향을 추적하는 상태 변수 추가
const imageTransitionDirection = ref<Record<string, string>>({});

// 이전 이미지로 이동 함수 수정
const prevImage = (postId: string, imagesLength: number) => {
  if (!currentImageIndices.value[postId]) {
    currentImageIndices.value[postId] = 0;
  }

  // 전환 방향 설정
  imageTransitionDirection.value[postId] = 'prev';

  if (currentImageIndices.value[postId] > 0) {
    currentImageIndices.value[postId]--;
  } else {
    // 처음 이미지일 경우 마지막 이미지로 순환
    currentImageIndices.value[postId] = imagesLength - 1;
  }

  // 이미지 변경 후 크기 조정
  updateImageSize(postId);
};

// 다음 이미지로 이동 함수 수정
const nextImage = (postId: string, imagesLength: number) => {
  if (!currentImageIndices.value[postId]) {
    currentImageIndices.value[postId] = 0;
  }

  // 전환 방향 설정
  imageTransitionDirection.value[postId] = 'next';

  if (currentImageIndices.value[postId] < imagesLength - 1) {
    currentImageIndices.value[postId]++;
  } else {
    // 마지막 이미지일 경우 첫 이미지로 순환
    currentImageIndices.value[postId] = 0;
  }

  // 이미지 변경 후 크기 조정
  updateImageSize(postId);
};

const handleImageWheel = (event: WheelEvent, postId: string, imagesLength: number) => {
  if (imagesLength <= 1) return;

  // 현재 포스트 인덱스 찾기
  const postIndex = props.posts?.findIndex(p => p.id === postId) ?? -1;
  if (postIndex === -1) return;

  // 현재 포스트의 현재 이미지 인덱스
  const currentIndex = currentImageIndices.value[postId] || 0;

  // 휠 이벤트 발생 시 이미지 이동
  if (event.deltaY > 0) { // 아래로 스크롤
    // 마지막 이미지이고 다음 포스트가 있을 때
    if (currentIndex === imagesLength - 1 && postIndex > 0) {
      // 다음 포스트로 이동
      navigateToPost(postIndex - 1); // 리스트가 역순이므로 인덱스는 감소
    } else {
      nextImage(postId, imagesLength); // 일반적인 다음 이미지
    }
  } else { // 위로 스크롤
    // 첫 번째 이미지이고 이전 포스트가 있을 때
    if (currentIndex === 0 && postIndex < (props.posts?.length ?? 0) - 1) {
      // 이전 포스트로 이동
      navigateToPost(postIndex + 1); // 리스트가 역순이므로 인덱스는 증가
    } else {
      prevImage(postId, imagesLength); // 일반적인 이전 이미지
    }
  }

  // 페이지 스크롤 방지
  event.preventDefault();
};

// 포스트 간 이동을 위한 함수
const navigateToPost = (postIndex: number) => {
  if (!props.posts || postIndex < 0 || postIndex >= props.posts.length) return;

  const targetPost = props.posts[postIndex];

  // 해당 포스트 요소 찾기
  const postElements = postContainerRef.value?.querySelectorAll('li');
  if (postElements && postElements[postIndex]) {
    // 스무스한 스크롤이 아닌 즉시 이동으로 변경하여 중간에 다른 포스트가 보이지 않도록 함
    postElements[postIndex].scrollIntoView({
      behavior: 'auto',  // 'smooth'에서 'auto'로 변경
      block: 'center'    // 'start'에서 'center'로 변경하여 화면 중앙에 위치
    });

    // 스크롤 후 애니메이션 효과를 위한 트랜지션 클래스 추가
    setTimeout(() => {
      postElements[postIndex].classList.add('post-active');
    }, 50);

    // 포커스 설정
    setFocusedPost(targetPost.id);

    // 포스트의 첫 번째 이미지부터 시작
    if (targetPost.postImages && targetPost.postImages.length > 0) {
      currentImageIndices.value[targetPost.id] = 0;
    }
  }
};

// 키보드 이벤트 처리
const focusedPostId = ref<string | null>(null);

const handleKeyDown = (event: KeyboardEvent) => {
  if (!focusedPostId.value) return;

  const post = props.posts?.find(p => p.id === focusedPostId.value);
  if (!post || !post.postImages || post.postImages.length <= 1) return;

  if (event.key === 'ArrowLeft') {
    prevImage(focusedPostId.value, post.postImages.length);
  } else if (event.key === 'ArrowRight') {
    nextImage(focusedPostId.value, post.postImages.length);
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
  window.addEventListener('keydown', handleKeyDown);

  // 초기 로드 시 첫 번째 포스트 활성화
  nextTick(() => {
    if (postContainerRef.value) {
      const firstPost = postContainerRef.value.querySelector('li');
      if (firstPost) {
        firstPost.classList.add('post-active');
      }
    }
  });

  watch(
      () => props.posts,
      (newPosts) => {
        if (newPosts && newPosts.length > 0) {
          setTimeout(() => {
            scrollToBottom(false); // 초기 로드 시 즉시 스크롤 (애니메이션 없이)

            // 스크롤 후 첫 번째 포스트 활성화
            nextTick(() => {
              if (postContainerRef.value) {
                const posts = postContainerRef.value.querySelectorAll('li');
                if (posts.length > 0) {
                  posts.forEach(el => el.classList.remove('post-active'));
                  posts[posts.length - 1].classList.add('post-active');
                }
              }
            });
          }, 100);
        }
      },
      {deep: true, immediate: true});
});

// 현재 표시할 이미지 가져오기
const getCurrentImage = (post: Post) => {
  if (!post.postImages || post.postImages.length === 0) return null;

  if (!currentImageIndices.value[post.id]) {
    currentImageIndices.value[post.id] = 0;
  }

  return post.postImages[currentImageIndices.value[post.id]];
};

// 이미지 변경시 애니메이션 효과를 위한 키 관리
const imageKeys = ref<Record<string, number>>({});
const getImageKey = (postId: string) => {
  if (!imageKeys.value[postId]) {
    imageKeys.value[postId] = 0;
  }
  return imageKeys.value[postId];
};

// 이미지 변경시 키 업데이트
const updateImageKey = (postId: string) => {
  if (!imageKeys.value[postId]) {
    imageKeys.value[postId] = 0;
  }
  imageKeys.value[postId]++;
};

// 이미지 변경 시 사이즈 조정 문제를 해결하기 위한 함수
const updateImageSize = (postId: string) => {
  // 이미지 변경 시 크기 재조정이 필요한 경우를 대비한 호출
  nextTick(() => {
    // DOM 업데이트 후 실행되는 코드
    updateImageKey(postId); // 이미지 키 업데이트로 애니메이션 적용

    // 이미지 로드 완료 후 포스트 컨테이너 크기 조정을 위한 이벤트
    const imgElement = document.querySelector(`li[data-post-id="${postId}"] .shorts-image`);
    if (imgElement) {
      imgElement.addEventListener('load', () => {
        // 이미지 로드 완료 후 필요한 경우 컨테이너 높이 조정
        handlePostScroll({} as Event);
      }, {once: true});
    }
  });
};

// 포스트 포커스 설정
const setFocusedPost = (postId: string | null) => {
  focusedPostId.value = postId;
};

defineExpose({scrollToBottom});
</script>

<template>
  <div
      ref="postContainerRef"
      class="border border-gray-400 border-md rounded-md p-0 m-4"
  >
    <ul class="w-full flex flex-col-reverse snap-y snap-mandatory scroll-custom" @scroll="handlePostScroll">
      <li class="bg-zinc-800 snap-center w-full"
          v-for="post in posts"
          :key="post.id"
          :data-post-id="post.id"
          @mouseenter="setFocusedPost(post.id)"
          @mouseleave="setFocusedPost(null)"
          @wheel.prevent="(e) => handlePostWheel(e, post.id)">

        <div class="post-content">
          <!-- 이미지 섹션 -->
          <div class="flex-grow flex items-center justify-center" v-if="post.postImages && post.postImages.length > 0">
            <div class="shorts-container">
              <!-- 메인 이미지 -->
              <div
                  class="shorts-image-container"
                  @wheel.stop="(e) => handleImageWheel(e, post.id, post.postImages.length)"
              >

                <transition-group
                    :name="imageTransitionDirection[post.id] === 'next' ? 'image' : 'prev-image'"
                    mode="out-in"
                >
                  <img
                      :key="currentImageIndices[post.id] || 0"
                      :src="getCurrentImage(post)?.url"
                      alt="Post Image"
                      class="shorts-image"
                  />
                </transition-group>

                <!-- 이미지 위에 오버레이되는 포스트 내용 -->
                <div :class="['post-overlay', !post.content ? 'empty-content' : '']">
                  <div class="post-header">
                    <p class="text-xl text-white mr-3">{{ post.writer }}</p>
                    <p class="text-xs">{{ getWriteTime(post.createDate) }}</p>
                  </div>
                  <p v-if="post.content" class="post-content-text">{{ post.content }}</p>
                </div>

                <!-- 이미지 카운터 표시 -->
                <div v-if="post.postImages.length > 1" class="image-counter">
                  {{ (currentImageIndices[post.id] || 0) + 1 }}/{{ post.postImages.length }}
                </div>
              </div>

              <!-- 오른쪽 액션 버튼들 -->
              <div class="shorts-actions">
                <!-- 좋아요 버튼 -->
                <button
                    class="action-button"
                    @click="toggleLike(getCurrentImage(post)?.id || `img-${post.id}-0`)"
                    :class="{ 'liked': likedImages[getCurrentImage(post)?.id || `img-${post.id}-0`] }"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                       class="action-icon"
                       :class="{ 'text-red-500 fill-red-500': likedImages[getCurrentImage(post)?.id || `img-${post.id}-0`] }">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                  </svg>
                  <span class="action-label">좋아요</span>
                </button>

                <!-- 이전 이미지 버튼 -->
                <button
                    v-if="post.postImages.length > 1"
                    class="action-button"
                    @click="prevImage(post.id, post.postImages.length)"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                       class="action-icon">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
                  </svg>
                  <span class="action-label">이전</span>
                </button>

                <!-- 다음 이미지 버튼 -->
                <button
                    v-if="post.postImages.length > 1"
                    class="action-button"
                    @click="nextImage(post.id, post.postImages.length)"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                       class="action-icon">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                  </svg>
                  <span class="action-label">다음</span>
                </button>

                <!-- 복사 버튼 -->
                <button
                    class="action-button"
                    @click="copyLink(getCurrentImage(post)?.url || '')"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                       class="action-icon">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M8 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3"/>
                  </svg>
                  <span class="action-label">복사</span>
                </button>
              </div>

              <!-- 이미지 인디케이터 -->
              <div v-if="post.postImages.length > 1" class="image-indicators">
                <button
                    v-for="(image, index) in post.postImages"
                    :key="image.id ?? index"
                    @click="currentImageIndices[post.id] = index"
                    :class="['w-2', 'h-2', 'rounded-full', 'mx-1', 'transition-all', 'duration-200',
                    index === (currentImageIndices[post.id] || 0) ? 'bg-white scale-110' : 'bg-gray-400 bg-opacity-60']"
                    :aria-label="`이미지 ${index + 1}로 이동`"
                ></button>
              </div>
            </div>
          </div>

          <!-- 이미지가 없는 경우 빈 공간 채우기 -->
          <div class="flex-grow" v-else></div>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.border-md {
  height: calc(100% - 1rem); /* 패딩을 고려한 높이 조정 */
  position: relative;
  overflow: visible; /* 넘치는 콘텐츠 표시 */
  padding: 0.5rem 0; /* 위아래 패딩 추가 */
}

ul {
  position: relative;
  width: 100%;
  height: 100%;
  overflow-y: auto; /* 스크롤은 ul에서 처리 */
  margin: 0;
  padding: 0;
}

.scroll-custom {
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
  height: 100%;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a;
  position: relative;
  padding: 0.5rem 0; /* 스크롤시 위아래 여백 확보 */
}

/* li 요소의 높이와 패딩 조정 */
ul > li {
  height: 100%;
  min-height: v-bind('isInputAreaCollapsed ? "50rem" : "40rem"');
  width: 100%;
  display: flex;
  flex-direction: column;
  scroll-snap-align: center;
  position: relative;
  overflow: hidden;
  padding: 1rem;
  box-sizing: border-box;
}

/* 이미지와 텍스트가 늘어날 수 있도록 수정 */
.post-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  height: 100%; /* 높이 100% 설정 */
}

.shorts-container {
  position: relative;
  width: 100%;
  max-width: 30rem;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  height: auto;
  max-height: calc(100% - 8rem); /* 헤더와 마진 고려하여 조정 */
}

/* 이미지 전환을 위한 개선된 애니메이션 */
.shorts-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s, opacity 0.3s;
  animation: fadeIn 0.3s ease-in-out;
}

.post-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden; /* 넘치는 콘텐츠는 숨김 */
}

.shorts-image-container {
  width: 100%;
  height: 0;
  padding-bottom: 125%; /* 4:5 비율 유지 */
  position: relative;
  overflow: hidden;
  border-radius: 8px;
}

.shorts-image-container:hover .shorts-image {
  transform: scale(1.03);
}

/* 이미지 카운터 */
.image-counter {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

/* 쇼츠 액션 버튼 컨테이너 */
.shorts-actions {
  display: flex;
  flex-direction: column;
  position: absolute;
  right: -60px;
  top: 50%;
  transform: translateY(-50%);
  gap: 16px;
}

/* 액션 버튼 */
.action-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  transition: transform 0.2s;
}

.action-button:hover {
  transform: scale(1.1);
}

/* 액션 아이콘 */
.action-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

/* 액션 라벨 */
.action-label {
  font-size: 12px;
  white-space: nowrap;
}

/* 좋아요 활성화 상태 */
.liked .action-icon {
  color: #ef4444;
  fill: #ef4444;
}

/* 이미지 인디케이터 */
.image-indicators {
  position: absolute;
  bottom: 12px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.scroll-custom {
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
  height: 100%;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #52525b #27272a;
  position: relative;
}

.post-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7) 40%);
  color: white;
  z-index: 10;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.post-content-text {
  margin-bottom: 1rem;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.8);
}

.post-overlay.empty-content {
  padding-top: 3rem; /* 내용이 없을 때도 그라데이션 배경만 표시 */
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(1.03);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideIn {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

</style>