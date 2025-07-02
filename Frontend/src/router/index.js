import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '@/views/Login.vue'
import WebShop from '@/views/WebShop.vue'
import SearchUsersView from "@/views/SearchUsersView.vue";
import LoginView from "@/views/auth/LoginView.vue";
import SignupView from "@/views/auth/SignupView.vue";
import UserProfile from "@/views/user/UserProfile.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/signup', name: 'signup', component: SignupView },
    { path: '/profile', component: UserProfile },
    { path: '/search', component:SearchUsersView },

  ]
})

export default router
