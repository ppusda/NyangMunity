import { createRouter, createWebHistory } from "vue-router";
import MainView from "../views/MainView.vue";
import BoardListView from "../views/board/List.vue";
import LoginView from "../views/user/Login.vue";
import JoinView from "../views/user/Join.vue";
import UserView from "../views/user/User.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "main",
      component: MainView,
    },
    {
      path: "/boards",
      name: "boards",
      component: BoardListView,
    },
    {
      path: "/user/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/user/join",
      name: "join",
      component: JoinView,
    },
    {
      path: "/user/info",
      name: "info",
      component: UserView,
    },
  ],
});

export default router;
