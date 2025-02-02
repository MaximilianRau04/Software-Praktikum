import EventRegistrations from "@/components/viewEvents/EventRegistrations.vue";
import Login from "../components/Login.vue";
import MainPage from "../components/viewExchangeDays/home/MainPage.vue";
import EventPlanning from "../components/adminPanel/AdminPanel.vue";
import HomePage from "@/components/HomePage.vue";
import GiveFeedback from "@/components/feedback/GiveFeedback.vue";
import FeedbackSummary from "@/components/feedback/FeedbackSummary.vue";
import Forum from "@/components/forum/Forum.vue";
import Profile from "@/components/profile/Profile.vue";
import Leaderboard from "@/components/leaderboard/Leaderboard.vue";
import EventPage from "@/components/viewEvents/EventPage.vue";
import TrainerProfileManage from "@/components/profile/TrainerProfileManage.vue";
import ExchangeDayPage from "@/components/viewExchangeDays/ExchangeDayPage.vue";
import Unauthorized from "@/components/Unauthorized.vue"

export default [
  {
    name: "Master",
    path: "/",
    redirect: "/login",
    components: {
      default: HomePage,
      main: MainPage,
    },
    children: [
      {
        name: "home",
        path: "/home",
        component: MainPage,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/events/planning",
        name: "eventPlanning",
        component: EventPlanning,
        meta: { roles: ['ROLE_ADMIN']}
      },
      {
        path: "/events/:eventId/attendance",
        name: "feedback",
        component: GiveFeedback,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/events/registrations",
        name: "eventRegistrations",
        component: EventRegistrations,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/exchangeDays/:exchangeDayId/manage",
        name: "manageExchangeDay",
        component: ExchangeDayPage,
        props: true,
        meta: { roles: ['ROLE_ADMIN']}
      },
      {
        path: "/events/:eventId",
        name: "EventPage",
        component: EventPage,
        props: true,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/feedback/:eventId",
        name: "feedbackSummary",
        component: FeedbackSummary,
        meta: { roles: ['ROLE_ADMIN']}
      },
      {
        path: "/forum/:eventId",
        name: "forum",
        component: Forum,
        props: true,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/profile/:username",
        name: "Profile",
        component: Profile,
        props: true,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/leaderboard",
        name: "Leaderboard",
        component: Leaderboard,
        meta: { roles: ['ROLE_USER'] }
      },
      {
        path: "/trainerProfile/:trainerId/manage",
        name: "TrainerProfileManage",
        component: TrainerProfileManage,
        props: true,
        meta: { roles: ['ROLE_ADMIN']}
      },
    ],
  },
  {
    path: "/login",
    name: "login",
    component: Login,
  },
  {
    path: "/unauthorized",
    name: "Unauthorized",
    component: Unauthorized,
  },
];
