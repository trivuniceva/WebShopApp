import { createStore } from 'vuex'

export default createStore({
  state: {
    loggedUser: null,
  },
  mutations: {
    setLoggedUser(state, user) {
      state.loggedUser = user
    },
    clearLoggedUser(state) {
      state.loggedUser = null
    }
  },
  actions: {
  },
  getters: {
  }
})
