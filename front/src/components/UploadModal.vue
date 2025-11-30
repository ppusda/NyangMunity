<script setup lang="ts">
import {ref, onMounted} from 'vue';
import {infoToast, warningToast} from '@/libs/toaster';
import axiosClient from '@/libs/axiosClient';
import s3Client from '@/libs/s3Client';
import type {Image} from '@/interfaces/type';

const emit = defineEmits(['close', 'uploaded']);

// 탭 관리
const activeTab = ref<'upload' | 'gallery'>('upload');

// 업로드 이미지 (1개만)
const selectedImage = ref<Image | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

// 갤러리 이미지
const galleryImages = ref<Image[]>([]);
const galleryPage = ref(0);
const galleryProviders = ref<string[]>([]);
const selectedProvider = ref('Nyangmunity');

// 게시물 내용
const content = ref('');

// 업로드 상태
const isUploading = ref(false);

// Provider 가져오기
const fetchProviders = async () => {
  try {
    const response = await axiosClient.get('/images/providers');
    galleryProviders.value = response.data.Provider || [];
  } catch (error) {
    console.error('Provider 로드 실패:', error);
  }
};

// 갤러리 이미지 가져오기
const fetchGalleryImages = async (page: number, provider: string) => {
  try {
    const response = await axiosClient.get(`/images?page=${page}&provider=${provider}&size=20`);
    if (page === 0) {
      galleryImages.value = response.data.content || [];
    } else {
      galleryImages.value.push(...(response.data.content || []));
    }
  } catch (error) {
    warningToast('이미지를 불러오는데 실패했습니다.');
  }
};

// Provider 변경
const changeProvider = (provider: string) => {
  selectedProvider.value = provider;
  galleryPage.value = 0;
  fetchGalleryImages(0, provider);
};

// 갤러리 이미지 선택
const selectGalleryImage = (image: Image) => {
  selectedImage.value = {
    id: image.id,
    url: image.url,
    filename: null,
    source: 'gallery'
  };
  infoToast('이미지가 선택되었습니다!');
};

// 파일 선택 버튼 클릭
const handleFileButtonClick = () => {
  fileInput.value?.click();
};

// 파일 선택 (1개만)
const handleFileSelect = (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (files && files[0]) {
    processFile(files[0]);
  }
};

// 드래그 앤 드롭
const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const files = event.dataTransfer?.files;
  if (files && files[0]) {
    processFile(files[0]);
  }
};

const handleDragOver = (event: DragEvent) => {
  event.preventDefault();
};

// 파일 처리
const processFile = (file: File) => {
  const reader = new FileReader();
  reader.onload = (e) => {
    const url = e.target?.result as string;
    selectedImage.value = {
      id: null,
      url,
      filename: file.name,
      source: 'upload'
    };
  };
  reader.readAsDataURL(file);
};

// 이미지 제거
const removeImage = () => {
  selectedImage.value = null;
};

// Data URL을 Blob으로 변환
const dataUrlToBlob = (dataUrl: string): Blob => {
  const arr = dataUrl.split(',');
  const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/jpeg';
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], {type: mime});
};

// 업로드 처리
const handleUpload = async () => {
  if (!selectedImage.value) {
    warningToast('이미지를 선택해주세요.');
    return;
  }

  isUploading.value = true;

  try {
    let imageId: string;

    if (selectedImage.value.source === 'gallery') {
      // 갤러리 이미지는 ID만 사용
      imageId = selectedImage.value.id as string;
    } else {
      // 직접 업로드한 이미지는 S3에 업로드
      const response = await axiosClient.get(`/images/upload?filename=${selectedImage.value.filename}`);
      const {id, uploadUrl} = response.data;

      const blob = dataUrlToBlob(selectedImage.value.url);
      await s3Client.put(uploadUrl, blob, {
        headers: {
          'Content-Type': blob.type || 'image/jpeg'
        }
      });

      imageId = id;
    }

    // 게시물 생성
    await axiosClient.post('/posts', {
      content: content.value || null,
      postImageIds: [imageId]
    });

    infoToast('게시물이 업로드되었습니다!');
    emit('uploaded');
    emit('close');
  } catch (error) {
    warningToast('업로드에 실패했습니다.');
  } finally {
    isUploading.value = false;
  }
};

onMounted(() => {
  fetchProviders();
  fetchGalleryImages(0, selectedProvider.value);
});
</script>

