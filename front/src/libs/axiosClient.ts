import axios from 'axios';
import {useCookies} from "vue3-cookies";
import {warningToast} from "@/libs/toaster";
import {logout, reissue, saveMemberAuthentication} from "@/utils/account";

const {cookies} = useCookies();

let isRefreshing = false;
let refreshSubscribers: Array<(token?: string) => void> = [];

const addRefreshSubscriber = (callback: (token?: string) => void) => {
    refreshSubscribers.push(callback);
};

const onRefreshed = (token?: string) => {
    refreshSubscribers.forEach(callback => callback(token));
    refreshSubscribers = [];
};

const axiosClient = axios.create({
    baseURL: import.meta.env.DEV ? "http://localhost:8080" : "https://api.nyangmunity.bbgk.me",
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.request.use(
    config => {
        const token = cookies.get("accessToken")
        if (token) {
            config.headers['Authorization'] = "Bearer " + token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

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

        // 이미 재요청을 시도한 경우
        if (originalRequest._retry) {
            warningToast("재로그인 해주세요!");
            logout();
            return Promise.reject(error);
        }

        // 첫 번째 401 에러
        if (!isRefreshing) {
            isRefreshing = true;
            originalRequest._retry = true;

            try {
                const reissueResponse = await reissue();

                if (reissueResponse) {
                    // 토큰 갱신
                    saveMemberAuthentication(reissueResponse);
                    const newToken = reissueResponse.memberTokens.accessToken;

                    originalRequest.headers['Authorization'] = "Bearer " + newToken;
                    onRefreshed(newToken);

                    // 현재 요청 재시도
                    return axiosClient(originalRequest);
                } else {
                    warningToast("인증에 실패했습니다. 다시 로그인해주세요.");
                    onRefreshed(); // 대기 중인 요청들도 정리
                    logout();
                    return Promise.reject(error);
                }
            } catch (reissueError) {
                warningToast("세션이 만료되었습니다. 다시 로그인해주세요.");
                onRefreshed(); // 대기 중인 요청들도 정리
                logout();
                return Promise.reject(reissueError);
            } finally {
                isRefreshing = false;
            }
        } else {
            // 토큰 재발급 진행 중 - 대기
            return new Promise((resolve, reject) => {
                addRefreshSubscriber((token) => {
                    if (token) {
                        originalRequest.headers['Authorization'] = "Bearer " + token;
                        resolve(axiosClient(originalRequest));
                    } else {
                        reject(error);
                    }
                });
            });
        }
    }
);

export default axiosClient;