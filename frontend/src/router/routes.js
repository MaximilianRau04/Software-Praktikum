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
      },
      {
        path: "/events/planning",
        name: "eventPlanning",
        component: EventPlanning,
      },
      {
        path: "/events/:eventId/attendance",
        name: "feedback",
        component: GiveFeedback,
      },
      {
        path: "/events/registrations",
        name: "eventRegistrations",
        component: EventRegistrations,
      },
      {
        path: "/exchangeDays/:exchangeDayId/manage",
        name: "manageExchangeDay",
        component: ExchangeDayPage,
        props: true,
      },
      {
        path: "/events/:eventId",
        name: "EventPage",
        component: EventPage,
        props: true,
      },
      {
        path: "/feedback/:eventId",
        name: "feedbackSummary",
        component: FeedbackSummary,
      },
      {
        path: "/forum/:eventId",
        name: "forum",
        component: Forum,
        props: true,
      },
      {
        path: "/profile/:username",
        name: "Profile",
        component: Profile,
        props: true,
      },
      {
        path: "/leaderboard",
        name: "Leaderboard",
        component: Leaderboard,
      },
      {
        path: "/trainerProfile/:trainerId/manage",
        name: "TrainerProfileManage",
        component: TrainerProfileManage,
        props: true,
      },
    ],
  },
  {
    path: "/login",
    name: "login",
    component: Login,
  },
];
