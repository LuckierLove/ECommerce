import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    role: localStorage.getItem("role") || ""
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
      localStorage.setItem("token", token);
    },
    setRole(role: string) {
      this.role = role;
      localStorage.setItem("role", role);
    },
    clearToken() {
      this.token = "";
      this.role = "";
      localStorage.removeItem("token");
      localStorage.removeItem("role");
    }
  }
});
