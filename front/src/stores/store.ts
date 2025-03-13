import Vuex from 'vuex';

const store = new Vuex.Store({
  state: {
    memberId: null,
    userNickname: null,
  },
  mutations: {
    setUser(state, user) {
      state.memberId = user.id;
      state.userNickname = user.nickname;
    },
    clearUser(state) {
      state.memberId = null;
      state.userNickname = null;
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
