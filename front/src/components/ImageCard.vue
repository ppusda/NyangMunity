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
          <span class="icon">{{ isLiked ? '❤️' : '🤍' }}</span>
          <span class="count">{{ formatCount(image.likesCount) }}</span>
        </button>

        <div class="stat-item">
          <span class="icon">👁️</span>
          <span class="count">{{ formatCount(image.viewsCount) }}</span>
        </div>

        <button
          class="stat-button save-button"
          :class="{ saved: isSaved }"
          @click.stop="handleSave"
          title="컬렉션에 저장"
        >
          <span class="icon">{{ isSaved ? '⭐' : '☆' }}</span>
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
  font-size: 16px;
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
    font-size: 14px;
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
