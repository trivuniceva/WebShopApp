<template>
  <div class="user-search-container">
    <h2>Find Users</h2>

    <div class="search-filters">
      <div class="input-group">
        <label for="searchName" class="form-label">First Name:</label>
        <input v-model="searchName" id="searchName" placeholder="Enter first name" class="rounded-input small-input" />
      </div>

      <div class="input-group">
        <label for="searchLastName" class="form-label">Last Name:</label>
        <input v-model="searchLastName" id="searchLastName" placeholder="Enter last name" class="rounded-input small-input" />
      </div>

      <div class="input-group">
        <label for="startDate" class="form-label">Date of Birth From:</label>
        <input type="date" id="startDate" v-model="startDate" class="rounded-input small-input" />
      </div>

      <div class="input-group">
        <label for="endDate" class="form-label">Date of Birth To:</label>
        <input type="date" id="endDate" v-model="endDate" class="rounded-input small-input" />
      </div>

      <button @click="searchUsers" class="rounded-button search-button">Search</button>
    </div>

    <div class="search-results-section">
      <h3>Search Results</h3>

      <div v-if="filteredUsers.length" class="results-content">
        <div class="sort-options">
          <label for="sortOption" class="form-label">Sort by:</label>
          <select id="sortOption" v-model="sortOption" class="rounded-input sort-select">
            <option value="firstName">Name</option>
            <option value="lastName">Last Name</option>
            <option value="dateOfBirth">Date of Birth</option>
          </select>
        </div>

        <ul class="user-list">
          <li v-for="user in sortedUsers" :key="user.id" class="user-list-item">
            <div @click="navigateToUserProfile(user.id)" class="user-info-clickable">
              <span class="user-name">{{ user.firstName }} {{ user.lastName }}</span>
              <span class="user-dob">{{ user.dateOfBirth }}</span>
            </div>

            <button
                v-if="loggedUser && loggedUser.role === 'Administrator' && user.role !== 'Administrator'"
                @click.stop="toggleBlockStatus(user)"
                :class="['action-button', { 'block-button-style': !user.blocked, 'unblock-button-style': user.blocked }]"
            >
              {{ user.blocked ? 'Unblock User' : 'Block User' }}
            </button>
          </li>
        </ul>
      </div>

      <p v-else-if="users.length && !filteredUsers.length" class="no-matching-users">No matching users found.</p>
      <p v-else class="initial-message">Enter criteria and click Search to find users.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const users = ref([])
const filteredUsers = ref([])

const searchName = ref('')
const searchLastName = ref('')
const startDate = ref('')
const endDate = ref('')
const sortOption = ref('firstName')

const loggedUser = computed(() => {
  const user = store.getters.getLoggedUser
  return user
})

const fetchUsers = async () => {
  try {
    const response = await fetch('http://localhost:8080/WebShopAppREST/rest/users')
    if (!response.ok) return
    const fetchedUsers = await response.json()
    users.value = fetchedUsers.map(user => ({
      ...user,
      blocked: user.blocked || false
    }))
    searchUsers()
  } catch (error) {
    console.error('Error fetching users:', error)
  }
}

const searchUsers = () => {
  filteredUsers.value = users.value.filter(user => {
    const matchName = searchName.value ? user.firstName.toLowerCase().includes(searchName.value.toLowerCase()) : true
    const matchLastName = searchLastName.value ? user.lastName.toLowerCase().includes(searchLastName.value.toLowerCase()) : true
    const userDobStr = user.dateOfBirth
    const startDobStr = startDate.value
    const endDobStr = endDate.value
    const matchDate = (!startDobStr || userDobStr >= startDobStr) && (!endDobStr || userDobStr <= endDobStr)
    return matchName && matchLastName && matchDate
  })
}

const sortedUsers = computed(() => {
  return [...filteredUsers.value].sort((a, b) => {
    if (sortOption.value === 'firstName') return a.firstName.localeCompare(b.firstName)
    if (sortOption.value === 'lastName') return a.lastName.localeCompare(b.lastName)
    if (sortOption.value === 'dateOfBirth') return a.dateOfBirth.localeCompare(b.dateOfBirth)
    return 0
  })
})

const navigateToUserProfile = (userId) => {
  router.push({ name: 'UserProfile', params: { id: userId } })
}

