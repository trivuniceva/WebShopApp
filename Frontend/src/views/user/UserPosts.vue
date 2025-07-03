<template>
  <div class="user-posts-container">
    <div v-if="posts.length > 0" class="post-list">
      <div
          v-for="post in posts"
          :key="post.id"
          class="post-card"
          @click="openPostPopup(post)"
      >
        <img
            v-if="isValidImagePath(post.imagePath)"
            :src="getFullImageUrl(post.imagePath)"
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

    <PostPopup
        :isVisible="isPopupOpen"
        :post="currentPost"
        @close="closePopup"
        @post-deleted="handlePostDeleted"
    />
  </div>
</template>

<script setup>
import { ref, watch, defineProps } from 'vue'
import axios from 'axios'
import PostPopup from "@/components/PostPopup.vue";

const props = defineProps({
  userId: String,
})

const posts = ref([])
const isPopupOpen = ref(false)
const currentPost = ref(null)

const fetchPosts = async () => {
  if (!props.userId) {
    posts.value = []
    return
  }
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/posts/user/${props.userId}`)
    posts.value = response.data
  } catch (err) {
    posts.value = []
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
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const isValidImagePath = (path) => {
  return path && path.trim() !== '' && !path.endsWith('null') && !path.includes('undefined')
}

const getFullImageUrl = (imagePath) => {
  if (!imagePath) return ''
  const backendBaseUrl = 'http://localhost:8080'
  return `${backendBaseUrl}${imagePath}`
}

const handlePostDeleted = (deletedPostId) => {
  posts.value = posts.value.filter(post => post.id !== deletedPostId)
}
</script>

<style scoped>
.delete-post-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  margin-top: 10px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.2s ease;
  align-self: flex-start;
}

.delete-post-btn:hover {
  background-color: #c82333;
}

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
  background-color: #fcfcfc;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

.post-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 15px;
}

.post-content {
  text-align: left;
}

.post-text {
  font-size: 1.1rem;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.5;
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
</style>
