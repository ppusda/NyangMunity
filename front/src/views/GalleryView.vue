<template>
  <div class="gallery-view">
    <div class="container">
      <!-- 헤더 -->
      <div class="gallery-header">
        <h1 class="gallery-title">냥뮤니티 갤러리</h1>
        <div class="header-actions">
          <select v-model="sortBy" class="sort-select" @change="loadImages">
            <option value="latest">최신순</option>
            <option value="popular">인기순</option>
            <option value="views">조회순</option>
          </select>
          <button class="upload-button" @click="showUploadModal = true">
            이미지 업로드
          </button>
        </div>
      </div>

      <!-- 태그 필터 -->
      <TagFilter v-model="selectedTags" @change="handleTagFilter" />

      <!-- 이미지 그리드 -->
      <div v-if="loading" class="loading-state">
        <p>이미지를 불러오는 중...</p>
      </div>

      <div v-else-if="images.length === 0" class="empty-state">
        <p>아직 이미지가 없습니다.</p>
        <button class="upload-button" @click="showUploadModal = true">
          첫 번째 이미지 업로드하기
        </button>
      </div>

      <div v-else class="image-grid">
        <ImageCard
          v-for="image in images"
          :key="image.id"
          :image="image"
          :liked="isLiked(image.id)"
          :saved="isSaved(image.id)"
          @like="handleLike"
          @save="handleSave"
          @tag-click="handleTagClick"
          @image-click="handleImageClick"
        />
      </div>

      <!-- 페이지네이션 -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="page-button"
          :disabled="currentPage === 0"
          @click="goToPage(currentPage - 1)"
        >
          이전
        </button>

        <div class="page-numbers">
          <button
            v-for="page in visiblePages"
            :key="page"
            class="page-number"
            :class="{ active: page === currentPage }"
            @click="goToPage(page)"
          >
            {{ page + 1 }}
          </button>
        </div>

        <button
          class="page-button"
          :disabled="currentPage === totalPages - 1"
          @click="goToPage(currentPage + 1)"
        >
          다음
        </button>
      </div>
    </div>

    <!-- 업로드 모달 (예시) -->
    <div v-if="showUploadModal" class="modal-overlay" @click="showUploadModal = false">
      <div class="modal-content" @click.stop>
        <h2>이미지 업로드</h2>
        <p>여기에 UploadModal 컴포넌트가 들어갑니다.</p>
        <button @click="showUploadModal = false">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import TagFilter from '@/components/TagFilter.vue';
import ImageCard from '@/components/ImageCard.vue';
import type { ImageTag } from '@/interfaces/Tag';

interface ImageData {
  id: string;
  name?: string;
  url: string;
  likesCount: number;
  viewsCount: number;
  tags?: ImageTag[];
}

interface PageResponse {
  content: ImageData[];
  totalPages: number;
  totalElements: number;
  number: number;
}

const images = ref<ImageData[]>([]);
const selectedTags = ref<string[]>([]);
const sortBy = ref('latest');
const loading = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 20;
const showUploadModal = ref(false);

// 좋아요한 이미지 ID 목록 (실제로는 Vuex나 Pinia로 관리)
const likedImages = ref<Set<string>>(new Set());
const savedImages = ref<Set<string>>(new Set());

onMounted(() => {
  loadImages();
});

// 이미지 목록 로드
const loadImages = async () => {
  loading.value = true;
  try {
    const params: Record<string, any> = {
      page: currentPage.value,
      size: pageSize,
      sort: sortBy.value,
    };

    if (selectedTags.value.length > 0) {
      params.tags = selectedTags.value.join(',');
    }

    const response = await axios.get<PageResponse>('/api/images', { params });

    images.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('이미지 로드 실패:', error);
  } finally {
    loading.value = false;
  }
};

// 태그 필터 변경
const handleTagFilter = (tags: string[]) => {
  currentPage.value = 0; // 페이지 초기화
  loadImages();
};

// 태그 클릭 (필터에 추가)
const handleTagClick = (tagName: string) => {
  if (!selectedTags.value.includes(tagName)) {
    selectedTags.value.push(tagName);
    handleTagFilter(selectedTags.value);
  }
};

// 좋아요
const handleLike = async (imageId: string) => {
  try {
    if (likedImages.value.has(imageId)) {
      await axios.delete(`/api/images/${imageId}/like`);
      likedImages.value.delete(imageId);
    } else {
      await axios.post(`/api/images/${imageId}/like`);
      likedImages.value.add(imageId);
    }

    // 이미지 카운트 업데이트
    const image = images.value.find((img) => img.id === imageId);
    if (image) {
      image.likesCount += likedImages.value.has(imageId) ? 1 : -1;
    }
  } catch (error) {
    console.error('좋아요 처리 실패:', error);
  }
};

// 저장 (컬렉션)
const handleSave = async (imageId: string) => {
  try {
    if (savedImages.value.has(imageId)) {
      await axios.delete(`/api/collections/${imageId}`);
      savedImages.value.delete(imageId);
    } else {
      await axios.post(`/api/collections/${imageId}`);
      savedImages.value.add(imageId);
    }
  } catch (error) {
    console.error('저장 처리 실패:', error);
  }
};

// 이미지 클릭
const handleImageClick = (imageId: string) => {
  // 이미지 상세 모달 열기 등
  console.log('Image clicked:', imageId);
};

// 페이지 이동
const goToPage = (page: number) => {
  currentPage.value = page;
  loadImages();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 보이는 페이지 번호들
const visiblePages = computed(() => {
  const pages: number[] = [];
  const maxVisible = 5;
  let start = Math.max(0, currentPage.value - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages.value, start + maxVisible);

  if (end - start < maxVisible) {
    start = Math.max(0, end - maxVisible);
  }

  for (let i = start; i < end; i++) {
    pages.push(i);
  }

  return pages;
});

// 좋아요 여부 확인
const isLiked = (imageId: string): boolean => {
  return likedImages.value.has(imageId);
};

// 저장 여부 확인
const isSaved = (imageId: string): boolean => {
  return savedImages.value.has(imageId);
};
</script>

<style scoped>
.gallery-view {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.gallery-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.gallery-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3436;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.sort-select {
  padding: 10px 16px;
  border: 2px solid #dfe6e9;
  border-radius: 8px;
  font-size: 14px;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-select:focus {
  outline: none;
  border-color: #ff6b6b;
}

.upload-button {
  padding: 10px 20px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-button:hover {
  background-color: #ff5252;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #636e72;
}

.empty-state .upload-button {
  margin-top: 20px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-top: 30px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 40px;
  margin-bottom: 40px;
}

.page-button {
  padding: 8px 16px;
  background-color: white;
  border: 2px solid #dfe6e9;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.page-button:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #ff6b6b;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  width: 40px;
  height: 40px;
  background-color: white;
  border: 2px solid #dfe6e9;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.page-number:hover {
  background-color: #f8f9fa;
  border-color: #ff6b6b;
}

.page-number.active {
  background-color: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 40px;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .gallery-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .gallery-title {
    font-size: 24px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .image-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }

  .page-numbers {
    gap: 4px;
  }

  .page-number {
    width: 36px;
    height: 36px;
    font-size: 13px;
  }
}
</style>
