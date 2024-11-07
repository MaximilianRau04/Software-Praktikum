import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import main from '../components/MainPage.vue';

const routes = [
  { path: '/', component: Login },
  { path: '/main', component: main },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;