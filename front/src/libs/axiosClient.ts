import axios from 'axios';

import {useCookies} from "vue3-cookies";
import {warningToast} from "@/libs/toaster";
import {logout, reissue, saveMemberInfo} from "@/utils/account";

const {cookies} = useCookies();

// Axios 인스턴스 생성
const axiosClient = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터
axiosClient.interceptors.request.use(
    config => {
        // 요청을 보내기 전에 수행할 로직 (예: 토큰 설정)
        const token = cookies.get("accessToken")
        if (token) {
            config.headers['Authorization'] = token;
        }
        return config;
    },
    error => {
        // 요청 에러 처리
        return Promise.reject(error);
    }
);

// 응답 인터셉터
axiosClient.interceptors.response.use(
    response => {
        return response;
    },
    async error => {
        const originalRequest = error.config;
        // 응답 값이 401이며, 재요청 플래그가 true인 경우
        if (error.response.status === 401 && originalRequest._retry) {
            warningToast("재로그인 해주세요!");
            logout();
            return;
        }

        // 응답 값이 401이며, 재요청 플래그가 false인 경우
        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true; // 재요청 플래그 설정

            const reissueResponse = await reissue(); // 토큰 재발급
            if (reissueResponse) {
                saveMemberInfo(reissueResponse);
                return axiosClient(originalRequest); // 요청 재실행
            }
        }

        if (error.response) {
            // 에러 메시지 처리
            const errorData = error.response.data;
            warningToast(errorData.message);
            return Promise.reject(error);
        } else {
            // 기타 에러 발생 시
            warningToast("예기치 못한 오류가 발생했습니다.");
        }

        return Promise.reject(error);
    }
);

export default axiosClient;