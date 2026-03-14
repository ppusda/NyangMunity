import Vuex from 'vuex';

// State 타입 정의
interface MemberState {
  memberId: string | null;
  email: string | null;
  nickname: string | null;
  isLogin: boolean;
  accessToken: string | null;
  refreshToken: string | null;
}

// Member 인터페이스
interface Member {
  id: string;
  email: string;
  nickname: string;
}

// Tokens 인터페이스
interface Tokens {
  accessToken: string;
  refreshToken: string;
}

const member = new Vuex.Store<MemberState>({
  state: {
    memberId: null,
    email: null,
    nickname: null,
    isLogin: false,
    accessToken: null,
    refreshToken: null,
  },
  mutations: {
    setMember(state: MemberState, member: Member) {
      state.memberId = member.id;
      state.email = member.email;
      state.nickname = member.nickname;
      state.isLogin = true;
    },
    clearMember(state: MemberState) {
      state.memberId = null;
      state.email = null;
      state.nickname = null;
      state.isLogin = false;
    },
    setTokens(state: MemberState, { accessToken, refreshToken }: Tokens) {
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
    },
    clearTokens(state: MemberState) {
      state.accessToken = null;
      state.refreshToken = null;
    },
  },
  actions: {
    login({ commit }: { commit: any }, member: Member) {
      commit('setMember', member);
    },
    logout({ commit }: { commit: any }) {
      commit('clearMember');
      commit('clearTokens');
    },
    saveTokens({ commit }: { commit: any }, { accessToken, refreshToken }: Tokens) {
      commit('setTokens', { accessToken, refreshToken });
    }
  },
});

export default member;
