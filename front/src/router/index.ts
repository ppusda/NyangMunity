import { createRouter, createWebHistory } from "vue-router";
import MainView from "@/views/MainView.vue";
import BoardListView from "@/views/board/List.vue";
import LoginView from "@/views/member/Login.vue";
import JoinView from "@/views/member/Join.vue";
import UserView from "@/views/member/User.vue";

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
      path: "/member/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/member/join",
      name: "join",
      component: JoinView,
    },
    {
      path: "/member/info",
      name: "info",
      component: UserView,
    },
  ],
});

export default router;
