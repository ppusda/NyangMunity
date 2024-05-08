import { createRouter, createWebHistory } from "vue-router";
import MainView from "../views/MainView.vue";
import WriteView from "../views/board/WriteView.vue";
import BoardListView from "../views/board/BoardListView.vue";
import ReadView from "../views/board/ReadView.vue";
import EditView from "../views/board/EditView.vue";
import LoginView from "../views/user/LoginView.vue";
import JoinView from "../views/user/JoinView.vue";
import UserView from "../views/user/UserView.vue";
import MemeView from "../views/meme/MemeMainView.vue";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "main",
      component: MainView,
    },
    {
      path: "/boards/write",
      name: "write",
      component: WriteView,
    },
    {
      path: "/read/:postId",
      name: "read",
      component: ReadView,
      props: true,
    },
    {
      path: "/edit/:postId",
      name: "edit",
      component: EditView,
      props: true,
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
    {
      path: "/meme",
      name: "meme",
      component: MemeView,
    },
  ],
});

export default router;
