import Login from '../components/Login.vue';
import MainPage from '../components/ViewAllExchangeDays/MainPage.vue';
import EventPlanning from '../components/createNewEvents/EventPlanning.vue';
import HomePage from '@/components/HomePage.vue';
import GiveFeedback from '@/components/feedback/GiveFeedback.vue'

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
        component: MainPage,
      },
      {
        path: '/events/planning',
        name: 'eventPlanning',
        component: EventPlanning
      },
      {
        path: '/events/:eventId/attendance',
        name: 'feedback',
        component: GiveFeedback,
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
];