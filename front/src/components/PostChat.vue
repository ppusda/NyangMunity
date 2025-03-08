<script setup lang="ts">

const props = defineProps({
  posts: Array,
});

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
            <template v-for="(image, index) in post.postImages.slice(0, 3)" :key="image.id">
              <div
                  :class="[
                    'relative',
                    'image-container',
                    post.postImages.length === 2
                      ? `two-image-${index}`
                      : (index === 0 ? 'first-image' : index === 1 ? 'second-image' : 'third-image')
                  ]"
              >
                <img
                    :src="image.url"
                    alt="Post Image"
                    class="absolute inset-0 w-full h-full object-cover rounded-md"
                />
                <!-- 더보기 표시 -->
                <div
                    v-if="index === 2 && post.postImages.length > 3"
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

/* 첫 번째 이미지 (큰 정사각형) - 3개 이미지일 때 */
.first-image {
  grid-column: 1;
  grid-row: 1 / span 2;
  aspect-ratio: 1/1; /* 정사각형 비율 유지 */
}

/* 두 번째 이미지 (첫 번째 이미지 오른쪽 위) - 3개 이미지일 때 */
.second-image {
  grid-column: 2;
  grid-row: 1;
}

/* 세 번째 이미지 (두 번째 이미지 아래) - 3개 이미지일 때 */
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
}

/* 이미지 정사각형으로 맞추기 */
img {
  object-fit: cover;
  width: 100%;
  height: 100%;
}
</style>