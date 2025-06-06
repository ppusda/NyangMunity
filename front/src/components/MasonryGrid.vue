<script setup>
import {onMounted, ref, nextTick, watch} from "vue";

import Masonry from "masonry-layout";
import imagesLoaded from "imagesloaded";

const props = defineProps({
  images: Array, // Masonry에 표시할 이미지 리스트
});

const masonryContainer = ref(null);
let masonryInstance = null;

const emit = defineEmits(["select-image"]);

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

// 첫 로딩 시 이미지가 다 뜨기 전에 Masonry 실행 방지
onMounted(async () => {
  await nextTick();
  imagesLoaded(masonryContainer.value, () => {
    initMasonry();
  });
});

// items 변경 시 Masonry 업데이트
watch(
    () => props.images,
    async () => {
      await nextTick(); // DOM 업데이트 이후 실행
      if (masonryInstance) {
        imagesLoaded(masonryContainer.value, () => {
          masonryInstance.reloadItems();
          masonryInstance.layout();
        });
      }
    },
    {deep: true}
);


const selectImage = (item) => {
  emit("select-image", item);
};

</script>

<template>
  <div ref="masonryContainer" class="masonry w-full h-auto">
    <div
        v-for="item in images"
        :key="item.id"
        class="masonry-item group relative cursor-pointer"
        @click="selectImage(item)"
    >
      <img :src="item.url" class="w-full h-auto rounded-md"/>
      <div
          class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-md"
      >
        <p class="text-xs text-white">이미지 선택</p>
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
