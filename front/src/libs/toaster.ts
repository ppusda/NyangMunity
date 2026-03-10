// toaster.ts
import {toast, type ToastOptions} from 'vue3-toastify'

const defaultOptions: ToastOptions = {
    autoClose: 2000,
    theme: 'dark',
    position: 'top-right',
}

export const warningToast = (message: string, options?: ToastOptions) => {
    toast.warning(message, {...defaultOptions, ...options})
}

export const infoToast = (message: string, options?: ToastOptions) => {
    toast.info(message, {...defaultOptions, ...options})
}

export const successToast = (message: string, options?: ToastOptions) => {
    toast.success(message, {...defaultOptions, ...options})
}

export const errorToast = (message: string, options?: ToastOptions) => {
    toast.error(message, {...defaultOptions, ...options})
}