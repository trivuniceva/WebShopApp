<template>
  <div class="user-posts-container">
    <div v-if="postsWithImages.length > 0" class="post-list">
      <div
          v-for="post in postsWithImages"
          :key="post.id"
          class="post-card"
          @click="openPostPopup(post)" >
        <img
            v-if="isValidImagePath(post.imagePath)"
            :src="getFullImageUrl(post.imagePath)" class="post-image"
            alt="Post image"
        />
        <div class="post-content">
          <p class="post-text">{{ post.text }}</p>
          <p class="post-date">{{ formatDate(post.creationDate) }}</p>
          <button @click.stop="confirmDeletePost(post.id)" class="delete-post-btn">Delete Post</button>
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
            :src="getFullImageUrl(currentPost.imagePath)" :alt="currentPost.text"
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

const getFullImageUrl = (imagePath) => {
  if (!imagePath) return '';
  const backendBaseUrl = 'http://localhost:8080';
  return `${backendBaseUrl}${imagePath}`;
}

const postsWithImages = computed(() =>
    posts.value
)

const confirmDeletePost = async (postId) => {
  if (confirm('Are you sure you want to delete this post? This will also delete all associated comments.')) {
    try {
      await axios.delete(`http://localhost:8080/WebShopAppREST/rest/posts/${postId}`);
      alert('Post deleted successfully!');
      fetchPosts();
    } catch (error) {
      console.error('Error deleting post:', error);
      alert('Failed to delete post. Please try again.');
    }
  }
};
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
  background-color: #ffffff;
  padding: 30px;
  border-radius: 18px;
  width: 65%;
  max-width: 700px;
  max-height: 85%;
  overflow-y: auto;
  text-align: center;
  position: relative;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  color: #2c3e50;
}

.popup-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.popup-text {
  font-size: 1.25rem;
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
  top: 15px;
  right: 15px;
  background-color: #f86b86;
  border: none;
  color: white;
  border-radius: 50%;
  width: 34px;
  height: 34px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.close-btn:hover {
  background-color: #e05a73;
  transform: translateY(-1px);
}

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
