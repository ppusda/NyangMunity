<template>
  <div class="image-card">
    <!-- 이미지 영역 -->
    <div class="image-wrapper" @click="$emit('image-click', image.id)">
      <img
        :src="image.url"
        :alt="image.name || '고양이 이미지'"
        class="image"
        loading="lazy"
      />
      <div class="image-overlay">
        <button class="view-button">자세히 보기</button>
      </div>
    </div>

    <!-- 정보 영역 -->
    <div class="card-footer">
      <!-- 좋아요 & 조회수 -->
      <div class="stats">
        <button
          class="stat-button like-button"
          :class="{ liked: isLiked }"
          @click.stop="handleLike"
        >
          <!-- 좋아요 (filled heart) -->
          <svg v-if="isLiked" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
            <path d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z" />
          </svg>
          <!-- 좋아요 (outline heart) -->
          <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z" />
          </svg>
          <span class="count">{{ formatCount(image.likesCount) }}</span>
        </button>

        <div class="stat-item">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span class="count">{{ formatCount(image.viewsCount) }}</span>
        </div>

        <button
          class="stat-button save-button"
          :class="{ saved: isSaved }"
          @click.stop="handleSave"
          title="컬렉션에 저장"
        >
          <!-- 저장됨 (filled bookmark) -->
          <svg v-if="isSaved" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
            <path fill-rule="evenodd" d="M6.32 2.577a49.255 49.255 0 0111.36 0c1.497.174 2.57 1.46 2.57 2.93V21a.75.75 0 01-1.085.67L12 18.089l-7.165 3.583A.75.75 0 013.75 21V5.507c0-1.47 1.073-2.756 2.57-2.93z" clip-rule="evenodd" />
          </svg>
          <!-- 저장 안됨 (outline bookmark) -->
          <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z" />
          </svg>
        </button>
      </div>

      <!-- 태그 영역 -->
      <div v-if="image.tags && image.tags.length > 0" class="tags">
        <button
          v-for="tag in image.tags"
          :key="tag.id"
          class="tag-chip"
          @click.stop="$emit('tag-click', tag.name)"
        >
          #{{ tag.name }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { ImageTag } from '@/interfaces/Tag';

interface ImageData {
  id: string;
  name?: string;
  url: string;
  likesCount: number;
  viewsCount: number;
  tags?: ImageTag[];
}

interface Props {
  image: ImageData;
  liked?: boolean;
  saved?: boolean;
}

interface Emits {
  (e: 'like', imageId: string): void;
  (e: 'save', imageId: string): void;
  (e: 'tag-click', tagName: string): void;
  (e: 'image-click', imageId: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  liked: false,
  saved: false,
});

const emit = defineEmits<Emits>();

const isLiked = ref(props.liked);
const isSaved = ref(props.saved);

// 좋아요 핸들러
const handleLike = () => {
  isLiked.value = !isLiked.value;
  emit('like', props.image.id);
};

// 저장 핸들러
const handleSave = () => {
  isSaved.value = !isSaved.value;
  emit('save', props.image.id);
};

// 숫자 포맷팅 (1000 -> 1K)
const formatCount = (count: number): string => {
  if (count >= 1000000) {
    return `${(count / 1000000).toFixed(1)}M`;
  }
  if (count >= 1000) {
    return `${(count / 1000).toFixed(1)}K`;
  }
  return count.toString();
};
</script>

<style scoped>
.image-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.image-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 비율 */
  overflow: hidden;
  background-color: #f8f9fa;
}

.image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-card:hover .image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-card:hover .image-overlay {
  opacity: 1;
}

.view-button {
  padding: 10px 20px;
  background-color: white;
  color: #2d3436;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.view-button:hover {
  background-color: #ff6b6b;
  color: white;
}

.card-footer {
  padding: 12px;
}

.stats {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.stat-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.stat-button:hover {
  background-color: #f8f9fa;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
}

.like-button.liked {
  background-color: #ffe0e0;
}

.save-button.saved {
  background-color: #fff3cd;
}

.icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.count {
  font-size: 13px;
  font-weight: 600;
  color: #2d3436;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag-chip {
  padding: 4px 10px;
  background-color: #f8f9fa;
  border: none;
  border-radius: 12px;
  font-size: 12px;
  color: #2d3436;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-chip:hover {
  background-color: #ff6b6b;
  color: white;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .card-footer {
    padding: 10px;
  }

  .stats {
    gap: 8px;
  }

  .stat-button,
  .stat-item {
    padding: 4px 8px;
  }

  .icon {
    width: 14px;
    height: 14px;
  }

  .count {
    font-size: 12px;
  }

  .tag-chip {
    font-size: 11px;
    padding: 3px 8px;
  }
}
</style>
