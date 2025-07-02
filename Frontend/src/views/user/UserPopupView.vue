<template>
  <div class="user-search">
    <h2>User search</h2>

    <!-- Search form -->
    <div class="search-options">
      <input v-model="searchName" placeholder="Name:" class="rounded-input" />
      <input v-model="searchLastName" placeholder="Lastname:" class="rounded-input" />
      <label for="startDate">Date of birth from:</label>
      <input type="date" id="startDate" v-model="startDate" class="rounded-input" />
      <label for="endDate">Date of birth to:</label>
      <input type="date" id="endDate" v-model="endDate" class="rounded-input" />
      <button @click="searchUsers" class="rounded-button">Search</button>
    </div>

    <!-- Results -->
    <div class="search-results" v-if="filteredUsers.length">
      <h3>Search results</h3>
      <select v-model="sortOption" class="rounded-input">
        <option value="firstName">Name</option>
        <option value="lastName">Lastname</option>
        <option value="dateOfBirth">Date of Birth</option>
      </select>

      <ul>
        <li v-for="user in sortedUsers" :key="user.id" @click="openUserDetails(user)">
          {{ user.firstName }} {{ user.lastName }} - {{ user.dateOfBirth }}
        </li>
      </ul>
    </div>

    <!-- User details modal -->
    <UserDetailsModal
        v-if="selectedUser"
        :user="selectedUser"
        :current-user="loggedUser"
        @close="selectedUser = null"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import UserDetailsModal from "@/components/UserDetailsModal.vue";

const store = useStore()
const loggedUser = computed(() => store.state.loggedUser)

const users = ref([])
const filteredUsers = ref([])
const searchName = ref('')
const searchLastName = ref('')
const startDate = ref('')
const endDate = ref('')
const sortOption = ref('firstName')
const selectedUser = ref(null)

const fetchUsers = async () => {
  const response = await fetch('http://172.20.10.4:8080/WebShopAppREST/rest/users')
  if (!response.ok) {
    console.error('Ne mogu da učitam korisnike')
    return
  }
  users.value = await response.json()
  filteredUsers.value = [...users.value]
}

const searchUsers = () => {
  filteredUsers.value = users.value.filter(user => {
    const matchName = user.firstName.toLowerCase().includes(searchName.value.toLowerCase())
    const matchLastName = user.lastName.toLowerCase().includes(searchLastName.value.toLowerCase())

    const dob = new Date(user.dateOfBirth)
    const start = startDate.value ? new Date(startDate.value) : null
    const end = endDate.value ? new Date(endDate.value) : null

    const matchDate = (!start || dob >= start) && (!end || dob <= end)
    return matchName && matchLastName && matchDate
  })
}

const sortedUsers = computed(() => {
  return [...filteredUsers.value].sort((a, b) => {
    if (sortOption.value === 'firstName') {
      return a.firstName.localeCompare(b.firstName)
    } else if (sortOption.value === 'lastName') {
      return a.lastName.localeCompare(b.lastName)
    } else if (sortOption.value === 'dateOfBirth') {
      return new Date(a.dateOfBirth) - new Date(b.dateOfBirth)
    }
    return 0
  })
})

const openUserDetails = (user) => {
  selectedUser.value = user
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
li {
  cursor: pointer;
  transition: background 0.2s;
}
li:hover {
  background-color: #e0e0e0;
}
</style>
