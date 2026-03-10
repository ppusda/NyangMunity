<script setup lang="ts">
import {ref, onMounted, computed, onBeforeUnmount} from 'vue';
import axiosClient from '@/libs/axiosClient';
import type {Post} from '@/interfaces/type';
import {infoToast, warningToast} from '@/libs/toaster';
import {useClipboard} from '@vueuse/core';
import store from '@/stores/store';
import router from '@/router';
import ImageModal from '@/components/ImageModal.vue';
import UploadModal from '@/components/UploadModal.vue';
import TagFilter from '@/components/TagFilter.vue';

// 로그인 상태
const isLogin = computed(() => store.state.isLogin);

// 카테고리 타입 정의 (더 유연하게)
interface CategoryChild {
  id: string;
  name: string;
  type: 'post' | 'image';
  provider?: string;  // 모든 provider를 받을 수 있도록 string으로 변경
}

interface Category {
  id: string;
  name: string;
  type: 'post' | 'image' | 'group';
  provider?: string;
  expanded?: boolean;
  children?: CategoryChild[];
}

// 카테고리 (동적으로 생성될 예정)
const categories = ref<Category[]>([
  {id: 'all', name: '전체', type: 'post'}
]);

const selectedCategory = ref('all');

// 상태 관리
const posts = ref<Post[]>([]);
const page = ref(0);
const hasMore = ref(true);
const isLoading = ref(false);
const isCategoriesLoading = ref(true);
const sortBy = ref('latest');

// 모달 상태
const showImageModal = ref(false);
const showUploadModal = ref(false);
const selectedPost = ref<Post | null>(null);

// 좋아요 상태
const likedImages = ref<Record<string, boolean>>({});

// 태그 필터
const selectedTags = ref<string[]>([]);

// URL 복사
const {copy} = useClipboard();

// Provider 목록 가져오기 (백엔드에서)
const fetchProviders = async (): Promise<string[]> => {
  try {
    const response = await axiosClient.get('/images/providers');
    return response.data.Provider || [];
  } catch (error) {
    console.error('Provider 로드 실패:', error);
    return [];
  }
};

// 카테고리 동적 생성
const initializeCategories = async () => {
  isCategoriesLoading.value = true;

  try {
    const providers = await fetchProviders();

    // Posts 하위 카테고리 생성
    const postsChildren: CategoryChild[] = [
      {id: 'posts-all', name: '전체', type: 'post'}
    ];

    providers.forEach(provider => {
      postsChildren.push({
        id: `posts-${provider.toLowerCase()}`,
        name: provider,
        type: 'post',
        provider: provider
      });
    });

    // Images 하위 카테고리 생성
    const imagesChildren: CategoryChild[] = [];

    providers.forEach(provider => {
      imagesChildren.push({
        id: `images-${provider.toLowerCase()}`,
        name: provider,
        type: 'image',
        provider: provider
      });
    });

    // 전체 카테고리 구성
    categories.value = [
      {id: 'all', name: '전체', type: 'post'},
      {
        id: 'posts',
        name: 'Posts',
        type: 'group',
        expanded: true,
        children: postsChildren
      },
      {
        id: 'images',
        name: 'Images',
        type: 'group',
        expanded: false,
        children: imagesChildren
      }
    ];
  } catch (error) {
    console.error('카테고리 초기화 실패:', error);
    // 실패 시 기본 카테고리 사용
    categories.value = [
      {id: 'all', name: '전체', type: 'post'},
      {
        id: 'posts',
        name: 'Posts',
        type: 'group',
        expanded: true,
        children: [
          {id: 'posts-all', name: '전체', type: 'post'}
        ]
      }
    ];
  } finally {
    isCategoriesLoading.value = false;
  }
};

// 카테고리 토글
const toggleCategory = (categoryId: string) => {
  const category = categories.value.find(c => c.id === categoryId);
  if (category && category.type === 'group') {
    category.expanded = !category.expanded;
  }
};

