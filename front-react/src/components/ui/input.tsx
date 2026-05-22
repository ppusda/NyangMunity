import * as React from 'react';
import { cn } from '@/lib/utils';

export const Input = React.forwardRef<HTMLInputElement, React.InputHTMLAttributes<HTMLInputElement>>(
  ({ className, type, ...props }, ref) => (
    <input
      type={type}
      ref={ref}
      className={cn(
        'flex h-9 w-full rounded-md border border-border bg-surface px-3 py-1 text-sm text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-accent-hover focus:ring-[3px] focus:ring-accent disabled:cursor-not-allowed disabled:opacity-50 transition-shadow',
        className,
      )}
      {...props}
    />
  ),
);
Input.displayName = 'Input';
