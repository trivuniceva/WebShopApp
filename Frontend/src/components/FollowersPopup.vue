<template>
  <div class="popup-overlay" @click.self="$emit('close')">
    <div class="popup-window">
      <button class="close-popup-button" @click="$emit('close')">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-x">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
      <h3 class="popup-title">Friends <3</h3>
      <div class="follower-list-container">
        <ul v-if="followersWithDetails.length">
          <li v-for="follower in followersWithDetails" :key="follower.id" class="follower-item" @click.prevent="openProfile(follower.id)">
            <span class="follower-name">{{ follower.firstName }} {{ follower.lastName }}</span>
            <button class="remove-follower-button" @click.stop="$emit('remove', follower.id)">Remove</button>
          </li>
        </ul>
        <p v-else-if="isLoading" class="loading-message">Loading friends...</p>
        <p v-else class="no-followers-message">No friends yet.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  followers: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['remove', 'close'])

const router = useRouter()
const followersWithDetails = ref([])
const isLoading = ref(true)

const fetchFollowerDetails = async (followerId) => {
  try {
    const response = await fetch(`http://localhost:8080/WebShopAppREST/rest/users/${followerId}`)
    if (response.ok) {
      return await response.json()
    } else {
      console.error(`Failed to fetch details for user ${followerId}: HTTP ${response.status}`)
      return null
    }
  } catch (error) {
    console.error(`Network error while fetching details for user ${followerId}:`, error)
    return null
  }
}

watch(() => props.followers, async (newFollowers) => {
  isLoading.value = true
  if (newFollowers && newFollowers.length > 0) {
    const promises = newFollowers.map(id => fetchFollowerDetails(id))
    const results = await Promise.all(promises)
    followersWithDetails.value = results.filter(user => user !== null)
  } else {
    followersWithDetails.value = []
  }
  isLoading.value = false
}, { immediate: true })

onMounted(async () => {})

function openProfile(followerId) {
  emit('close')
  router.push({ name: 'UserProfile', params: { id: followerId } })
}
</script>

<style scoped>
/* CSS remains unchanged */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
}

.popup-window {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 30px;
  width: 90%;
  max-width: 450px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
  color: #2c3e50;
  display: flex;
  flex-direction: column;
  animation: slideIn 0.3s ease-out;
}

.close-popup-button {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.close-popup-button svg {
  color: #888;
  width: 20px;
  height: 20px;
}

.close-popup-button:hover {
  background-color: #f0f0f0;
  transform: rotate(90deg);
}

.popup-title {
  text-align: center;
  font-size: 1.8em;
  color: #2c3e50;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 2px solid #2c3e50;
  font-weight: 700;
}

.follower-list-container {
  flex-grow: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.follower-list-container ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.follower-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f7f9fc;
  padding: 15px 20px;
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.follower-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.08);
}

.follower-name {
  font-size: 1.1em;
  color: #333;
  font-weight: 500;
  cursor: pointer;
}

.remove-follower-button {
  background-color: #2c3e50;
  color: white;
  border: none;
  padding: 8px 18px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9em;
  font-weight: 600;
  transition: background-color 0.2s ease, transform 0.2s ease;
  flex-shrink: 0;
}

.remove-follower-button:hover {
  background-color: #2c3e50;
  transform: translateY(-1px);
}

.no-followers-message {
  text-align: center;
  color: #777;
  font-size: 1.1em;
  padding: 20px;
}

.follower-list-container::-webkit-scrollbar {
  width: 8px;
}

.follower-list-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.follower-list-container::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 10px;
}

.follower-list-container::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@media (max-width: 768px) {
  .popup-window {
    padding: 20px;
    max-width: 95%;
  }

  .popup-title {
    font-size: 1.6em;
    margin-bottom: 20px;
  }

  .follower-item {
    padding: 12px 15px;
  }

  .follower-name {
    font-size: 1em;
  }

  .remove-follower-button {
    padding: 7px 15px;
    font-size: 0.85em;
  }
}

@media (max-width: 480px) {
  .popup-window {
    padding: 15px;
  }

  .popup-title {
    font-size: 1.4em;
    margin-bottom: 15px;
  }

  .follower-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .remove-follower-button {
    width: 100%;
    margin-top: 5px;
  }
}
</style>
