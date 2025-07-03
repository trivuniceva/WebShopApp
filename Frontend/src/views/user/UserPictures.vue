<template>
  <div class="user-pictures">
    <div v-if="images.length > 0" class="image-grid">
      <div v-for="image in images" :key="image.id" class="image-item">
        <img :src="image.path" :alt="image.text" @click="openImagePopup(image)">
      </div>
    </div>
    <div v-else>
      <p>No pictures to display.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';
import axios from 'axios';

const props = defineProps({
  userId: {
    type: String,
    required: true,
  },
});

// Dodajemo novi emit za brisanje slike
const emit = defineEmits(['open-image-popup', 'image-deleted']);

const images = ref([]);

const fetchImages = async () => {
  if (!props.userId) {
    console.log('No user ID provided to fetch images.');
    images.value = [];
    return;
  }
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/images/user/${props.userId}`);
    images.value = response.data;
    console.log(`Fetched ${images.value.length} images for user ID: ${props.userId}`);
  } catch (error) {
    console.error('Error fetching images:', error);
    images.value = [];
  }
};

watch(() => props.userId, fetchImages, { immediate: true });

// Emitujemo događaj kada se klikne na sliku
const openImagePopup = (image) => {
  emit('open-image-popup', image);
};

// Nova funkcija za uklanjanje slike iz liste kada je obrisana
const removeImage = (imageId) => {
  images.value = images.value.filter(image => image.id !== imageId);
};

// Izloži funkciju roditeljskoj komponenti
defineExpose({
  removeImage
});
</script>

<style scoped>
/* Vaša postojeća stilizacija je dobra, nema potrebe da je menjamo */
.user-pictures {
  width: 80%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin: 0 auto;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3px;
  padding: 20px;
  justify-items: center;
  overflow-y: auto;
}

.image-item {
  width: 100%;
  height: 433px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  position: relative;
}

.image-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0);
  transition: background-color 0.3s ease;
  pointer-events: none;
  border-radius: 8px;
}

.image-item:hover::before {
  background-color: rgba(0, 0, 0, 0.3);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>