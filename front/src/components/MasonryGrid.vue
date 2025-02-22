<script setup>
import { onMounted, ref, nextTick, watch } from "vue";
import { toast } from "vue3-toastify";
import { useClipboard } from "@vueuse/core";

import Masonry from "masonry-layout";
import imagesLoaded from "imagesloaded";

const props = defineProps({
  items: Array, // Masonry에 표시할 이미지 리스트
});

const masonryContainer = ref(null);
let masonryInstance = null;

// Masonry 초기화 함수
const initMasonry = () => {
  if (!masonryContainer.value) return;

  masonryInstance = new Masonry(masonryContainer.value, {
    itemSelector: ".masonry-item",
    columnWidth: ".masonry-item",
    percentPosition: true,
    gutter: 10,
  });

  imagesLoaded(masonryContainer.value, () => {
    masonryInstance.layout();
  });
};

// Masonry 적용
onMounted(async () => {
  await nextTick();
  initMasonry();
});

// items 변경 시 Masonry 업데이트
watch(
    () => props.items,
    () => {
      nextTick(() => {
        if (masonryInstance) {
          masonryInstance.reloadItems();
          masonryInstance.layout();
        }
      });
    },
    { deep: true }
);

// 클립보드 복사 기능
const { copy } = useClipboard();
const copyLink = (link) => {
  copy(link);
  toast("이미지 복사 완료!", {
    autoClose: 2000,
    theme: "dark",
  });
};
</script>

<template>
  <div ref="masonryContainer" class="masonry w-full h-auto">
    <div
        v-for="item in items"
        :key="item.id"
        class="masonry-item group relative cursor-pointer"
        @click="copyLink(item.url)"
    >
      <img :src="item.url" class="w-full h-auto rounded-md" />

      <!-- 마우스를 올리면 어두워지며 텍스트 표시 -->
      <div
          class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-md"
      >
        <p class="text-xs text-white">클릭하여 링크 복사</p>
      </div>
    </div>
  </div>
</template>

<style>
.masonry {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.masonry-item {
  width: 48%; /* 2줄로 정렬 */
  margin-bottom: 10px;
  position: relative;
  overflow: hidden;
}

.group {
  position: relative;
}

.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

.group-hover\:opacity-100 {
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}
</style>
