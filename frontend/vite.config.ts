import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5178,
    proxy: {
      // 将以 /api 开头的请求代理到后端 Spring Boot（本地开发用）
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        secure: false,
        // 保持路径不变，如果需要重写可以使用 rewrite
        // rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
});
