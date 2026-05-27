import { defineStore } from "pinia";
import { getMe } from "../api/users";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    role: localStorage.getItem("role") || "",
    userId: localStorage.getItem("userId") || ""
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
    setUserId(userId: string) {
      this.userId = userId;
      localStorage.setItem("userId", userId);
    },
    async loadCurrentUser() {
      if (!this.token) {
        return null;
      }
      const res = await getMe();
      const user = (res as any).data as { id: string };
      this.setUserId(user.id);
      return user;
    },
    clearToken() {
      this.token = "";
      this.role = "";
      this.userId = "";
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      localStorage.removeItem("userId");
    }
  }
});
