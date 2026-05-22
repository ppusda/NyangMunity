import type { Config } from 'tailwindcss';
import animate from 'tailwindcss-animate';

export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        background: 'var(--bg-base)',
        foreground: 'var(--text-primary)',
        border: 'var(--border-default)',
        input: 'var(--border-default)',
        ring: 'var(--accent-hover)',
        surface: 'var(--bg-surface)',
        elevated: 'var(--bg-elevated)',
        primary: {
          DEFAULT: 'var(--accent-base)',
          foreground: '#FFFFFF',
        },
        secondary: {
          DEFAULT: 'var(--bg-elevated)',
          foreground: 'var(--text-primary)',
        },
        muted: {
          DEFAULT: 'var(--bg-surface)',
          foreground: 'var(--text-secondary)',
        },
        accent: {
          DEFAULT: 'var(--accent-subtle)',
          foreground: 'var(--text-primary)',
          base: 'var(--accent-base)',
          hover: 'var(--accent-hover)',
          bright: 'var(--accent-bright)',
        },
        destructive: {
          DEFAULT: 'var(--like)',
          foreground: '#FFFFFF',
        },
        card: {
          DEFAULT: 'var(--bg-surface)',
          foreground: 'var(--text-primary)',
        },
        popover: {
          DEFAULT: 'var(--bg-elevated)',
          foreground: 'var(--text-primary)',
        },
        text: {
          primary: 'var(--text-primary)',
          secondary: 'var(--text-secondary)',
          tertiary: 'var(--text-tertiary)',
        },
        like: 'var(--like)',
        success: 'var(--success)',
        warning: 'var(--warning)',
        error: 'var(--error)',
      },
      borderRadius: {
        sm: 'var(--r-1)',
        DEFAULT: 'var(--r-2)',
        md: 'var(--r-2)',
        lg: 'var(--r-3)',
        xl: 'var(--r-4)',
      },
      fontFamily: {
        kr: ['var(--font-kr)'],
        en: ['var(--font-en)'],
      },
      transitionTimingFunction: {
        out: 'cubic-bezier(0.16, 1, 0.3, 1)',
      },
      keyframes: {
        'accordion-down': {
          from: { height: '0' },
          to: { height: 'var(--radix-accordion-content-height)' },
        },
        'accordion-up': {
          from: { height: 'var(--radix-accordion-content-height)' },
          to: { height: '0' },
        },
      },
      animation: {
        'accordion-down': 'accordion-down 0.2s ease-out',
        'accordion-up': 'accordion-up 0.2s ease-out',
      },
    },
  },
  plugins: [animate],
} satisfies Config;