const toggleBlockStatus = async (user) => {
  const action = user.blocked ? 'Unblock' : 'Block'
  const confirmMessage = user.blocked ? `Are you sure you want to unblock ${user.firstName}?` : `Are you sure you want to block ${user.firstName}?`

  if (!confirm(confirmMessage)) return

  try {
    const endpoint = user.blocked ? `unblock` : `block`
    const response = await fetch(`http://localhost:8080/WebShopAppREST/rest/users/${user.id}/${endpoint}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`Failed to ${action.toLowerCase()} user: ${errorText}`)
    }

    user.blocked = !user.blocked
    await fetchUsers()
  } catch (error) {
    console.error(`Error ${action.toLowerCase()}ing user:`, error.message)
    alert(`Failed to ${action.toLowerCase()} user: ${error.message}`)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>


<style scoped>
.action-button {
  border: none;
  border-radius: 8px;
  padding: 8px 15px;
  cursor: pointer;
  font-size: 0.9em;
  margin-left: 15px;
  transition: background-color 0.3s ease, transform 0.2s ease;
  flex-shrink: 0;
}

.block-button-style {
  background-color: #dc3545;
  color: white;
}

.block-button-style:hover {
  background-color: #c82333;
  transform: translateY(-1px);
}

.unblock-button-style {
  background-color: #28a745;
  color: white;
}

.unblock-button-style:hover {
  background-color: #218838;
  transform: translateY(-1px);
}

.user-search-container {
  text-align: center;
  padding: 30px;
  max-width: 900px;
  margin: 10px auto;
  background-color: #ffffff;
  border-radius: 18px;
  box-shadow: 0 8px 25px rgba(44, 62, 80, 0.12);
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 1.8em;
  font-weight: 600;
}

h3 {
  color: #2c3e50;
  margin-top: 30px;
  margin-bottom: 20px;
  font-size: 1.6em;
  font-weight: 600;
}

.search-filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  align-items: end;
  justify-content: center;
}

.input-group {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.form-label {
  text-align: left;
  font-size: 0.95em;
  color: #555;
  margin-bottom: 5px;
  display: block;
}

.rounded-input,
.sort-select {
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 12px 15px;
  width: 100%;
  box-sizing: border-box;
  color: #555;
  background-color: #fcfcfc;
  font-size: 1em;
  transition: all 0.3s ease;
}

.rounded-input:focus,
.sort-select:focus {
  border-color: #f86b86;
  outline: none;
  box-shadow: 0 0 0 3px rgba(248, 107, 134, 0.2);
}

.small-input {
  max-width: 250px;
  margin: 0 auto;
}

.sort-select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%232c3e50' d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px;
}

.user-link {
  text-decoration: none;
  color: #2c3e50;
}

.user-link:hover {
  color: #F8AFB4;
}

.rounded-button {
  background-color: #f86b86;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 25px;
  cursor: pointer;
  font-size: 1.1em;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(248, 107, 134, 0.3);
  width: auto;
  justify-self: center;
  margin-top: 15px;
}

.rounded-button:hover {
  background-color: #e05a73;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(248, 107, 134, 0.4);
}

.search-button {
  grid-column: 1 / -1;
  max-width: 200px;
}

.search-results-section {
  background-color: #fcfcfc;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: 30px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  justify-content: flex-start;
}

.sort-options .form-label {
  margin-bottom: 0;
  flex-shrink: 0;
}

.user-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-list-item {
  background-color: #f5f8fb;
  padding: 12px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex; /* Makes user info and button align horizontally */
  justify-content: space-between; /* Pushes info to left, button to right */
  align-items: center; /* Vertically centers content */
}

.user-list-item:hover {
  background-color: #e6f0f7;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* New style to ensure user info is a flexible clickable area */
.user-info-clickable {
  flex-grow: 1; /* Allows it to take up available space */
  display: flex;
  justify-content: space-between;
  align-items: center;
  /* Optional: Add padding/margin if needed to separate from button slightly */
  padding-right: 15px; /* Adds space between info and button */
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1.05em;
}

.user-dob {
  color: #777;
  font-size: 0.9em;
}

.no-matching-users,
.initial-message {
  padding: 20px;
  font-size: 1.1em;
  color: #777;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 10px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .user-search-container {
    padding: 20px;
  }

  .search-filters {
    grid-template-columns: 1fr;
  }

  .search-button {
    max-width: 100%;
  }

  .sort-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .small-input {
    max-width: 100%;
  }

  /* Adjust user list item for smaller screens */
  .user-list-item {
    flex-direction: column; /* Stack info and button vertically */
    align-items: flex-start; /* Align content to the left */
    gap: 10px; /* Space between info and button when stacked */
  }

  .user-info-clickable {
    width: 100%; /* Take full width */
    padding-right: 0; /* Remove right padding when stacked */
  }

  .action-button { /* Use the general class for adjustments */
    width: 100%; /* Make button full width when stacked */
    margin-left: 0; /* Remove left margin when stacked */
    margin-top: 5px; /* Add slight top margin if needed */
  }
}

@media (max-width: 500px) {
  .user-search-container {
    margin: 5px auto;
    padding: 15px;
  }

  h2 {
    font-size: 1.6em;
  }

  h3 {
    font-size: 1.4em;
  }

  .rounded-input, .sort-select {
    padding: 10px 12px;
  }

  .rounded-button {
    padding: 10px 20px;
    font-size: 1em;
  }
}
</style>