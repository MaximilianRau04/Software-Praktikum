<template>
    <div class="avatar" :style="avatarStyles">
      {{ initials }}
    </div>
  </template>
  
  <script>
  export default {
    props: {
      username: {
        type: String,
        required: true
      }
    },
    computed: {
      initials() {
        const parts = this.username.split(/[_\-\s]/);
        return parts
          .slice(0, 2)
          .map(part => part.charAt(0).toUpperCase())
          .join('');
      },
      avatarStyles() {
        const hash = this.username.split('').reduce((acc, char) => char.charCodeAt(0) + acc, 0);
        const hue = hash % 360;
        return {
          backgroundColor: `hsl(${hue}, 65%, 65%)`,
          color: 'white'
        };
      }
    }
  };
  </script>
  
  <style scoped>
  .avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 0.9rem;
  }
  </style>