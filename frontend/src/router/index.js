import { createRouter, createWebHistory } from 'vue-router'; 
import Login from '../components/Login.vue';
import main from '../components/MainPage.vue';
import MainPage from '../components/MainPage.vue';
import Create from '../components/Create.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Login
    },
    {
      path: '/home',
      name: 'main',
      component: MainPage
    },
    {
      path: '/events',
      name: 'create',
      component: Create
    }
  ]
});

export default router;