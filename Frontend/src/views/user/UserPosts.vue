<template>
  <div class="user-posts-container">
    <div v-if="postsWithImages.length > 0" class="post-list">
      <div
          v-for="post in postsWithImages"
          :key="post.id"
          class="post-card"
          @click="openPostPopup(post)"
      >
        <img
            v-if="isValidImagePath(post.imagePath)"
            :src="post.imagePath"
            class="post-image"
            alt="Post image"
        />

        <div class="post-content">
          <p class="post-text">{{ post.text }}</p>
          <p class="post-date">{{ formatDate(post.creationDate) }}</p>
        </div>
      </div>
    </div>
    <div v-else class="no-posts-message">
      <p>No posts to display.</p>
    </div>

    <div
        v-if="isPopupOpen"
        class="post-popup-overlay"
        @click.self="closePopup"
    >
      <div class="post-popup-content">
        <button class="close-btn" @click="closePopup">X</button>
        <img
            v-if="isValidImagePath(currentPost?.imagePath)"
            :src="currentPost.imagePath"
            :alt="currentPost.text"
            class="popup-image"
        />
        <p class="popup-text">{{ currentPost.text }}</p>
        <p class="popup-date">
          Posted: {{ formatDate(currentPost.creationDate) }}
        </p>
        <p class="popup-comments">
          Comments: {{ currentPost.commentIds?.length || 0 }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  userId: String,
})

const posts = ref([])
const isPopupOpen = ref(false)
const currentPost = ref(null)

const fetchPosts = async () => {
  if (!props.userId) return
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/posts/user/${props.userId}`)
    posts.value = response.data
  } catch (err) {
    console.error('Greška prilikom dohvatanja postova:', err)
  }
}

watch(() => props.userId, fetchPosts, { immediate: true })

const openPostPopup = (post) => {
  currentPost.value = post
  isPopupOpen.value = true
}

const closePopup = () => {
  isPopupOpen.value = false
  currentPost.value = null
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-GB', {
    year: 'numeric', month: 'long', day: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

const isValidImagePath = (path) => {
  return path && path.trim() !== '' && !path.endsWith('null') && !path.includes('undefined');
}

const postsWithImages = computed(() =>
    posts.value
)
</script>

<style scoped>
.user-posts-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 20px;
  box-sizing: border-box;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  background-color: #fcfcfc; /* Vrlo blaga, skoro bela pozadina */
  border: 1px solid #eee; /* Suptilan border */
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* Nešto jača, ali i dalje suptilna senka */
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-3px); /* Malo se podigne */
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1); /* Senka postane izraženija */
}

.post-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 15px; /* Povećan razmak ispod slike */
}

.post-content {
  text-align: left;
}

.post-text {
  font-size: 1.1rem;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.5; /* Poboljšana čitljivost teksta */
}

.post-date {
  font-size: 0.85rem;
  color: #777;
}

.no-posts-message {
  padding: 20px;
  font-size: 1.1em;
  color: #777;
  text-align: center;
}

.post-popup-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.post-popup-content {
  background-color: #ffffff; /* Bela pozadina popupa */
  padding: 30px; /* Povećan padding */
  border-radius: 18px; /* Veći border-radius */
  width: 65%; /* Malo širi popup */
  max-width: 700px; /* Maksimalna širina */
  max-height: 85%; /* Malo veća visina */
  overflow-y: auto;
  text-align: center;
  position: relative;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25); /* Jača senka */
  color: #2c3e50;
}

.popup-image {
  max-width: 100%;
  max-height: 500px; /* Ograniči visinu slike u popupu */
  object-fit: contain; /* Sliku smesti unutar okvira */
  border-radius: 12px;
  margin-bottom: 20px; /* Povećan razmak */
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.popup-text {
  font-size: 1.25rem; /* Malo veći font */
  margin-bottom: 10px;
  font-weight: 500;
  line-height: 1.6;
}

.popup-date {
  color: #777;
  font-size: 0.95rem;
  margin-bottom: 15px;
}

.popup-comments {
  color: #2c3e50;
  margin-top: 10px;
  font-size: 1rem;
  font-weight: 500;
}

.close-btn {
  position: absolute;
  top: 15px; /* Povećan razmak */
  right: 15px; /* Povećan razmak */
  background-color: #f86b86;
  border: none;
  color: white;
  border-radius: 50%;
  width: 34px; /* Veće dugme */
  height: 34px; /* Veće dugme */
  font-size: 1.1rem;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.close-btn:hover {
  background-color: #e05a73;
  transform: translateY(-1px);
}

/* Media Queries */
@media (max-width: 768px) {
  .post-card {
    padding: 12px;
  }

  .post-text {
    font-size: 1rem;
  }

  .post-popup-content {
    width: 90%;
    padding: 20px;
  }

  .popup-text {
    font-size: 1.1rem;
  }

  .close-btn {
    width: 30px;
    height: 30px;
    font-size: 1rem;
  }
}
</style>