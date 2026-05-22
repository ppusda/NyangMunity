import { useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { SortTabs } from '@/components/gallery/SortTabs';
import { BreedChips } from '@/components/gallery/BreedChips';
import { MasonryGrid } from '@/components/gallery/MasonryGrid';
import { CurationHero } from '@/components/gallery/CurationHero';
import { useGalleryImages, usePopularImages, useProviders } from '@/hooks/useImages';
import { useGalleryStats } from '@/hooks/useGalleryStats';
import type { Provider, SortKey } from '@/types';

export function GalleryPage() {
  const [sort, setSort] = useState<SortKey>('latest');
  const [provider, setProvider] = useState<Provider | undefined>(undefined);

  const { data: providers } = useProviders();
  const { data: popular } = usePopularImages(7);
  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useGalleryImages({ sort, provider });

  const flat = useMemo(
    () => (data?.pages ?? []).flatMap((p) => p.content),
    [data],
  );

  const stats = useGalleryStats(data?.pages, popular);

  return (
    <div className="pb-16">
      <CurationHero items={popular ?? []} stats={stats} />

      <section className="px-8 pt-7">
        <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-4">
          <div>
            <h2 className="text-xl font-semibold tracking-tight">전체 갤러리</h2>
            <p className="text-sm text-text-secondary">방금 올라온 사진부터 시간순으로.</p>
          </div>
          <SortTabs value={sort} onChange={setSort} />
        </div>

        <div className="mb-5">
          <BreedChips
            providers={providers ?? []}
            selected={provider}
            onSelect={setProvider}
          />
        </div>

        {isError ? (
          <div className="text-center text-sm text-error py-10">
            이미지를 불러오지 못했어요.
          </div>
        ) : isLoading ? (
          <div className="text-center text-sm text-text-tertiary py-10">불러오는 중...</div>
        ) : flat.length === 0 ? (
          <div className="text-center text-sm text-text-tertiary py-10">
            조건에 맞는 사진이 없어요.
          </div>
        ) : (
          <>
            <MasonryGrid
              items={flat}
              onReachEnd={() => fetchNextPage()}
              isLoadingMore={isFetchingNextPage}
              hasMore={hasNextPage}
            />
            {hasNextPage && !isFetchingNextPage && (
              <div className="flex justify-center mt-8">
                <Button variant="outline" onClick={() => fetchNextPage()}>
                  더 보기
                </Button>
              </div>
            )}
          </>
        )}
      </section>
    </div>
  );
}
