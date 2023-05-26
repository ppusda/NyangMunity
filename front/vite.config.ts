import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";

const path = require('path');

// module.exports = {
//   outputDir: path.resolve(__dirname, '../src/main/resources/static'),
// } 후에 한 번에 배포가 가능하다면 시도해 볼 예정

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), vueJsx()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    proxy: {
      "/nm/": {
        target: "http://localhost:8080",
        rewrite: (path) => path.replace(/^\/nm/, ""),
      },
      "/tc/": {
        target: "https://api.tenor.com",
        rewrite: (path) => path.replace(/^\/tc/, ""),
      },
    },
  },
});
