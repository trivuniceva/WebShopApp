<template>
  <div class="signup-container">
    <h2>Sign Up</h2>
    <form @submit.prevent="register" class="signup-form">
      <input type="text" v-model="username" placeholder="Username:" required class="rounded-input" />
      <input type="email" v-model="email" placeholder="Email:" required class="rounded-input" />
      <input type="text" v-model="firstName" placeholder="Name:" required class="rounded-input" />
      <input type="text" v-model="lastName" placeholder="Lastname:" required class="rounded-input" />

      <div class="gender-radio">
        <label for="female">Female</label>
        <input type="radio" id="female" value="female" v-model="gender" required />

        <label for="male">Male</label>
        <input type="radio" id="male" value="male" v-model="gender" required />
      </div>

      <input type="password" v-model="password" placeholder="Password:" required class="rounded-input" />
      <input type="password" v-model="confirmPassword" placeholder="Confirm password:" required class="rounded-input" />

      <button type="submit" class="rounded-button">Sign Up</button>
    </form>
  </div>
</template>

<script>
export default {
  name: "SignupView",
  data() {
    return {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      gender: '',
      password: '',
      confirmPassword: ''
    }
  },
  methods: {
    register() {
      console.log("Registracija poslana", this.username);
    }
  }
}
</script>

<style scoped>
.signup-container {
  position: relative;
  height: 580px;
  width: 424px;
  text-align: center;
  padding-top: 40px;
  border-radius: 15px;
  margin: 40px auto;
  overflow: hidden;
}

.signup-container::before {
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

.signup-form,
.signup-container > h2 {
  position: relative;
  z-index: 2;
}

.signup-form {
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

.gender-radio {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px 0;
  padding: 0 10px;
  font-weight: bold;
  color: white;
}

h2 {
  color: white;
}
</style>
