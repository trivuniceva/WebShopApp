<template>
  <div v-if="isVisible" class="image-popup-overlay" @click.self="closePopup">
    <div class="image-popup-content">

      <div class="image-container">
        <img :src="image.path" :alt="image.text">
      </div>

      <div class="info-and-comments-container">
        <p class="close-popup-btn" @click="closePopup">X</p>

        <button v-if="canDeleteImage" @click="deleteImage" class="delete-image-btn">
          Delete Image
        </button>

        <div class="post-header">
          <div class="user-info">
            <img :src="uploaderProfilePic" :alt="image.uploader" class="user-avatar">
            <span class="username">{{ image.uploader || 'Anonymous User' }}</span>
          </div>
          <p class="image-upload-date">{{ formatUploadDate(image.uploadDate) }}</p>
        </div>

        <div class="image-description">
          <p class="image-text">{{ image.text }}</p>
        </div>

        <div class="comments-section">
          <div v-if="comments.length > 0" class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <img :src="getCommenterProfilePic(comment.userId)" alt="User Avatar" class="comment-avatar">
                <span class="comment-username">{{ comment.username }}:</span>
                <button
                    v-if="canDeleteComment(comment)"
                    @click="deleteComment(comment.id)"
                    class="delete-comment-btn"
                >
                  Delete
                </button>
              </div>
              <span class="comment-text">{{ comment.text }}</span>
            </div>
          </div>
          <div v-else class="no-comments">
            <p>No comments yet.</p>
          </div>
        </div>

        <div class="add-comment-section">
          <div v-if="loggedUser">
            <input type="text" placeholder="Add a comment..." class="comment-input" v-model="newCommentText" @keyup.enter="postComment">
            <button class="post-comment-btn" @click="postComment">Post</button>
          </div>
          <div v-else>
            <p class="login-prompt">Log in to leave a comment.</p>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch, computed } from 'vue';
import { useStore } from 'vuex';
import axios from 'axios';

const props = defineProps({
  isVisible: {
    type: Boolean,
    required: true,
  },
  image: {
    type: Object,
    required: true,
  },
});

// Emitujemo događaj za zatvaranje pop-upa i za brisanje slike
const emit = defineEmits(['close', 'image-deleted']);
const store = useStore();

const comments = ref([]);
const newCommentText = ref('');
const uploaderProfilePic = ref('');
const commenterProfilePics = ref({});

const loggedUser = computed(() => store.state.loggedUser);

// Nova computed property za proveru da li korisnik može obrisati sliku
const canDeleteImage = computed(() => {
  return loggedUser.value && props.image.userId === loggedUser.value.id;
});

const closePopup = () => {
  emit('close');
};

const fetchCommentsAndUserData = async (objectId) => {
  if (!objectId) {
    comments.value = [];
    return;
  }
  try {
    const commentsResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/object/${objectId}`);
    comments.value = commentsResponse.data;

    const uniqueUserIds = [...new Set(comments.value.map(c => c.userId))];
    for (const userId of uniqueUserIds) {
      if (!commenterProfilePics.value[userId]) {
        const userResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`);
        commenterProfilePics.value[userId] = userResponse.data.profilePicturePath;
      }
    }

    if (props.image.userId && props.image.userId !== 'anonymous') {
      const uploaderResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${props.image.userId}`);
      uploaderProfilePic.value = uploaderResponse.data.profilePicturePath;
    } else {
      uploaderProfilePic.value = '';
    }

  } catch (error) {
    console.error('Greška pri učitavanju podataka:', error);
    comments.value = [];
    uploaderProfilePic.value = '';
    commenterProfilePics.value = {};
  }
};

const postComment = async () => {
  if (newCommentText.value.trim() === '') {
    return;
  }

  if (!loggedUser.value) {
    console.error('Korisnik nije ulogovan. Ne mogu objaviti komentar.');
    return;
  }

  const newComment = {
    objectId: props.image.id,
    objectType: 'IMAGE',
    userId: loggedUser.value.id,
    text: newCommentText.value,
    commentDate: new Date().toISOString(),
    editDate: null,
    logicallyDeleted: false,
  };

  try {
    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/comments/add', newComment);

    const addedComment = response.data;
    comments.value.push({
      ...addedComment,
      username: loggedUser.value.username
    });

    if (!commenterProfilePics.value[addedComment.userId]) {
      const userResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${addedComment.userId}`);
      commenterProfilePics.value[addedComment.userId] = userResponse.data.profilePicturePath;
    }

    newCommentText.value = '';

  } catch (error) {
    console.error('Greška pri dodavanju komentara:', error);
  }
};

const canDeleteComment = (comment) => {
  if (!loggedUser.value) {
    return false;
  }
  const isAuthor = comment.userId === loggedUser.value.id;
  const isPostOwner = props.image.userId === loggedUser.value.id;

  return isAuthor || isPostOwner;
};

