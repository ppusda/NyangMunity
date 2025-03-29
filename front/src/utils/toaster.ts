import {toast} from "vue3-toastify";

export const warningToast = (message: string) => {
    toast(message, {
        autoClose: 2000,
        theme: "dark",
        type: "warning",
    });
}

export const infoToast = (message: string) => {
    toast(message, {
        autoClose: 2000,
        theme: "dark",
        type: "info",
    });
}