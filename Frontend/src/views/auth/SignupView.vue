<template>
  <div class="signup-container">
    <h2>Sign Up</h2>
    <form @submit.prevent="register" class="signup-form">
      <input type="text" v-model="username" placeholder="Username:" required class="rounded-input" />
      <input type="email" v-model="email" placeholder="Email:" required class="rounded-input" />
      <input type="text" v-model="firstName" placeholder="Name:" required class="rounded-input" />
      <input type="text" v-model="lastName" placeholder="Lastname:" required class="rounded-input" />

      <input type="date" v-model="dateOfBirth" placeholder="Date of Birth:" class="rounded-input" />

      <div class="gender-radio">
        <label for="female">Female</label>
        <input type="radio" id="female" value="Female" v-model="gender" required /> <label for="male">Male</label>
        <input type="radio" id="male" value="Male" v-model="gender" required /> </div>

      <input type="password" v-model="password" placeholder="Password:" required class="rounded-input" />
      <input type="password" v-model="confirmPassword" placeholder="Confirm password:" required class="rounded-input" />

      <button type="submit" class="rounded-button">Sign Up</button>
    </form>

    <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'; // Uvezi useRouter

export default {
  name: "SignupView",
  setup() { // Koristi setup za Composition API hooks
    const router = useRouter(); // Inicijalizuj router
    return { router }; // Vrati router da bude dostupan u templateu ili opcijama
  },
  data() {
    return {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      dateOfBirth: '', // Dodaj polje za datum rođenja
      gender: '',
      password: '',
      confirmPassword: '',
      successMessage: '',
      errorMessage: ''
    }
  },
  methods: {
    async register() {
      this.successMessage = ''; // Resetuj poruke
      this.errorMessage = '';

      if (this.password !== this.confirmPassword) {
        this.errorMessage = "Passwords do not match.";
        return;
      }

      const userData = {
        username: this.username,
        password: this.password,
        emailAddress: this.email,
        firstName: this.firstName,
        lastName: this.lastName,
        dateOfBirth: this.dateOfBirth, // Dodaj datum rođenja
        gender: this.gender,
        // Role i ostale podrazumevane vrednosti postavlja backend
      };

      try {
        const response = await fetch('http://localhost:8080/WebShopAppREST/rest/users/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(userData)
        });

        if (response.ok) { // Status 200-299 (uključujući 201 Created)
          const registeredUser = await response.json();
          this.successMessage = `Registration successful! Welcome, ${registeredUser.firstName}! You can now log in.`;
          // Opciono: Preusmeri korisnika na login stranicu nakon uspešne registracije
          setTimeout(() => {
            this.router.push('/login');
          }, 2000); // Preusmeri nakon 2 sekunde
        } else {
          // Ako je odgovor status 4xx ili 5xx, pročitaj telo odgovora za poruku
          const errorText = await response.text(); // Backend šalje poruku kao plain text
          this.errorMessage = errorText || 'Registration failed. Please try again.';
          console.error('Registration error:', response.status, errorText);
        }
      } catch (error) {
        console.error('Network or other error:', error);
        this.errorMessage = 'An unexpected error occurred. Please try again later.';
      }
    }
  }
}
</script>

<style scoped>
/* Svi tvoji postojeći stilovi */
.signup-container {
  position: relative;
  height: auto; /* Prilagodi visinu kontejnera da bude dinamična */
  min-height: 580px; /* Minimalna visina */
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

/* Stilovi za poruke o uspehu/grešci */
.success-message {
  color: #e6ffe6; /* Svetlo zelena */
  background-color: rgba(40, 167, 69, 0.2); /* Blaga zelena pozadina */
  padding: 10px;
  border-radius: 8px;
  margin-top: 20px;
  font-weight: bold;
  position: relative;
  z-index: 2;
}

.error-message {
  color: #ffcccc; /* Svetlo crvena */
  background-color: rgba(220, 53, 69, 0.2); /* Blaga crvena pozadina */
  padding: 10px;
  border-radius: 8px;
  margin-top: 20px;
  font-weight: bold;
  position: relative;
  z-index: 2;
}
</style>