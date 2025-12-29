<template>
  <div class="tag-filter-container">
    <div class="filter-header">
      <h3 class="filter-title">인기 태그</h3>
      <button
        v-if="selectedTags.length > 0"
        class="clear-button"
        @click="clearAll"
      >
        전체 해제
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      <p>태그를 불러오는 중...</p>
    </div>

    <div v-else-if="popularTags.length === 0" class="empty-state">
      <p>아직 태그가 없습니다.</p>
    </div>

    <div v-else class="tags-grid">
      <!-- 전체 버튼 -->
      <button
        class="tag-button"
        :class="{ active: selectedTags.length === 0 }"
        @click="clearAll"
      >
        <span class="tag-icon">🏠</span>
        <span class="tag-text">전체</span>
      </button>

      <!-- 인기 태그 버튼들 -->
      <button
        v-for="tag in popularTags"
        :key="tag.id"
        class="tag-button"
        :class="{ active: isSelected(tag.name) }"
        @click="toggleTag(tag.name)"
      >
        <span class="tag-text">#{{ tag.name }}</span>
        <span class="tag-badge">{{ tag.usageCount }}</span>
      </button>
    </div>

    <!-- 선택된 태그 표시 -->
    <div v-if="selectedTags.length > 0" class="selected-tags">
      <span class="selected-label">선택된 태그:</span>
      <div class="selected-chips">
        <span
          v-for="tag in selectedTags"
          :key="tag"
          class="selected-chip"
        >
          #{{ tag }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import type { Tag } from '@/interfaces/Tag';

interface Props {
  modelValue: string[];
  limit?: number;
}

interface Emits {
  (e: 'update:modelValue', tags: string[]): void;
  (e: 'change', tags: string[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  limit: 20,
});

const emit = defineEmits<Emits>();

const popularTags = ref<Tag[]>([]);
const selectedTags = ref<string[]>([...props.modelValue]);
const loading = ref(false);

// 컴포넌트 마운트 시 인기 태그 로드
onMounted(async () => {
  await loadPopularTags();
});

// 인기 태그 조회
const loadPopularTags = async () => {
  loading.value = true;
  try {
    const response = await axios.get<Tag[]>('/api/tags/popular', {
      params: { limit: props.limit },
    });
    popularTags.value = response.data;
  } catch (error) {
    console.error('인기 태그 조회 실패:', error);
  } finally {
    loading.value = false;
  }
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

.clear-button:hover {
  background-color: #27272a;
  border-color: #52525b;
  color: #ffffff;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  font-size: 14px;
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
  font-size: 20px;
}

.tag-text {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  transition: color 0.2s;
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
  display: inline-block;
  padding: 4px 10px;
  background-color: #3b82f6;
  color: white;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .tag-filter-container {
    padding: 16px;
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
