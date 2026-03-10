<template>
  <div class="tag-filter-container">
    <div class="filter-header">
      <h3 class="filter-title">인기 태그</h3>
      <div class="header-actions">
        <button
            v-if="popularTags.length > displayLimit"
            class="toggle-button"
            @click="toggleExpanded"
        >
          {{ isExpanded ? '접기 ▲' : `더보기 (${popularTags.length - displayLimit}개) ▼` }}
        </button>
        <button
            v-if="selectedTags.length > 0"
            class="clear-button"
            @click="clearAll"
        >
          전체 해제
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <p>태그를 불러오는 중...</p>
    </div>

    <div v-else-if="popularTags.length === 0" class="empty-state">
      <p>아직 태그가 없습니다.</p>
    </div>

    <div v-else>
      <!-- 태그 그리드 - 높이 제한 & 스크롤 -->
      <div class="tags-grid-wrapper" :class="{ expanded: isExpanded }">
        <div class="tags-grid">
          <!-- 전체 버튼 -->
          <button
              class="tag-button"
              :class="{ active: selectedTags.length === 0 }"
              @click="clearAll"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="tag-icon">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12l8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25" />
            </svg>
            <span class="tag-text">전체</span>
          </button>

          <!-- 인기 태그 버튼들 -->
          <button
              v-for="tag in displayedTags"
              :key="tag.id"
              class="tag-button"
              :class="{ active: isSelected(tag.name) }"
              @click="toggleTag(tag.name)"
          >
            <span class="tag-text">#{{ tag.name }}</span>
            <span class="tag-badge">{{ tag.usageCount }}</span>
          </button>
        </div>
      </div>

      <!-- 선택된 태그 표시 -->
      <div v-if="selectedTags.length > 0" class="selected-tags">
        <span class="selected-label">선택된 태그:</span>
        <div class="selected-chips">
          <span
              v-for="tag in selectedTags"
              :key="tag"
              class="selected-chip"
              @click="toggleTag(tag)"
          >
            #{{ tag }}
            <span class="chip-remove">×</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, watch, computed} from 'vue';
import axiosClient from '@/libs/axiosClient';
import type {Tag} from '@/interfaces/Tag';

interface Props {
  modelValue: string[];
  limit?: number;
  displayLimit?: number; // 기본 표시 개수
}

interface Emits {
  (e: 'update:modelValue', tags: string[]): void;

  (e: 'change', tags: string[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  limit: 50,
  displayLimit: 15, // 기본 15개만 표시
});

const emit = defineEmits<Emits>();

const popularTags = ref<Tag[]>([]);
const selectedTags = ref<string[]>([...props.modelValue]);
const loading = ref(false);
const isExpanded = ref(false); // 확장 상태

// 표시할 태그 목록 (접기/펼치기)
const displayedTags = computed(() => {
  if (isExpanded.value) {
    return popularTags.value;
  }
  return popularTags.value.slice(0, props.displayLimit);
});

// 컴포넌트 마운트 시 인기 태그 로드
onMounted(async () => {
  await loadPopularTags();
});

// 인기 태그 조회
const loadPopularTags = async () => {
  loading.value = true;
  try {
    const response = await axiosClient.get<Tag[]>('/api/tags/popular', {
      params: {limit: props.limit},
    });
    popularTags.value = response.data;
  } catch (error) {
    console.error('인기 태그 조회 실패:', error);
  } finally {
    loading.value = false;
  }
};

// 확장/접기 토글
const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value;
};

// 태그 선택/해제 토글
const toggleTag = (tagName: string) => {
  const index = selectedTags.value.indexOf(tagName);

  if (index > -1) {
    // 이미 선택된 태그면 제거
    selectedTags.value.splice(index, 1);
  } else {
    // 선택되지 않은 태그면 추가
    selectedTags.value.push(tagName);
  }

  emitChanges();
};

// 전체 선택 해제
const clearAll = () => {
  selectedTags.value = [];
  emitChanges();
};

// 태그가 선택되었는지 확인
const isSelected = (tagName: string): boolean => {
  return selectedTags.value.includes(tagName);
};

// 변경사항 emit
const emitChanges = () => {
  emit('update:modelValue', selectedTags.value);
  emit('change', selectedTags.value);
};

// 외부에서 modelValue가 변경되면 동기화
watch(
    () => props.modelValue,
    (newValue) => {
      selectedTags.value = [...newValue];
    }
);
</script>

<style scoped>
.tag-filter-container {
  width: 100%;
  padding: 20px;
  background-color: #18181b;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.toggle-button,
.clear-button {
  padding: 6px 12px;
  background-color: transparent;
  border: 1px solid #3f3f46;
  border-radius: 6px;
  font-size: 13px;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-button:hover,
.clear-button:hover {
  background-color: #27272a;
  border-color: #52525b;
  color: #ffffff;
}

.toggle-button {
  color: #60a5fa;
  border-color: #60a5fa;
}

.toggle-button:hover {
  background-color: #1e3a8a;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  font-size: 14px;
}

.tags-grid-wrapper {
  max-height: 280px; /* 약 3줄 */
  overflow: hidden;
  transition: max-height 0.3s ease;
  position: relative;
}

.tags-grid-wrapper.expanded {
  max-height: 600px; /* 확장 시 더 많이 */
  overflow-y: auto;
}

/* 스크롤바 스타일 */
.tags-grid-wrapper.expanded::-webkit-scrollbar {
  width: 8px;
}

.tags-grid-wrapper.expanded::-webkit-scrollbar-track {
  background: #18181b;
  border-radius: 4px;
}

.tags-grid-wrapper.expanded::-webkit-scrollbar-thumb {
  background: #3f3f46;
  border-radius: 4px;
}

.tags-grid-wrapper.expanded::-webkit-scrollbar-thumb:hover {
  background: #52525b;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
}

.tag-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 12px 16px;
  background-color: #27272a;
  border: 2px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 70px;
}

.tag-button:hover {
  background-color: #3f3f46;
  transform: translateY(-2px);
}

.tag-button.active {
  background-color: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.tag-button.active .tag-text {
  color: white;
}

.tag-button.active .tag-badge {
  background-color: rgba(255, 255, 255, 0.3);
  color: white;
}

.tag-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.tag-text {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  transition: color 0.2s;
  word-break: break-word;
  text-align: center;
}

.tag-badge {
  font-size: 11px;
  color: #9ca3af;
  background-color: #3f3f46;
  padding: 2px 8px;
  border-radius: 10px;
  transition: all 0.2s;
}

.selected-tags {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #3f3f46;
}

.selected-label {
  font-size: 13px;
  font-weight: 600;
  color: #9ca3af;
  margin-right: 8px;
}

.selected-chips {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.selected-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background-color: #3b82f6;
  color: white;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.selected-chip:hover {
  background-color: #2563eb;
}

.chip-remove {
  font-size: 16px;
  font-weight: bold;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.selected-chip:hover .chip-remove {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .tag-filter-container {
    padding: 16px;
  }

  .filter-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .tags-grid-wrapper {
    max-height: 240px;
  }

  .tags-grid-wrapper.expanded {
    max-height: 500px;
  }

  .tags-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 8px;
  }

  .tag-button {
    padding: 10px 12px;
    min-height: 60px;
  }

  .tag-text {
    font-size: 13px;
  }
}
</style>