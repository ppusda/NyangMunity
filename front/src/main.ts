import {createApp} from "vue";
import {createPinia} from "pinia";
import Paginate from "vuejs-paginate-next";

import App from "./App.vue";
import router from "./router";
import store from "./stores/store";

import ElementPlus from "element-plus";

import "./assets/main.css";
import "element-plus/dist/index.css";
import 'vue3-toastify/dist/index.css';

import "@fortawesome/fontawesome-free/js/all.js";

const app = createApp(App);

app.use(createPinia());
app.use(store);
app.use(router);
app.use(ElementPlus);
app.use(Paginate);

app.component("vue-paginate", Paginate);

app.mount("#app");
