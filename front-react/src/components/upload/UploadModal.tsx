import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Image as ImageIcon, Repeat2, Upload, X, Clock, AlertTriangle } from 'lucide-react';
import { toast } from 'sonner';
import { imagesApi } from '@/api/images';
import { useAuthStore } from '@/stores/authStore';
import { cn } from '@/lib/utils';

interface Props {
  open: boolean;
  onClose: () => void;
}

const MAX_DESCRIPTION = 500;
const SUGGESTED_TAGS = ['#아기냥', '#식빵자세', '#골골송', '#창문냥', '#박스고양이', '#하품'];

function bytesToReadable(n: number): string {
  if (n < 1024) return `${n} B`;
  if (n < 1024 * 1024) return `${(n / 1024).toFixed(1)} KB`;
  return `${(n / 1024 / 1024).toFixed(1)} MB`;
}

function normalizeTag(raw: string): string | null {
  const trimmed = raw.trim().replace(/^#+/, '');
  if (!trimmed) return null;
  return trimmed;
}

export function UploadModal({ open, onClose }: Props) {
  const isLogin = useAuthStore((s) => s.isLogin);
  const qc = useQueryClient();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [file, setFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [description, setDescription] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState('');
  const [isDragging, setIsDragging] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const resetForm = useCallback(() => {
    setFile(null);
    setPreviewUrl((current) => {
      if (current) URL.revokeObjectURL(current);
      return null;
    });
    setDescription('');
    setTags([]);
    setTagInput('');
    setIsDragging(false);
  }, []);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !isSubmitting) onClose();
    };
    window.addEventListener('keydown', onKey);
    document.body.style.overflow = 'hidden';
    return () => {
      window.removeEventListener('keydown', onKey);
      document.body.style.overflow = '';
    };
  }, [open, onClose, isSubmitting]);

  useEffect(() => {
    if (!open) resetForm();
  }, [open, resetForm]);

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const handleFileSelect = (selected: File | null) => {
    if (!selected) return;
    if (!selected.type.startsWith('image/')) {
      toast.warning('이미지 파일만 올릴 수 있어요.');
      return;
    }
    setPreviewUrl((current) => {
      if (current) URL.revokeObjectURL(current);
      return URL.createObjectURL(selected);
    });
    setFile(selected);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    const dropped = e.dataTransfer.files?.[0];
    if (dropped) handleFileSelect(dropped);
  };

  const addTag = (raw: string) => {
    const normalized = normalizeTag(raw);
    if (!normalized) return;
    setTags((current) =>
      current.includes(normalized) || current.length >= 10
        ? current
        : [...current, normalized],
    );
    setTagInput('');
  };

  const removeTag = (t: string) => {
    setTags((current) => current.filter((x) => x !== t));
  };

  const handleTagKey = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if ((e.key === 'Enter' || e.key === ',' || e.key === ' ') && tagInput.trim()) {
      e.preventDefault();
      addTag(tagInput);
    } else if (e.key === 'Backspace' && !tagInput && tags.length > 0) {
      setTags((current) => current.slice(0, -1));
    }
  };

  const handleSubmit = async () => {
    if (!isLogin) {
      toast.warning('로그인이 필요해요.');
      return;
    }
    if (!file) {
      toast.warning('사진을 먼저 선택해주세요.');
      return;
    }
    setIsSubmitting(true);
    try {
      const { id, uploadUrl } = await imagesApi.requestUploadUrl(file.name);
      await imagesApi.uploadToS3(uploadUrl, file);
      await imagesApi.completeUpload({
        imageId: id,
        description: description.trim() || undefined,
        tags: tags.length > 0 ? tags : undefined,
      });
      toast.success('사진이 올라갔어요. 90일 카운트다운이 시작됐어요.');
      await qc.invalidateQueries({ queryKey: ['images', 'list'], exact: false });
      await qc.invalidateQueries({ queryKey: ['images', 'popular'], exact: false });
      await qc.invalidateQueries({ queryKey: ['images', 'topLike'] });
      onClose();
    } catch (err) {
      // axios 인터셉터가 토스트, 여기는 상태만 복구
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const remainingDescription = MAX_DESCRIPTION - description.length;
  const previewMeta = useMemo(() => {
    if (!file) return null;
    return `${bytesToReadable(file.size)}`;
  }, [file]);

  if (!open) return null;

  const isEmpty = !file;

  return (
    <div
      className="up-root"
      role="dialog"
      aria-modal="true"
      aria-labelledby="upload-title"
      onClick={(e) => {
        if (e.target === e.currentTarget && !isSubmitting) onClose();
      }}
    >
      <div className={cn('up-modal', isEmpty && 'up-modal--empty')}>
        <header className="up-header">
          <div>
            <h2 id="upload-title" className="up-title">사진 올리기</h2>
            <p className="up-subtitle">
              올리는 순간 <em>90일 카운트다운</em>이 시작됩니다. 좋아요 50개를 받으면 영원히 남습니다.
            </p>
          </div>
          <button
            type="button"
            className="up-close"
            aria-label="닫기"
            onClick={onClose}
            disabled={isSubmitting}
          >
            <X size={16} />
          </button>
        </header>

        {isEmpty ? (
          <div className="up-empty">
            <div
              className={cn('up-empty-zone', isDragging && 'is-dragging')}
              onClick={() => fileInputRef.current?.click()}
              onDragOver={(e) => {
                e.preventDefault();
                setIsDragging(true);
              }}
              onDragLeave={() => setIsDragging(false)}
              onDrop={handleDrop}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  fileInputRef.current?.click();
                }
              }}
            >
              <div className="up-empty-icon">
                <Upload />
              </div>
              <h3 className="up-empty-title">사진을 끌어다 놓으세요</h3>
              <p className="up-empty-sub">
                또는 클릭해서 파일을 선택할 수 있어요. JPG · PNG · WebP
              </p>
              <button
                type="button"
                className="nm-btn nm-btn--primary nm-btn--lg"
                onClick={(e) => {
                  e.stopPropagation();
                  fileInputRef.current?.click();
                }}
              >
                <ImageIcon size={16} /> 사진 선택
              </button>
            </div>
          </div>
        ) : (
          <div className="up-body">
            <section className="up-preview">
              <div className="up-dropzone">
                {previewUrl && (
                  <img src={previewUrl} alt="" className="up-preview-img" />
                )}
                <div className="up-preview-shade" />
                <button
                  type="button"
                  className="up-preview-replace"
                  onClick={() => fileInputRef.current?.click()}
                >
                  <Repeat2 /> 다른 사진으로 교체
                </button>
                <div className="up-preview-info">
                  <div className="up-preview-name">{file?.name}</div>
                  <div className="up-preview-meta">{previewMeta}</div>
                </div>
              </div>
            </section>

            <section className="up-form nm-scroll">
              <div className="up-field">
                <label className="up-label" htmlFor="up-tags">
                  태그 <span className="up-label-opt">선택 · 최대 10개</span>
                </label>
                <div className="up-tags-input">
                  {tags.map((t) => (
                    <span key={t} className="up-tag">
                      #{t}
                      <button
                        type="button"
                        onClick={() => removeTag(t)}
                        aria-label={`${t} 태그 제거`}
                      >
                        <X />
                      </button>
                    </span>
                  ))}
                  <input
                    id="up-tags"
                    className="up-tag-field"
                    value={tagInput}
                    onChange={(e) => setTagInput(e.target.value)}
                    onKeyDown={handleTagKey}
                    placeholder={tags.length === 0 ? '#아기냥, #자연광 처럼' : ''}
                  />
                </div>
                <div className="up-suggest">
                  {SUGGESTED_TAGS.filter((s) => !tags.includes(s.replace(/^#/, ''))).map(
                    (s) => (
                      <button
                        type="button"
                        key={s}
                        className="up-suggest-chip"
                        onClick={() => addTag(s)}
                      >
                        {s}
                      </button>
                    ),
                  )}
                </div>
              </div>

              <div className="up-field">
                <label className="up-label" htmlFor="up-desc">
                  설명 <span className="up-label-opt">선택</span>
                </label>
                <textarea
                  id="up-desc"
                  className="up-textarea"
                  rows={4}
                  maxLength={MAX_DESCRIPTION}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="이 순간에 대한 이야기를 적어보세요."
                />
                <span className="up-counter">
                  {remainingDescription} / {MAX_DESCRIPTION}
                </span>
              </div>

              <div className="up-notice">
                <AlertTriangle className="up-notice-icon" />
                <div className="up-notice-text">
                  <strong>업로드 안내</strong>
                  <br />
                  업로드 후 90일 안에 좋아요 50개를 받으면 영구 보관됩니다. 그렇지 않으면 자동으로 삭제돼요.
                </div>
              </div>
            </section>
          </div>
        )}

        <footer className="up-footer">
          <div className="up-clock">
            <span className="up-clock-dot" aria-hidden="true" />
            <span className="up-clock-text">
              <Clock size={12} style={{ display: 'inline', verticalAlign: '-2px', marginRight: 4 }} />
              올리면 <em>D-90</em> 카운트다운 시작
            </span>
          </div>
          <div className="up-footer-right">
            <button
              type="button"
              className="nm-btn nm-btn--ghost"
              onClick={onClose}
              disabled={isSubmitting}
            >
              취소
            </button>
            <button
              type="button"
              className="nm-btn nm-btn--primary"
              onClick={handleSubmit}
              disabled={!file || isSubmitting}
            >
              <Upload size={14} />
              {isSubmitting ? '올리는 중...' : '올리기'}
            </button>
          </div>
        </footer>

        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          hidden
          onChange={(e) => {
            const selected = e.target.files?.[0] ?? null;
            handleFileSelect(selected);
            e.target.value = '';
          }}
        />
      </div>
    </div>
  );
}
