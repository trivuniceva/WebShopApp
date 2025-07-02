<template>
  <div class="edit-profile-container">
    <h2>Edit Profile</h2>

    <form v-if="user" class="edit-profile-form" @submit.prevent="saveChanges">
      <div class="form-columns">
        <div class="form-column left-column">
          <label for="email" class="form-label">Email:</label>
          <input
              type="email"
              id="email"
              v-model="user.email"
              placeholder="Email:"
              required
              class="rounded-input"
          />

          <label for="firstName" class="form-label">First Name:</label>
          <input
              type="text"
              id="firstName"
              v-model="user.firstName"
              placeholder="First Name:"
              required
              class="rounded-input"
          />

          <label for="lastName" class="form-label">Last Name:</label>
          <input
              type="text"
              id="lastName"
              v-model="user.lastName"
              placeholder="Last Name:"
              required
              class="rounded-input"
          />

          <label for="dateOfBirth" class="form-label">Date of Birth:</label>
          <input
              type="date"
              id="dateOfBirth"
              v-model="user.dateOfBirth"
              class="rounded-input"
          />

          <label for="gender" class="form-label">Gender:</label>
          <select id="gender" v-model="user.gender" class="rounded-input">
            <option disabled value="">Select gender</option>
            <option value="female">Female</option>
            <option value="male">Male</option>
            <option value="other">Other</option>
          </select>

          <div class="form-row">
            <label for="privateProfile" class="form-label checkbox-label">Private Account:</label>
            <input
                type="checkbox"
                id="privateProfile"
                v-model="user.privateAccount"
            />
          </div>
        </div>

        <div class="form-column right-column">
          <label for="newPassword" class="form-label">New Password:</label>
          <input
              type="password"
              id="newPassword"
              v-model="newPassword"
              placeholder="New Password:"
              class="rounded-input"
          />

          <label for="confirmPassword" class="form-label">Confirm New Password:</label>
          <input
              type="password"
              id="confirmPassword"
              v-model="confirmPassword"
              placeholder="Confirm New Password:"
              class="rounded-input"
          />
          <p v-if="passwordError" class="error-message">{{ passwordError }}</p>

          <label for="profilePicUpload" class="form-label upload-label">Profile Picture:</label>
          <input
              type="file"
              id="profilePicUpload"
              accept="image/*"
              @change="handleImageUpload"
          />
          <label for="profilePicUpload" class="upload-button">Choose Photo</label>

          <img v-if="previewImage" :src="previewImage" alt="Preview" class="preview-img" />
          <div v-else class="preview-img-placeholder">
            <span>No Image</span>
          </div>

        </div>
      </div>

      <button type="submit" class="rounded-button">Save Changes</button>
    </form>

    <p v-else>Loading...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import User from '@/models/User'

const user = ref(null)
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const previewImage = ref('')

onMounted(() => {
  const userStr = localStorage.getItem('loggedUser')
  if (userStr) {
    user.value = new User(JSON.parse(userStr))
    previewImage.value = user.value.profilePicturePath || ''
  }
})

function handleImageUpload(event) {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = () => {
      previewImage.value = reader.result
      user.value.profilePicturePath = reader.result
    }
    reader.readAsDataURL(file)
  }
}

async function saveChanges() {
  passwordError.value = ''

  if (newPassword.value || confirmPassword.value) {
    if (newPassword.value !== confirmPassword.value) {
      passwordError.value = 'Passwords do not match.'
      return
    }
    if (newPassword.value.length < 6) {
      passwordError.value = 'Password must be at least 6 characters long.';
      return;
    }
    user.value.password = newPassword.value
  }

  try {
    const response = await fetch(`http://localhost:8080/WebShopAppREST/rest/users/${user.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(user.value)
    })

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to update user');
    }

    const updatedUser = await response.json()
    localStorage.setItem('loggedUser', JSON.stringify(updatedUser))
    alert('Changes saved successfully!')
    newPassword.value = '';
    confirmPassword.value = '';
    passwordError.value = '';

  } catch (error) {
    console.error('Update failed:', error)
    alert(`Something went wrong during update: ${error.message || 'Unknown error'}.`)
  }
}
</script>

<style scoped>
.edit-profile-container {
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

.edit-profile-form {
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
  align-items: stretch;
  gap: 15px;
}

.rounded-input,
select {
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
select:focus {
  border-color: #f86b86;
  outline: none;
  box-shadow: 0 0 0 3px rgba(248, 107, 134, 0.2);
}

select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%232c3e50' d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px;
}

.form-label {
  text-align: left;
  font-size: 0.95em;
  color: #555;
  margin-bottom: -10px;
  display: block;
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 10px;
  margin-top: 5px;
  margin-bottom: 5px;
}

.checkbox-label {
  margin-bottom: 0;
  flex-grow: 1;
}

input[type='checkbox'] {
  width: 20px;
  height: 20px;
  accent-color: #f86b86;
  cursor: pointer;
  flex-shrink: 0;
}

.error-message {
  color: #e74c3c;
  font-size: 0.9em;
  margin-top: -10px;
  margin-bottom: 5px;
  text-align: center;
  width: 100%;
}

.rounded-button {
  background-color: #f86b86;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 25px;
  cursor: pointer;
  /*width: 50%;*/
  margin: 25px auto 0;
  font-size: 1.1em;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(248, 107, 134, 0.3);
}

.rounded-button:hover {
  background-color: #e05a73;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(248, 107, 134, 0.4);
}

input[type="file"] {
  width: 0.1px;
  height: 0.1px;
  opacity: 0;
  overflow: hidden;
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
  align-self: flex-start;
  margin-top: 10px;
  width: fit-content;
}

.upload-button:hover {
  background-color: #d6e4ec;
}

.preview-img {
  width: 165px;
  height: 165px;
  border-radius: 50%;
  margin-top: 15px;
  margin-bottom: 5px;
  object-fit: cover;
  border: 3px solid #f86b86;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  align-self: center;
}

.preview-img-placeholder {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  margin-top: 15px;
  margin-bottom: 5px;
  background-color: #e0e0e0;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #777;
  font-size: 1.1em;
  border: 3px dashed #bbb;
  align-self: center;
}

p {
  color: #555;
}

@media (max-width: 768px) {
  .form-columns {
    flex-direction: column;
    gap: 25px;
  }

  .form-column {
    min-width: unset;
    width: 100%;
  }

  .rounded-button {
    width: 80%;
  }
}
</style>