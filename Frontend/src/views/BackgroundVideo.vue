<template>
  <div class="video-background">
    <video autoplay muted loop controlsList="nodownload">
      <source src="../assets/mainPageVideo.mp4" type="video/mp4">
    </video>
  </div>
</template>

<script>
export default {
  name: "BackgroundVideo"
}
</script>

<style scoped>
.video-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: -1;
}

.video-background video {
  min-width: 100%;
  min-height: 100%;
  width: auto;
  height: auto;
}
</style>