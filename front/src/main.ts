import { createApp } from "vue";
import { createPinia } from "pinia";
import VueCookies from "vue3-cookies";
import Paginate from "vuejs-paginate-next";

import App from "./App.vue";
import router from "./router";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

import "./assets/main.css";
import "@fortawesome/fontawesome-free/js/all.js";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(ElementPlus);
app.use(VueCookies);
app.use(Paginate);

app.component("vue-paginate", Paginate);

app.mount("#app");
