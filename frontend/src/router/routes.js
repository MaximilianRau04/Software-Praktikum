import EventRegistrations from "@/components/ViewAllExchangeDays/EventRegistrations.vue";
import Login from "../components/Login.vue";
import MainPage from "../components/ViewAllExchangeDays/MainPage.vue";
import EventPlanning from "../components/createNewEvents/EventPlanning.vue";
import HomePage from "@/components/HomePage.vue";
import GiveFeedback from "@/components/feedback/GiveFeedback.vue";
import FeedbackSummary from "@/components/feedback/FeedbackSummary.vue";
import Forum from "@/components/forum/Forum.vue";
import UserProfile from "@/components/navigation/UserProfile.vue";
import UpdateEvent from "@/components/ViewAllExchangeDays/UpdateEvent.vue";

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
        path: "/user/:username",
        name: "UserProfile",
        component: UserProfile,
        props: true,
      },
      {
        path: "/events/:eventId/update",
        name: "updateEvent",
        component: UpdateEvent,
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
