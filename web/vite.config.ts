import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: "0.0.0.0",
    proxy: {
      "/api/v1": {
        target: "http://DESKTOP-CF5B18H.mshome.net:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
