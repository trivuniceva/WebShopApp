import { createStore } from 'vuex'

export default createStore({
  state: {
    loggedUser: null,
    token: null,
  },
  mutations: {
    setLoggedUser(state, user) {
      state.loggedUser = user
    },
    clearLoggedUser(state) {
      state.loggedUser = null
      state.token = null
    },
    setToken(state, token) {
      state.token = token;
    }
  },
  actions: {
    initializeStore({ commit }) {
      const user = localStorage.getItem('loggedUser');
      if (user) {
        commit('setLoggedUser', JSON.parse(user));
      }
    }
  },
  getters: {
    getLoggedUser: (state) => state.loggedUser,
    getToken: (state) => state.token,
    isAdmin: (state) => state.loggedUser && state.loggedUser.role === 'Administrator',
  }
})