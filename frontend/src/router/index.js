import { createRouter, createWebHistory } from 'vue-router'; 
import Login from '../components/Login.vue';
import main from '../components/MainPage.vue';
import MainPage from '../components/MainPage.vue';


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Login
    },
    {
      path: '/main',
      name: 'main',
      component: MainPage
    }
  ]
});

export default router;