// 게시물 가져오기
const fetchPosts = async (pageNum: number) => {
  if (isLoading.value || !hasMore.value) return;

  isLoading.value = true;
  try {
    const selected = findSelectedCategory(selectedCategory.value);
    let url = '';

    if (selected?.type === 'image') {
      // Images 갤러리
      url = `/images?page=${pageNum}&size=20&gallery=true`;
      if (selected.provider) {
        url += `&provider=${selected.provider}`;
      }
      // 태그 필터 추가
      if (selectedTags.value.length > 0) {
        url += `&tags=${selectedTags.value.join(',')}`;
      }
    } else {
      // Posts
      url = `/posts?page=${pageNum}&size=20`;
      if (selected?.provider) {
        url += `&provider=${selected.provider}`;
      }
      // 태그 필터 추가
      if (selectedTags.value.length > 0) {
        url += `&tags=${selectedTags.value.join(',')}`;
      }
    }

    const response = await axiosClient.get(url);

    if (pageNum === 0) {
      posts.value = response.data.content;
    } else {
      posts.value.push(...response.data.content);
    }

    // 좋아요 상태 초기화
    response.data.content.forEach((post: Post) => {
      const image = getImage(post);
      if (image?.id) {
        likedImages.value[image.id] = image.likeState || false;
      }
    });

    hasMore.value = !response.data.last;
  } catch (error) {
    warningToast('게시물을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

// 선택된 카테고리 찾기
const findSelectedCategory = (id: string): Category | CategoryChild | null => {
  for (const cat of categories.value) {
    if (cat.id === id) return cat;
    if (cat.children) {
      const found = cat.children.find((child) => child.id === id);
      if (found) return found;
    }
  }
  return null;
};

// 카테고리 변경
const changeCategory = (categoryId: string) => {
  const selected = findSelectedCategory(categoryId);
  if (!selected || selected.type === 'group') return;

  selectedCategory.value = categoryId;
  page.value = 0;
  hasMore.value = true;
  posts.value = [];
  fetchPosts(0);
};

// 무한 스크롤
const handleScroll = () => {
  if (!mainRef.value) return;

  const element = mainRef.value;
  const scrollTop = element.scrollTop;
  const scrollHeight = element.scrollHeight;
  const clientHeight = element.clientHeight;

  if (scrollTop + clientHeight >= scrollHeight - 500 && !isLoading.value && hasMore.value) {
    page.value += 1;
    fetchPosts(page.value);
  }
};

// 이미지 클릭 - 상세보기 모달
const openImageModal = (post: Post) => {
  selectedPost.value = post;
  showImageModal.value = true;
};

// 업로드 버튼 클릭
const openUploadModal = () => {
  if (!isLogin.value) {
    warningToast('로그인이 필요합니다.');
    router.push({name: 'login'});
    return;
  }
  showUploadModal.value = true;
};

// 업로드 완료 후
const handleUploaded = () => {
  page.value = 0;
  hasMore.value = true;
  fetchPosts(0);
};

// 태그 필터 변경
const handleTagFilter = (tags: string[]) => {
  page.value = 0;
  hasMore.value = true;
  posts.value = [];
  fetchPosts(0);
};

// 이미지 가져오기
const getImage = (item: any) => {
  if (item.url) {
    return {
      id: item.id,
      url: item.url,
      likeState: item.likeState || false
    };
  }
  return item.postImages?.[0];
};

// 작성자 이름 가져오기
const getWriter = (item: any) => {
  if (item.url) return 'Gallery';
  return item.writer || 'Unknown';
};

// 좋아요 상태 확인
const isLiked = (post: Post) => {
  const imageId = getImage(post)?.id;
  return imageId ? likedImages.value[imageId] || false : false;
};

// 좋아요 토글
const toggleLike = async (post: Post) => {
  if (!isLogin.value) {
    warningToast('로그인이 필요합니다.');
    await router.push({name: 'login'});
    return;
  }

  const imageId = getImage(post)?.id;
  if (!imageId) return;

  try {
    const response = await axiosClient.post('/images/likes', {imageId});
    likedImages.value[imageId] = response.data.state;
    infoToast(response.data.state ? '좋아요!' : '좋아요 취소');
  } catch (error) {
    warningToast('좋아요 처리에 실패했습니다.');
  }
};

// URL 복사
const copyImageUrl = (post: Post) => {
  const url = getImage(post)?.url;
  if (url) {
    copy(url);
    infoToast('이미지 URL이 복사되었습니다!');
  }
};

// 정렬 변경
const changeSortBy = (newSort: string) => {
  sortBy.value = newSort;
  page.value = 0;
  hasMore.value = true;
  posts.value = [];
  fetchPosts(0);
};

// main 영역 ref
const mainRef = ref<HTMLElement | null>(null);

onMounted(async () => {
  // 카테고리 초기화 먼저
  await initializeCategories();

  // 게시물 로드
  await fetchPosts(0);

  // 스크롤 이벤트 등록
  if (mainRef.value) {
    mainRef.value.addEventListener('scroll', handleScroll);
  }
});

onBeforeUnmount(() => {
  if (mainRef.value) {
    mainRef.value.removeEventListener('scroll', handleScroll);
  }
});

</script>

<template>
  <div class="min-h-screen bg-zinc-900 flex h-screen overflow-hidden">
    <!-- 왼쪽 사이드바 (데스크톱) - 스크롤 가능 -->
    <aside class="hidden md:block w-64 bg-zinc-900 border-r border-zinc-800 p-6 overflow-y-auto">
      <div class="space-y-1">
        <h2 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-4 px-3">카테고리</h2>

        <!-- 카테고리 로딩 중 -->
        <div v-if="isCategoriesLoading" class="flex justify-center py-8">
          <div class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
        </div>

        <!-- 카테고리 목록 -->
        <template v-else v-for="category in categories" :key="category.id">
          <!-- 단일 카테고리 -->
          <button
              v-if="category.type !== 'group'"
              @click="changeCategory(category.id)"
              :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl text-left transition-all duration-200',
              selectedCategory === category.id
                ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/30'
                : 'text-gray-400 hover:bg-zinc-800 hover:text-white'
            ]"
          >
            <!-- 전체 버튼: Squares-2x2 -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 flex-shrink-0">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z" />
            </svg>
            <span class="font-medium">{{ category.name }}</span>
          </button>

          <!-- 그룹 카테고리 -->
          <div v-else class="space-y-1">
            <button
                @click="toggleCategory(category.id)"
                class="w-full flex items-center justify-between px-4 py-3 rounded-xl text-gray-400 hover:bg-zinc-800 hover:text-white transition-all duration-200"
            >
              <div class="flex items-center gap-3">
                <!-- Posts 그룹: Document-text -->
                <svg v-if="category.id === 'posts'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 flex-shrink-0">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
                </svg>
                <!-- Images 그룹: Photo -->
                <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 flex-shrink-0">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                </svg>
                <span class="font-medium">{{ category.name }}</span>
              </div>
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4 transition-transform duration-200"
                  :class="{ 'rotate-180': category.expanded }"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
              </svg>
            </button>

            <!-- 하위 카테고리 -->
            <div v-if="category.expanded" class="ml-4 space-y-1">
              <button
                  v-for="child in category.children"
                  :key="child.id"
                  @click="changeCategory(child.id)"
                  :class="[
                  'w-full flex items-center gap-3 px-4 py-2 rounded-lg text-left transition-all duration-200 text-sm',
                  selectedCategory === child.id
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-400 hover:bg-zinc-800 hover:text-white'
                ]"
              >
                {{ child.name }}
              </button>
            </div>
          </div>
        </template>
      </div>
    </aside>

    <!-- 메인 콘텐츠 - 스크롤 영역 -->
    <main ref="mainRef" class="flex-1 overflow-y-auto">
      <!-- 헤더 (모바일) - sticky -->
      <div class="md:hidden sticky top-0 z-40 backdrop-blur-md bg-zinc-900/90 border-b border-zinc-800 p-4">
        <select
            v-model="selectedCategory"
            @change="changeCategory(selectedCategory)"
            class="w-full px-4 py-2 bg-zinc-800 text-white rounded-lg border border-zinc-700 focus:border-blue-500 focus:outline-none"
            :disabled="isCategoriesLoading"
        >
          <option value="all">전체</option>
          <template v-for="category in categories.filter(c => c.type === 'group')" :key="category.id">
            <optgroup :label="category.name">
              <option v-for="child in category.children" :key="child.id" :value="child.id">
                {{ child.name }}
              </option>
            </optgroup>
          </template>
        </select>
      </div>

      <!-- 그리드 -->
      <div class="p-4 md:p-8">
        <!-- 태그 필터 -->
        <div class="mb-6">
          <TagFilter v-model="selectedTags" @change="handleTagFilter" :limit="20"/>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div
              v-for="post in posts"
              :key="post.id"
              @click="openImageModal(post)"
              class="group relative aspect-square rounded-2xl overflow-hidden bg-zinc-800 shadow-lg hover:shadow-2xl transition-all duration-300 cursor-pointer"
          >
            <!-- 이미지 -->
            <img
                :src="getImage(post)?.url"
                class="w-full h-full object-cover"
            />

            <!-- 모바일: 항상 보이는 액션 버튼 -->
            <div class="md:hidden absolute bottom-3 right-3 flex gap-2" @click.stop>
              <!-- 좋아요 -->
              <button
                  @click="toggleLike(post)"
                  class="w-10 h-10 rounded-full backdrop-blur-md flex items-center justify-center transition-all shadow-lg"
                  :class="isLiked(post)
                  ? 'bg-red-500/90 text-white'
                  : 'bg-black/40 text-white'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :fill="isLiked(post) ? 'currentColor' : 'none'"
                     viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </button>

              <!-- 복사 -->
              <button
                  @click="copyImageUrl(post)"
                  class="w-10 h-10 rounded-full bg-black/40 backdrop-blur-md text-white flex items-center justify-center shadow-lg"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                </svg>
              </button>
            </div>

            <!-- 데스크톱: 호버 시 보이는 오버레이 -->
            <div
                class="hidden md:block absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none">
              <div class="absolute bottom-0 left-0 right-0 p-4 pointer-events-auto" @click.stop>
                <!-- 업로더 -->
                <div class="flex items-center gap-2 mb-3">
                  <div
                      class="w-7 h-7 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white text-xs font-bold">
                    {{ getWriter(post).charAt(0).toUpperCase() }}
                  </div>
                  <span class="text-white text-sm font-medium">{{ getWriter(post) }}</span>
                </div>

                <!-- Content (Posts만) -->
                <p v-if="post.content" class="text-gray-300 text-xs line-clamp-2 mb-3">{{ post.content }}</p>

                <!-- 액션 버튼 -->
                <div class="flex gap-2">
                  <button
                      @click="toggleLike(post)"
                      class="flex-1 flex items-center justify-center gap-2 py-2 rounded-lg transition-all"
                      :class="isLiked(post)
                      ? 'bg-red-500 text-white'
                      : 'bg-white/20 text-white hover:bg-white/30'"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4"
                         :fill="isLiked(post) ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                    </svg>
                    <span class="text-xs font-medium">좋아요</span>
                  </button>

                  <button
                      @click="copyImageUrl(post)"
                      class="flex-1 flex items-center justify-center gap-2 py-2 bg-white/20 text-white hover:bg-white/30 rounded-lg transition-all"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24"
                         stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                    </svg>
                    <span class="text-xs font-medium">복사</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 로딩 -->
        <div v-if="isLoading" class="flex justify-center py-12">
          <div class="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
        </div>

        <!-- 더 이상 없음 -->
        <div v-if="!hasMore && posts.length > 0" class="text-center py-12">
          <p class="text-gray-500">더 이상 게시물이 없습니다</p>
        </div>

        <!-- 빈 상태 -->
        <div v-if="!isLoading && posts.length === 0" class="text-center py-20">
          <div class="mb-4 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1" stroke="currentColor" class="w-20 h-20 text-gray-600">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 13.5h3.86a2.25 2.25 0 012.012 1.244l.256.512a2.25 2.25 0 002.013 1.244h3.218a2.25 2.25 0 002.013-1.244l.256-.512a2.25 2.25 0 012.013-1.244h3.859m-19.5.338V18a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18v-4.162c0-.224-.034-.447-.1-.661L19.24 5.338a2.25 2.25 0 00-2.15-1.588H6.911a2.25 2.25 0 00-2.15 1.588L2.35 13.177a2.25 2.25 0 00-.1.661z" />
            </svg>
          </div>
          <p class="text-gray-400 text-lg">아직 게시물이 없습니다</p>
          <p class="text-gray-600 text-sm mt-2">첫 번째 고양이 사진을 공유해보세요!</p>
        </div>
      </div>
    </main>

    <!-- 플로팅 업로드 버튼 -->
    <button
        @click="openUploadModal"
        class="fixed bottom-8 right-8 w-16 h-16 bg-gradient-to-br from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white rounded-full shadow-2xl flex items-center justify-center transition-all duration-300 hover:scale-110 z-50"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
      </svg>
    </button>

    <!-- 모달들 -->
    <ImageModal
        v-if="showImageModal"
        :post="selectedPost"
        @close="showImageModal = false"
    />
    <UploadModal
        v-if="showUploadModal"
        @close="showUploadModal = false"
        @uploaded="handleUploaded"
    />
  </div>
</template>

<style scoped>
/* 텍스트 말줄임 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 스크롤바 커스텀 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #18181b;
}

::-webkit-scrollbar-thumb {
  background: #3f3f46;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #52525b;
}
</style>