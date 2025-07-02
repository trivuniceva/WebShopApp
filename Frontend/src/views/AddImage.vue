<template>
  <div class="add-image-container">
    <h2>Upload Photo</h2>

    <form @submit.prevent="addImage" class="add-image-form">
      <div class="form-columns">
        <div class="form-column left-column">
          <label for="image-upload" class="form-label">Choose Photo:</label>
          <input
              type="file"
              id="image-upload"
              accept="image/*"
              @change="onFileChange"
              required
          />
          <label for="image-upload" class="upload-button">Choose File</label>

          <img
              v-if="previewImage"
              :src="previewImage"
              alt="Preview"
              class="preview-img"
          />
          <div v-else class="preview-img-placeholder">
            <span>No Image</span>
          </div>
        </div>

        <div class="form-column right-column">
          <label for="image-text" class="form-label">Description:</label>
          <textarea
              id="image-text"
              v-model="imageText"
              placeholder="Write a description for your photo..."
              rows="6"
              class="rounded-input"
              required
          ></textarea>

          <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        </div>
      </div>

      <button type="submit" class="rounded-button">Upload</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'

const store = useStore()
const loggedUser = store.state.loggedUser

const selectedFile = ref(null)
const imageText = ref('')
const previewImage = ref('')
const successMessage = ref('')
const errorMessage = ref('')

function onFileChange(event) {
  const file = event.target.files[0]
  selectedFile.value = file

  if (file) {
    const reader = new FileReader()
    reader.onload = () => {
      previewImage.value = reader.result
    }
    reader.readAsDataURL(file)
  }
}

async function addImage() {
  successMessage.value = ''
  errorMessage.value = ''

  if (!selectedFile.value) {
    errorMessage.value = 'Please select a file.'
    return
  }

  if (!loggedUser || !loggedUser.id) {
    errorMessage.value = 'You must be logged in.'
    return
  }

  const imagePath = `/images/user_uploads/${selectedFile.value.name}`

  const newImage = {
    userId: loggedUser.id,
    path: imagePath,
    text: imageText.value,
  }

  try {
    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/images/add', newImage)
    successMessage.value = 'Photo uploaded successfully!'
    imageText.value = ''
    selectedFile.value = null
    previewImage.value = ''
    document.getElementById('image-upload').value = null
  } catch (err) {
    errorMessage.value = 'Failed to upload. Please try again.'
  }
}
</script>

<style scoped>
.add-image-container {
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
  margin-bottom: 25px;
  font-size: 1.8em;
  font-weight: 600;
}

.add-image-form {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-columns {
  display: flex;
  gap: 40px;
  width: 100%;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.form-column {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-label {
  text-align: left;
  font-size: 0.95em;
  color: #555;
  margin-bottom: -8px;
}

.rounded-input,
textarea {
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 12px 15px;
  width: 100%;
  box-sizing: border-box;
  color: #555;
  background-color: #fcfcfc;
  font-size: 1em;
  transition: all 0.3s ease;
  font-family: inherit;
}

.rounded-input:focus,
textarea:focus {
  border-color: #f86b86;
  outline: none;
  box-shadow: 0 0 0 3px rgba(248, 107, 134, 0.2);
}

textarea {
  resize: vertical;
}

input[type="file"] {
  width: 0.1px;
  height: 0.1px;
  opacity: 0;
  position: absolute;
  z-index: -1;
}

.upload-button {
  background-color: #ecf0f1;
  color: #2c3e50;
  padding: 10px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95em;
  transition: background-color 0.3s ease;
  width: fit-content;
}

.upload-button:hover {
  background-color: #d6e4ec;
}

.preview-img {
  width: 100%;
  max-width: 300px;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
  border: 2px solid #f86b86;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  align-self: center;
}

.preview-img-placeholder {
  width: 100%;
  max-width: 300px;
  height: 200px;
  border-radius: 12px;
  background-color: #e0e0e0;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #777;
  font-size: 1.1em;
  border: 2px dashed #bbb;
  align-self: center;
}

.success-message {
  color: #28a745;
  font-weight: 600;
  font-size: 1em;
}

.error-message {
  color: #dc3545;
  font-weight: 600;
  font-size: 1em;
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
  margin-top: 10px;
  box-shadow: 0 4px 10px rgba(248, 107, 134, 0.3);
  transition: all 0.3s ease;
}

.rounded-button:hover {
  background-color: #e05a73;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(248, 107, 134, 0.4);
}

@media (max-width: 768px) {
  .form-columns {
    flex-direction: column;
    gap: 25px;
  }
}
</style>
