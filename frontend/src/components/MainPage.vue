<template>
  <Header isHeaderContentStart>
    <template v-slot:headernav>
      <HeaderNav>
        <HeaderNavItem>
          <RouterLink to="/">login</RouterLink>
        </HeaderNavItem>
        <HeaderNavItem>
          <RouterLink to="/main">main</RouterLink>
        </HeaderNavItem>
      </HeaderNav>
    </template>
  </Header>
  <div class="container">
    <div class="leftSide" v-if="isVisible">
      <Upcoming :isVisible="isVisible" @toggle-visibility="toggleVisibility" />
    </div>
    <div class="leftSide" v-else="isVisible">
      <Detail :isVisible="isVisible" @toggle-visibility="toggleVisibility" />
    </div>
    <div class="rightSide">
      <ScrollableDivs :items="workshops" />
    </div>
  </div>
</template>

<script>
import Upcoming from './Upcoming.vue';
import Detail from './Detail.vue';
import ScrollableDivs from './Scrollable.vue';

export default {
  name: 'MainPage',
  components: {
    Upcoming,
    Detail,
    ScrollableDivs
  },
  data() {
    return {
      isVisible: true,
      workshops: []
    };
  },
  methods: {
    toggleVisibility() {
      this.isVisible = !this.isVisible;
    }
  }
};
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  background-color: #01172F;
}

#app {
  height: 100%;
}

.header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px;
  background-color: #f6f3f3;
}

.header-nav {
  display: flex;
  gap: 10px;
}

.nav-button {
  display: inline-block;
  background-color: #0288d1;
  color: white;
  border: none;
  padding: 10px 20px;
  margin: 5px;
  cursor: pointer;
  font-size: 16px;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.nav-button:hover {
  background-color: #0277bd;
}

.container {
  display: flex;
  height: calc(100% - 50px); /* Adjust based on header height */
}

.leftSide, .rightSide {
  flex: 1;
  padding: 20px;
}

.leftSide {
  background-color: #f0f0f0;
}

.rightSide {
  background-color: #e0e0e0;
  overflow-y: auto;
}
</style>
