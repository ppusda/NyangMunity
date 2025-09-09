import axios from 'axios';

import {useCookies} from "vue3-cookies";
import {warningToast} from "@/libs/toaster";
import {logout, reissue, saveMemberAuthentication} from "@/utils/account";

const {cookies} = useCookies();

// 토큰 재발급 진행 중인지 확인하는 변수
let isRefreshing = false;
// 토큰 재발급 대기 중인 요청들
let refreshSubscribers: Array<(token?: string) => void> = [];

// 대기 요청 등록
const addRefreshSubscriber = (callback: (token?: string) => void) => {
    refreshSubscribers.push(callback);
};

// Axios 인스턴스 생성
const axiosClient = axios.create({
    baseURL: import.meta.env.DEV ? "http://localhost:8080" : "https://api.nyangmunity.bbgk.me",
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터
axiosClient.interceptors.request.use(
    config => {
        // 요청을 보내기 전에 수행할 로직
        const token = cookies.get("accessToken")
        if (token) {
            config.headers['Authorization'] = "Bearer " + token;
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

        // 401 에러가 아니면 바로 에러 반환
        if (error.response?.status !== 401) {
            if (error.response) {
                const errorData = error.response.data;
                warningToast(errorData.message || "요청 중 오류가 발생했습니다.");
            } else {
                warningToast("네트워크 오류가 발생했습니다.");
            }
            return Promise.reject(error);
        }

        // 이미 재요청을 시도한 경우 (originalRequest에 직접 플래그 추가)
        if (originalRequest._retry) {
            warningToast("재로그인 해주세요!");
            logout();
            return Promise.reject(error);
        }

        // 첫 번째 401 에러이고 아직 토큰 재발급이 진행 중이 아닌 경우
        if (!isRefreshing) {
            isRefreshing = true;
            originalRequest._retry = true;

            try {
                // 토큰 재발급 요청
                const reissueResponse = await reissue();

                if (reissueResponse) {
                    // 토큰 갱신
                    saveMemberAuthentication(reissueResponse);
                    originalRequest.headers['Authorization'] = "Bearer " + reissueResponse.memberTokens.accessToken;

                    // 현재 요청 다시 시도
                    return axiosClient(originalRequest);
                } else {
                    // 재발급 응답이 없는 경우
                    warningToast("인증에 실패했습니다. 다시 로그인해주세요.");
                    logout();
                    return Promise.reject(error);
                }
            } catch (reissueError) {
                // 재발급 실패
                warningToast("세션이 만료되었습니다. 다시 로그인해주세요.");
                logout();
                return Promise.reject(reissueError);
            } finally {
                isRefreshing = false;
            }
        } else {
            // 이미 다른 요청에서 토큰 재발급이 진행 중인 경우
            // 현재 요청을 대기열에 추가하고 토큰이 갱신되면 자동으로 재시도
            return new Promise((resolve) => {
                addRefreshSubscriber(() => {
                    resolve(axiosClient(originalRequest));
                });
            });
        }
    }
);

export default axiosClient;