<template>
  <Teleport to="body">
    <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm"
        @click.self="$emit('close')"
    >
      <!-- 모달 -->
      <div class="w-full max-w-3xl bg-zinc-900 rounded-2xl overflow-hidden shadow-2xl animate-fadeIn">
        <!-- 헤더 -->
        <div class="p-6 border-b border-zinc-800 flex items-center justify-between">
          <h2 class="text-xl font-bold text-white">새 게시물</h2>
          <button
              @click="$emit('close')"
              class="text-gray-400 hover:text-white transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                 stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <!-- 탭 -->
        <div class="flex border-b border-zinc-800">
          <button
              @click="activeTab = 'upload'"
              :class="['flex-1 py-3 text-sm font-medium transition-all',
              activeTab === 'upload'
                ? 'text-white border-b-2 border-blue-500'
                : 'text-gray-400 hover:text-white']"
          >
            📤 직접 업로드
          </button>
          <button
              @click="activeTab = 'gallery'"
              :class="['flex-1 py-3 text-sm font-medium transition-all',
              activeTab === 'gallery'
                ? 'text-white border-b-2 border-blue-500'
                : 'text-gray-400 hover:text-white']"
          >
            🐱 갤러리에서 선택
          </button>
        </div>

        <!-- 본문 -->
        <div class="p-6">
          <!-- 업로드 탭 -->
          <div v-if="activeTab === 'upload'">
            <!-- 선택된 이미지 없을 때 -->
            <div v-if="!selectedImage">
              <div
                  class="border-2 border-dashed border-zinc-700 rounded-xl p-12 text-center hover:border-blue-500 transition-all cursor-pointer"
                  @drop="handleDrop"
                  @dragover="handleDragOver"
                  @click="handleFileButtonClick"
              >
                <input
                    ref="fileInput"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="handleFileSelect"
                />
                <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-600 mb-4" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                <p class="text-white text-lg font-medium mb-2">이미지를 업로드하세요</p>
                <p class="text-gray-500 text-sm">클릭하거나 드래그하여 선택</p>
              </div>
            </div>

            <!-- 선택된 이미지 있을 때 -->
            <div v-else>
              <div class="relative rounded-xl overflow-hidden aspect-video bg-zinc-800 mb-4">
                <img :src="selectedImage.url" class="w-full h-full object-contain"/>
                <button
                    @click="removeImage"
                    class="absolute top-4 right-4 w-10 h-10 bg-red-500 hover:bg-red-600 text-white rounded-full flex items-center justify-center transition-colors"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                       stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>
              <button
                  @click="removeImage"
                  class="w-full py-2 text-gray-400 hover:text-white text-sm transition-colors"
              >
                다른 이미지 선택
              </button>
            </div>
          </div>

          <!-- 갤러리 탭 -->
          <div v-if="activeTab === 'gallery'">
            <!-- Provider 선택 -->
            <div class="flex flex-wrap gap-2 mb-4">
              <button
                  v-for="provider in galleryProviders"
                  :key="provider"
                  @click="changeProvider(provider)"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  selectedProvider === provider
                    ? 'bg-blue-600 text-white'
                    : 'bg-zinc-800 text-gray-400 hover:bg-zinc-700 hover:text-white']"
              >
                {{ provider }}
              </button>
            </div>

            <!-- 갤러리 그리드 -->
            <div class="border border-zinc-800 rounded-xl p-4 max-h-96 overflow-y-auto">
              <div class="grid grid-cols-3 gap-3">
                <div
                    v-for="image in galleryImages"
                    :key="image.id"
                    @click="selectGalleryImage(image)"
                    :class="['relative aspect-square rounded-lg overflow-hidden cursor-pointer group',
                    selectedImage?.id === image.id ? 'ring-4 ring-blue-500' : '']"
                >
                  <img :src="image.url" class="w-full h-full object-cover"/>
                  <div
                      class="absolute inset-0 bg-blue-600/30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <svg v-if="selectedImage?.id === image.id" xmlns="http://www.w3.org/2000/svg"
                         class="h-12 w-12 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 설명 입력 -->
          <div class="mt-6">
            <label class="block text-white font-medium mb-2">설명 (선택, 최대 100자)</label>
            <textarea
                v-model="content"
                placeholder="간단한 설명을 입력해주세요."
                maxlength="100"
                class="w-full px-4 py-3 bg-zinc-800 text-white rounded-xl border border-zinc-700 focus:border-blue-500 focus:outline-none resize-none"
                rows="3"
            ></textarea>
            <p class="text-gray-500 text-sm mt-1 text-right">{{ content.length }}/100</p>
          </div>
        </div>

        <!-- 푸터 -->
        <div class="p-6 border-t border-zinc-800 flex gap-3">
          <button
              @click="$emit('close')"
              class="flex-1 py-3 bg-zinc-800 text-white rounded-xl hover:bg-zinc-700 transition-colors font-medium"
          >
            취소
          </button>
          <button
              @click="handleUpload"
              :disabled="!selectedImage || isUploading"
              class="flex-1 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl hover:from-blue-700 hover:to-purple-700 transition-all font-medium disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ isUploading ? '업로드 중...' : '게시하기' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out;
}
</style>