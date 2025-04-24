import {useCookies} from "vue3-cookies";
import {warningToast} from "@/libs/toaster";

import store from "@/stores/store";
import axiosClient from "@/libs/axiosClient";
import type {MemberResponse} from "@/interfaces/type";

const {cookies} = useCookies();

export const login = function (email: string, password: string) {
    // 로그인 (서버 측 토큰 생성)
    return axiosClient.post("/members/login", {email: email, password: password})
        .then(response => {
            const memberResponse: MemberResponse = {
                id: response.data.id,
                email: response.data.email,
                nickname: response.data.nickname
            };
            saveMemberInfo(memberResponse);
            return true;
        })
        .catch(() => {
            return false;
        });
}

export const saveMemberInfo = function (memberResponse: MemberResponse) {
    store.dispatch('login', memberResponse);
    localStorage.setItem('member', JSON.stringify(memberResponse));
}

export const logout = function () {
    // 로그아웃 (서버 측 토큰 삭제)
    axiosClient.post("/members/logout").then(() => {

        // store 변수 비우기
        store.dispatch('logout');

        // cookie 비우기
        cookies.remove("accessToken");
        cookies.remove("refreshToken");

        // storage 비우기
        localStorage.removeItem('member');
    });
};

export const reissue = function (): Promise<MemberResponse> {
    return axiosClient.post("/tokens").then((response) => {
        return response.data as MemberResponse;
    });
}

export const isTokenExist = function (): Boolean {
    if (cookies.get("accessToken") || cookies.get("refreshToken")) {
        return true;
    }

    return false;
}