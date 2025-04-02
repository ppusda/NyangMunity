import {useCookies} from "vue3-cookies";
import {warningToast} from "@/utils/toaster";

import axios from "axios";
import store from "@/stores/store";

const { cookies } = useCookies();

export const login = function (email: string, password: string) {
    // 로그인 (서버 측 토큰 생성)
    axios.post("/nm/members/login", {email: email, password: password}, { withCredentials: true })
        .then(response => {
            const memberData = {
                id: response.data.id,
                nickname: response.data.nickname
            };

            store.dispatch('login', memberData);
            localStorage.setItem('member', JSON.stringify(memberData));
        })
        .catch(error => {
            if (error.response) {
                warningToast(error.response.data.message);
            } else {
                warningToast("계정이 올바르지 않습니다.");
            }
        });
}

export const logout = function () {
    // 로그아웃 (서버 측 토큰 삭제)
    axios.post("/nm/members/logout").then(() => {

        // store 변수 비우기
        store.dispatch('logout');

        // cookie 비우기
        cookies.remove("accessToken");
        cookies.remove("refreshToken");

        // storage 비우기
        localStorage.removeItem('member');
    });
};