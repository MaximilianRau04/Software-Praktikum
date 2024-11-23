import Login from '../components/Login.vue';
import MainPage from '../components/ViewAllExchangeDays/MainPage.vue';
import Create from '../components/CreateNewEvents/Create.vue';
import HomePage from '@/components/HomePage.vue';

export default [
  {
    name: "Master",
    path: "/",
    redirect: '/login',
    components: {   
      default: HomePage,
      main: MainPage
    },
    children: [
      {
        name: 'home',
        path: '/home',
        component: HomePage,
      },
    ]
  },
  {
    path: '/login',
    name: 'login',
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
];