const deleteComment = async (commentId) => {
  if (!confirm('Are you sure you want to delete this comment?')) {
    return;
  }
  try {
    await axios.delete(`http://localhost:8080/WebShopAppREST/rest/comments/${commentId}`);

    comments.value = comments.value.filter(comment => comment.id !== commentId);

    console.log(`Comment with ID ${commentId} has been deleted.`);
  } catch (error) {
    console.error(`Greška pri brisanju komentara sa ID ${commentId}:`, error);
  }
};

// Nova funkcija za brisanje slike
const deleteImage = async () => {
  if (!confirm('Are you sure you want to delete this image? All comments will be deleted as well.')) {
    return;
  }
  try {
    await axios.delete(`http://localhost:8080/WebShopAppREST/rest/images/${props.image.id}`);

    // Emituj događaj roditeljskoj komponenti da je slika obrisana
    emit('image-deleted', props.image.id);

    // Zatvori pop-up
    closePopup();

    console.log(`Image with ID ${props.image.id} and its comments have been deleted.`);
  } catch (error) {
    console.error(`Greška pri brisanju slike sa ID ${props.image.id}:`, error);
  }
};

const getCommenterProfilePic = (userId) => {
  return commenterProfilePics.value[userId] || '';
};

watch(() => props.image?.id, (newId) => {
  if (props.isVisible && newId) {
    fetchCommentsAndUserData(newId);
  }
}, { immediate: true });

watch(() => props.isVisible, (newVal) => {
  if (newVal && props.image?.id) {
    fetchCommentsAndUserData(props.image.id);
  } else if (!newVal) {
    comments.value = [];
    uploaderProfilePic.value = '';
    commenterProfilePics.value = {};
  }
});

const formatUploadDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};
</script>

<style scoped>
/* Vaši postojeći stilovi ostaju isti */
.image-popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(5px);
}

.image-popup-content {
  background-color: #ffffff;
  border-radius: 20px;
  max-width: 90%;
  width: 1200px;
  height: 95vh;
  display: flex;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  color: #2c3e50;
}

.image-container {
  flex: 2;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: #f0f0f0;
}

.image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.info-and-comments-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 25px;
  position: relative;
}

.close-popup-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: background-color 0.2s ease;
  z-index: 1;
  font-size: 1.2em;
}

.close-popup-btn:hover {
  color: #f86b86;
}

.delete-image-btn {
  position: absolute;
  top: 15px;
  right: 60px; /* Pomeren desno da ne smeta X-u */
  background-color: #dc3545; /* Crvena boja */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  z-index: 1;
}

.delete-image-btn:hover {
  background-color: #c82333;
}


.post-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f86b86;
}

.username {
  font-weight: 700;
  font-size: 1.1em;
  color: #2c3e50;
}

.image-upload-date {
  font-size: 0.9em;
  color: #888;
  margin: 0;
  text-align: left;
}

.image-description {
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 20px;
}

.image-text {
  font-size: 1em;
  line-height: 1.5;
  color: #444;
  margin: 0;
  text-align: left;
}

.comments-section {
  flex-grow: 1;
  overflow-y: auto;
  padding-right: 15px;
  margin-bottom: 15px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-item {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 12px 15px;
  text-align: left;
  line-height: 1.4;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.comment-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e0e0e0;
}

.comment-username {
  font-weight: 600;
  color: #2c3e50;
  margin-right: 5px;
}

.comment-text {
  color: #555;
  padding-left: 36px;
}

.no-comments {
  text-align: center;
  color: #888;
  padding: 20px;
}

.add-comment-section {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.comment-input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 1em;
  transition: border-color 0.3s ease;
}

.comment-input:focus {
  outline: none;
  border-color: #f86b86;
  box-shadow: 0 0 0 3px rgba(248, 107, 134, 0.2);
}

.post-comment-btn {
  background-color: #f86b86;
  color: white;
  border: none;
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.post-comment-btn:hover {
  background-color: #e05a73;
  transform: translateY(-1px);
}

.login-prompt {
  width: 100%;
  text-align: center;
  color: #888;
  font-style: italic;
  margin: 10px 0;
}

.comments-section::-webkit-scrollbar {
  width: 8px;
}

.comments-section::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.comments-section::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 10px;
}

.comments-section::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

.delete-comment-btn {
  background: none;
  border: none;
  color: #dc3545;
  font-size: 0.85em;
  font-weight: 600;
  margin-left: auto;
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s ease;
  padding: 5px 10px;
  border-radius: 5px;
}

.delete-comment-btn:hover {
  opacity: 1;
  background-color: rgba(220, 53, 69, 0.1);
}

@media (max-width: 992px) {
  .image-popup-content {
    flex-direction: column;
    height: 95vh;
    max-width: 95%;
  }

  .image-container {
    flex: none;
    height: 50vh;
    padding: 15px;
  }

  .info-and-comments-container {
    flex: 1;
    padding: 20px;
  }
}

@media (max-width: 576px) {
  .image-popup-content {
    border-radius: 10px;
    height: 90vh;
    width: 95%;
  }

  .image-container {
    height: 40vh;
  }

  .info-and-comments-container {
    padding: 15px;
  }
}
</style>

