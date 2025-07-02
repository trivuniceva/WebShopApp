<template>
    <div>
        <h1>Login</h1>
        <form class="login-component" @submit.prevent="login">
            <div>
                <label for="username">Username:</label>
                <input type="text" id="username" v-model="username" required>
            </div>
            <div>
                <label for="password">Password:</label>
                <input type="password" id="password" v-model="password" required>
            </div>
            <button class="submit" type="submit">Login</button>

            <br>

            <p id="success">{{ success }}</p>
            <p id="error">{{ errorMessage }}</p>
        </form>
    </div>
</template>

<script setup>
import axios from 'axios';
import { ref } from 'vue';


const username = ref('');
const password = ref('');

const errorMessage = ref('');
const success = ref('');

const login = () => {
    axios.post('http://localhost:8080/WebShopAppREST/rest/login', { username: username.value, password: password.value })
        .then(response => {
            success.value = 'Korisnik je uspesno prijavljen.';
            errorMessage.value = '';
        })
        .catch(error => {
            success.value = '';
            errorMessage.value = 'Pogresno korisnicko ime ili lozinka.';
            console.error(error);
        });
}

</script>

<style scoped>
.login-component {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin: 0 auto;
    width: 400px;
}

.login-component label {
    font-weight: bold;
    display: block;
}

.login-component input {
    padding: 0.5rem;
    border: 1px solid #797979;
    border-radius: 0.25rem;
    background-color: #555555;
    color: white;
    display: block;
    width: 100%;
}

.login-component>.submit {
    padding: 0.5rem;
    border: none;
    border-radius: 0.25rem;
    background-color: #007bff;
    color: white;
    cursor: pointer;
}

.login-component>.submit:hover {
    background-color: #0056b3;
}

p {
    display: block;
}

/* Add your component-specific styles here */
</style>