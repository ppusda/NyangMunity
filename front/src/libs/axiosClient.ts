import axios from 'axios';

import {useCookies} from "vue3-cookies";
import {warningToast} from "@/libs/toaster";
import {logout, reissue} from "@/utils/account";

const { cookies } = useCookies();

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
        logout();
        return;
      }

      // 응답 값이 401이며, 재요청 플래그가 false인 경우
      if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true; // 재요청 플래그 설정

        const reissueResponse = reissue(); // 토큰 재발급
        if (reissueResponse) {
          axios.defaults.headers.common['Authorization'] = `${reissueResponse}`; // 새 토큰으로 헤더 설정
          originalRequest.headers['Authorization'] = `${reissueResponse}`; // 현재 요청에도 새 토큰 설정

          return axiosClient(originalRequest); // 요청 재실행
        }
      }

      if (error.response) {
        const errorData = error.response.data;
        if (errorData.validMessages !== null) {
          if (errorData.validMessages.username) {
            warningToast(errorData.validMessages.username);
            return Promise.reject(error);
          }

          if (errorData.validMessages.nickname) {
            warningToast(errorData.validMessages.nickname);
            return Promise.reject(error);
          }

          if (errorData.validMessages.password) {
            warningToast(errorData.validMessages.password);
            return Promise.reject(error);
          }

          if (errorData.validMessages.newPassword) {
            warningToast(errorData.validMessages.newPassword);
            return Promise.reject(error);
          }

          if (errorData.validMessages.newPasswordCheck) {
            warningToast(errorData.validMessages.newPasswordCheck);
            return Promise.reject(error);
          }

          if (errorData.validMessages.email) {
            warningToast(errorData.validMessages.email);
            return Promise.reject(error);
          }
        }
        // 기타 에러 메시지 처리
        warningToast(errorData.message);
        return Promise.reject(error);
      } else {
        // 네트워크 에러 등 기타 에러 처리
        warningToast("네트워크 오류가 발생했습니다.");
      }

      return Promise.reject(error);
    }
);

export default axiosClient;