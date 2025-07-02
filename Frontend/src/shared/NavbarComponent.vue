<template>
  <div>
    <nav class="navbar">
      <router-link to="/">Home</router-link>
      <router-link to="/search">Search</router-link>

      <template v-if="!loggedUser">
        <router-link to="/login">Login</router-link>
      </template>

      <template v-else>
        <div class="dropdown" @click.stop="toggleDropdown('profile')">
          <span class="dropdown-trigger">Profile ▾</span>
          <div v-if="openDropdown === 'profile'" class="dropdown-menu" @click.stop>
            <router-link to="/profile" @click="closeDropdown">View Profile</router-link>
            <router-link to="/edit-profile" @click="closeDropdown">Edit Profile</router-link>
          </div>
        </div>

        <div class="dropdown" @click.stop="toggleDropdown('content')">
          <span class="dropdown-trigger">Content ▾</span>
          <div v-if="openDropdown === 'content'" class="dropdown-menu" @click.stop>
            <router-link to="/add-image" @click="closeDropdown">Add Image</router-link>
            <router-link to="/add-post" @click="closeDropdown">Add Post</router-link>
          </div>
        </div>

        <a href="#" @click.prevent="logout">Logout</a>
      </template>
    </nav>

    <BackgroundVideo v-if="showVideo" />
  </div>
</template>

<script>
import { ref } from 'vue'
import { mapState } from 'vuex'
import BackgroundVideo from "@/views/BackgroundVideo.vue"

export default {
  name: "NavbarComponent",
  components: {
    BackgroundVideo
  },
  setup() {
    const openDropdown = ref(null)

    const toggleDropdown = (menu) => {
      openDropdown.value = openDropdown.value === menu ? null : menu
    }

    const closeDropdown = () => {
      openDropdown.value = null
    }

    // zatvaranje dropdowna kada klikneš van navbar-a
    const handleClickOutside = (event) => {
      if (!event.target.closest('.navbar')) {
        closeDropdown()
      }
    }

    window.addEventListener('click', handleClickOutside)

    // Obrati pažnju da bi bilo dobro ovo ukloniti na unmount komponenti (ako koristiš Composition API)
    // Ovde samo demo za jednostavnost

    return {
      openDropdown,
      toggleDropdown,
      closeDropdown,
    }
  },
  computed: {
    ...mapState(['loggedUser']),
    showVideo() {
      return ['/', '/login', '/signup'].includes(this.$route.path)
    }
  },
  methods: {
    logout() {
      this.$store.commit('clearLoggedUser')
      localStorage.removeItem('loggedUser')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  gap: 20px;
  padding: 20px;
  justify-content: center;
  font-weight: 500;
  font-size: 1rem;
}

a, .dropdown-trigger {
  cursor: pointer;
  color: #2c3e50;
  text-decoration: none;
  position: relative;
}

a:hover, .dropdown-trigger:hover {
  color: #F8AFB4;
  text-decoration: underline;
}

/* Dropdown style */
.dropdown {
  position: relative;
  user-select: none;
}

.dropdown-menu {
  position: absolute;
  top: 28px;
  left: 0;
  background-color: white;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  min-width: 150px;
  z-index: 10;
  padding: 8px 0;
  display: flex;
  flex-direction: column;
}

.dropdown-menu router-link {
  padding: 8px 16px;
  color: #2c3e50;
  text-decoration: none;
  white-space: nowrap;
}

.dropdown-menu router-link:hover {
  background-color: #f9f9f9;
  color: #f86b86;
}
</style>
