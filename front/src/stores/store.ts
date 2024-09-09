import Vuex from 'vuex';

const store = new Vuex.Store({
  state: {
    userId: null,
    userNickname: null,
  },
  mutations: {
    setUser(state, user) {
      state.userId = user.id;
      state.userNickname = user.nickname;
    },
    clearUser(state) {
      state.userId = null;
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
