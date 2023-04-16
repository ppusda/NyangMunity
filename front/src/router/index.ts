import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import WriteView from "../views/board/WriteView.vue"
import BoardListView from "../views/board/BoardListView.vue"
import ReadView from "../views/board/ReadView.vue"
import EditView from "../views/board/EditView.vue"
import LoginView from "../views/user/LoginView.vue"
import JoinView from "../views/user/JoinView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/boards/write",
      name: "write",
      component: WriteView
    },
    {
      path: "/read/:postId",
      name: "read",
      component: ReadView,
      props: true
    },
    {
      path: "/edit/:postId",
      name: "edit",
      component: EditView,
      props: true
    },
    {
      path: "/boards",
      name: "boards",
      component: BoardListView
    },
    {
      path: "/user/login",
      name: "login",
      component: LoginView
    },
    {
      path: "/user/join",
      name: "join",
      component: JoinView
    },

    // {
    //   path: "/about",
    //   name: "about",
    //   // route level code-splitting
    //   // this generates a separate chunk (About.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import("../views/AboutView.vue"),
    // },
  ],
});

export default router;