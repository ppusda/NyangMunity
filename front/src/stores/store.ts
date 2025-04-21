import Vuex from 'vuex';

const member = new Vuex.Store({
    state: {
        memberId: null,
        email: null,
        nickname: null,
        isLogin: false,
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

    },
    actions: {
        login({commit}, member) {
            commit('setMember', member);
        },
        logout({commit}) {
            commit('clearMember');
        },
    },
});

export default member;
