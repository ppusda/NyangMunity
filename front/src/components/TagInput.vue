<template>
  <div class="tag-input-container">
    <div class="tag-input-header">
      <label class="tag-input-label">태그 추가</label>
      <span class="tag-count">{{ tags.length }}/{{ maxTags }}</span>
    </div>

    <!-- 태그 입력 필드 -->
    <div class="tag-input-wrapper">
      <input
        v-model="inputValue"
        type="text"
        class="tag-input-field"
        placeholder="#태그입력... (최대 5개)"
        :disabled="tags.length >= maxTags"
        @input="handleInput"
        @keydown.enter.prevent="addTag"
        @keydown.down.prevent="navigateDown"
        @keydown.up.prevent="navigateUp"
        @blur="hideAutocomplete"
      />
      <button
        class="tag-add-button"
        :disabled="!inputValue.trim() || tags.length >= maxTags"
        @click="addTag"
      >
        추가
      </button>
    </div>

    <!-- 자동완성 드롭다운 -->
    <div v-if="showAutocomplete && suggestions.length > 0" class="autocomplete-dropdown">
      <div
        v-for="(suggestion, index) in suggestions"
        :key="suggestion.id"
        class="autocomplete-item"
        :class="{ active: selectedIndex === index }"
        @mousedown.prevent="selectSuggestion(suggestion)"
        @mouseenter="selectedIndex = index"
      >
        <span class="suggestion-name">#{{ suggestion.name }}</span>
        <span class="suggestion-count">({{ suggestion.usageCount }})</span>
      </div>
    </div>

    <!-- 추가된 태그 목록 -->
    <div v-if="tags.length > 0" class="tags-list">
      <div v-for="tag in tags" :key="tag" class="tag-chip">
        <span class="tag-name">#{{ tag }}</span>
        <button class="tag-remove" @click="removeTag(tag)">×</button>
      </div>
    </div>

    <!-- 안내 메시지 -->
    <p v-if="tags.length >= maxTags" class="tag-limit-message">
      태그는 최대 {{ maxTags }}개까지만 추가할 수 있습니다.
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import axios from 'axios';
import type { Tag } from '@/interfaces/Tag';

interface Props {
  modelValue: string[];
  maxTags?: number;
}

interface Emits {
  (e: 'update:modelValue', tags: string[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  maxTags: 5,
});

const emit = defineEmits<Emits>();

const tags = ref<string[]>([...props.modelValue]);
const inputValue = ref('');
const suggestions = ref<Tag[]>([]);
const showAutocomplete = ref(false);
const selectedIndex = ref(-1);
let debounceTimer: ReturnType<typeof setTimeout> | null = null;

// 태그 목록 변경 시 부모 컴포넌트에 emit
watch(
  tags,
  (newTags) => {
    emit('update:modelValue', newTags);
  },
  { deep: true }
);

// 입력 값 변경 시 자동완성 조회
const handleInput = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }

  const keyword = normalizeInput(inputValue.value);

  if (!keyword) {
    suggestions.value = [];
    showAutocomplete.value = false;
    return;
  }

  debounceTimer = setTimeout(async () => {
    try {
      const response = await axios.get<Tag[]>('/api/tags/autocomplete', {
        params: { keyword },
      });
      suggestions.value = response.data;
      showAutocomplete.value = true;
      selectedIndex.value = -1;
    } catch (error) {
      console.error('자동완성 조회 실패:', error);
    }
  }, 300);
};

// 태그 추가
const addTag = () => {
  const normalizedTag = normalizeInput(inputValue.value);

  if (!normalizedTag) {
    return;
  }

  if (tags.value.length >= props.maxTags) {
    alert(`태그는 최대 ${props.maxTags}개까지만 추가할 수 있습니다.`);
    return;
  }

  if (tags.value.includes(normalizedTag)) {
    alert('이미 추가된 태그입니다.');
    inputValue.value = '';
    return;
  }

  tags.value.push(normalizedTag);
  inputValue.value = '';
  suggestions.value = [];
  showAutocomplete.value = false;
};

// 자동완성 항목 선택
const selectSuggestion = (suggestion: Tag) => {
  inputValue.value = suggestion.name;
  addTag();
};

// 태그 제거
const removeTag = (tag: string) => {
  const index = tags.value.indexOf(tag);
  if (index > -1) {
    tags.value.splice(index, 1);
  }
};

// 입력 값 정규화 (소문자, # 제거, 공백 제거)
const normalizeInput = (value: string): string => {
  return value
    .trim()
    .toLowerCase()
    .replace(/^#/, '');
};

// 자동완성 숨기기
const hideAutocomplete = () => {
  setTimeout(() => {
    showAutocomplete.value = false;
  }, 200);
};

// 키보드 네비게이션 - 아래로
const navigateDown = () => {
  if (!showAutocomplete.value || suggestions.value.length === 0) {
    return;
  }
  selectedIndex.value = Math.min(selectedIndex.value + 1, suggestions.value.length - 1);
};

// 키보드 네비게이션 - 위로
const navigateUp = () => {
  if (!showAutocomplete.value || suggestions.value.length === 0) {
    return;
  }
  selectedIndex.value = Math.max(selectedIndex.value - 1, -1);
};

// Enter 키 처리 개선
watch(selectedIndex, (newIndex) => {
  if (newIndex >= 0 && suggestions.value[newIndex]) {
    // 선택된 항목이 있으면 자동으로 입력값 업데이트는 하지 않음
    // 사용자가 Enter를 눌러야 선택됨
  }
});
</script>

<style scoped>
.tag-input-container {
  width: 100%;
  position: relative;
}

.tag-input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.tag-input-label {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.tag-count {
  font-size: 12px;
  color: #9ca3af;
}

.tag-input-wrapper {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.tag-input-field {
  flex: 1;
  padding: 10px 12px;
  background-color: #27272a;
  border: 2px solid #3f3f46;
  border-radius: 8px;
  font-size: 14px;
  color: #ffffff;
  transition: all 0.2s;
}

.tag-input-field::placeholder {
  color: #71717a;
}

.tag-input-field:focus {
  outline: none;
  border-color: #3b82f6;
}

.tag-input-field:disabled {
  background-color: #18181b;
  cursor: not-allowed;
  opacity: 0.5;
}

.tag-add-button {
  padding: 10px 20px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-add-button:hover:not(:disabled) {
  background-color: #2563eb;
}

.tag-add-button:disabled {
  background-color: #3f3f46;
  cursor: not-allowed;
  opacity: 0.5;
}

.autocomplete-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 60px;
  background: #27272a;
  border: 2px solid #3f3f46;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  margin-top: 4px;
}

.autocomplete-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.autocomplete-item:hover,
.autocomplete-item.active {
  background-color: #3f3f46;
}

.suggestion-name {
  font-size: 14px;
  color: #ffffff;
  font-weight: 500;
}

.suggestion-count {
  font-size: 12px;
  color: #9ca3af;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background-color: #3f3f46;
  border-radius: 16px;
  font-size: 14px;
  color: #ffffff;
  transition: all 0.2s;
}

.tag-chip:hover {
  background-color: #52525b;
}

.tag-name {
  font-weight: 500;
}

.tag-remove {
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 18px;
  cursor: pointer;
  padding: 0;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.tag-remove:hover {
  background-color: #ef4444;
  color: white;
}

.tag-limit-message {
  margin-top: 8px;
  font-size: 12px;
  color: #ef4444;
  font-weight: 500;
}
</style>
