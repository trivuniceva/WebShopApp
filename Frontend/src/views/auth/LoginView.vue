<template>
  <div class="login-container">
    <h2>Login Page</h2>
    <form @submit.prevent="handleLogin" class="login-form">
      <input
          type="text"
          v-model="username"
          placeholder="Username:"
          required
          class="rounded-input"
      />
      <input
          type="password"
          v-model="password"
          placeholder="Password:"
          required
          class="rounded-input"
      />
      <button type="submit" class="rounded-button">Login</button>
    </form>

    <p>
      Don’t have an account?
      <strong><router-link to="/signup" style="color: #F86B86;">Sign up</router-link></strong>
    </p>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { loginUser } from '@/api/auth'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()
const store = useStore()

const handleLogin = async () => {
  try {
    const user = await loginUser({ username: username.value, password: password.value })
    console.log("Login success:", user) // proveri da li ima id
    store.commit('setLoggedUser', user)
    localStorage.setItem('loggedUser', JSON.stringify(user))
    router.push('/profile')
  } catch (err) {
    errorMessage.value = 'Pogrešan username ili lozinka'
  }
}
</script>


<style scoped>
.container {
  display: flex;
}

.left,
.right {
  flex: 1;
  padding: 34px;
}

.login-container {
  position: relative;
  height: 380px;
  width: 424px;
  text-align: center;
  padding-top: 80px;
  border-radius: 15px;
  margin: 8px auto;
  overflow: hidden;
}

.login-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  backdrop-filter: blur(5px);
  background-color: rgba(44, 62, 80, 0.05);
  z-index: 1;
}

.login-form,
.login-container > h2,
.login-container > p {
  position: relative;
  z-index: 2;

}

.login-form {
  max-width: 300px;
  margin: 0 auto;
}

.rounded-input {
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 8px;
  margin: 8px 0;
  width: 100%;
}

.rounded-button {
  background-color: #f86b86;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
  margin-top: 12px;
  width: 40%;
  font-size: 16px;
}

.rounded-button:hover {
  background-color: #f8afb4;
  color: white;
}

p,
h2 {
  color: white;
}

p {
  padding-top: 24px;
}
</style>
