import { useMemo } from 'react';
import type { ImageItem, ImagePage } from '@/types';

interface Result {
  survived: number | null;
  joinedToday: number;
  mostFragileDays: number | null;
}

function isSameLocalDay(iso: string, ref: Date): boolean {
  const d = new Date(iso);
  return (
    d.getFullYear() === ref.getFullYear() &&
    d.getMonth() === ref.getMonth() &&
    d.getDate() === ref.getDate()
  );
}

export function useGalleryStats(
  pages: ImagePage[] | undefined,
  topLiked: ImageItem[] | undefined,
): Result {
  return useMemo(() => {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const flat = (pages ?? []).flatMap((p) => p.content);

    const survived = pages?.[0]?.totalElements ?? null;

    const joinedToday = flat.filter((img) => isSameLocalDay(img.uploadDate, today)).length;

    const allCandidates = [...flat, ...(topLiked ?? [])];
    const validExpiry = allCandidates
      .map((img) => img.expiresAt)
      .filter((v): v is string => Boolean(v))
      .map((v) => Math.ceil((new Date(v).getTime() - now.getTime()) / 86_400_000))
      .filter((days) => days > 0);

    const mostFragileDays = validExpiry.length > 0 ? Math.min(...validExpiry) : null;

    return { survived, joinedToday, mostFragileDays };
  }, [pages, topLiked]);
}
