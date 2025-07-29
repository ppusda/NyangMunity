import Vuex from 'vuex';

const member = new Vuex.Store({
    state: {
        memberId: null,
        email: null,
        nickname: null,
        isLogin: false,
        accessToken: null,
        refreshToken: null,
    },
    mutations: {
        setMember(state, member) {
            state.memberId = member.id;
            state.email = member.email
            state.nickname = member.nickname;
            state.isLogin = true;
        },
        clearMember(state) {
            state.memberId = null;
            state.email = null;
            state.nickname = null;
            state.isLogin = false;
        },
        setTokens(state, { accessToken, refreshToken }) {
            state.accessToken = accessToken;
            state.refreshToken = refreshToken;
        },
        clearTokens(state) {
            state.accessToken = null;
            state.refreshToken = null;
        },
    },
    actions: {
        login({commit}, member) {
            commit('setMember', member);
        },
        logout({commit}) {
            commit('clearMember');
            commit('clearTokens');
        },
        saveTokens({ commit }, { accessToken, refreshToken }) {
            commit('setTokens', { accessToken, refreshToken });
        }
    },
});

export default member;
