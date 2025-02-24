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
      class="border border-gray-400 border-md rounded-md overflow-auto p-4 m-4 scroll-custom h-[36rem]"
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
      </li>
    </ul>
  </div>
</template>