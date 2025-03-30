import Vuex from 'vuex';

const store = new Vuex.Store({
  state: {
    memberId: null,
    userNickname: null,
    isLogin: false,
  },
  mutations: {
    setUser(state, user) {
      state.memberId = user.id;
      state.userNickname = user.nickname;
      state.isLogin = true;
    },
    clearUser(state) {
      state.memberId = null;
      state.userNickname = null;
      state.isLogin = false;
    },
  },
  actions: {
    login({ commit }, user) {
      commit('setUser', user);
    },
    logout({ commit }) {
      commit('clearUser');
    },
  },
});

export default store;
