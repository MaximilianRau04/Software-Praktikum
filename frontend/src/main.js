import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./assets/body.css";
import 'floating-vue/dist/style.css'
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import FloatingVue from 'floating-vue'
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas, faEye, faEyeSlash, faCircleQuestion } from "@fortawesome/free-solid-svg-icons";

library.add(faEye, faEyeSlash, faCircleQuestion)

createApp(App)
  .component("font-awesome-icon", FontAwesomeIcon)
  .use(router)
  .use(FloatingVue)
  .mount("#app");
