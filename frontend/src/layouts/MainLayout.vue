<template>
  <el-container :class="['layout-shell', { 'dark-theme': theme.dark }]">
    <el-aside width="244px" class="sidebar-shell">
      <div class="logo">
        <div class="logo-badge">EC</div>
        <div>
          <div class="logo-name">ECommerce</div>
          <div class="logo-subtitle">电商信息管理后台</div>
        </div>
      </div>
      <el-menu class="sidebar-menu" :default-active="activePath" router>
        <el-menu-item v-for="item in filteredMenu" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="content-shell">
      <el-header class="header-shell">
        <span class="title">{{ pageTitle }}</span>
        <div class="header-actions">
          <span v-if="authStore.role" class="role">{{ roleLabel }}</span>
          <div class="theme-switch" @click="toggleTheme" role="switch" :aria-checked="theme.dark">
            <div class="switch-track" :class="{ 'is-dark': theme.dark }">
              <span class="track-label label-light">浅</span>
              <div class="switch-thumb" :class="{ 'is-dark': theme.dark }"></div>
              <span class="track-label label-dark">深</span>
            </div>
          </div>
          <el-button v-if="authStore.token" class="soft-button" @click="handleLogout">退出</el-button>
          <el-button v-else class="soft-button soft-button-primary" @click="goLogin">登录</el-button>
        </div>
      </el-header>
      <el-main class="main-shell">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { useThemeStore } from "../stores/theme";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const theme = useThemeStore();
const activePath = computed(() => route.path);

const menuItems = [
  { path: "/user/products", label: "商品", roles: ["user"] },
  { path: "/user/cart", label: "购物车", roles: ["user"] },
  { path: "/user/orders", label: "订单", roles: ["user"] },
  { path: "/user/addresses", label: "地址", roles: ["user"] },
  { path: "/user/coupons", label: "优惠券", roles: ["user"] },
  { path: "/admin/users", label: "用户", roles: ["admin"] },
  { path: "/admin/merchants", label: "商家", roles: ["admin"] },
  { path: "/admin/roles", label: "角色", roles: ["admin"] },
  { path: "/admin/permissions", label: "权限", roles: ["admin"] },
  { path: "/merchant/products", label: "商品", roles: ["merchant"] },
  { path: "/merchant/orders", label: "订单", roles: ["merchant"] },
  { path: "/merchant/coupons", label: "优惠券", roles: ["merchant"] },
  { path: "/logs", label: "日志", roles: ["admin"] }
];

const filteredMenu = computed(() =>
  menuItems.filter((item) => item.roles.includes(authStore.role))
);

const roleLabel = computed(() => {
  if (authStore.role === "admin") return "管理员";
  if (authStore.role === "merchant") return "商家";
  if (authStore.role === "user") return "普通用户";
  return "";
});

const pageTitle = computed(() => {
  const current = menuItems.find((item) => item.path === route.path);
  return current?.label || "控制台";
});

const goLogin = () => {
  router.push("/login");
};

const handleLogout = () => {
  authStore.clearToken();
  router.push("/login");
};

const toggleTheme = () => {
  theme.toggle();
};
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  padding: 18px;
  gap: 18px;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.16), transparent 28%),
    radial-gradient(circle at bottom right, rgba(14, 165, 233, 0.12), transparent 26%),
    #f4f7fb;
}

.sidebar-shell,
.header-shell,
.main-shell {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
}

.sidebar-shell {
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 6px 10px;
}

.logo-badge {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #111827, #3b82f6);
  color: #fff;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(59, 130, 246, 0.28);
}

.logo-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.logo-subtitle {
  margin-top: 2px;
  font-size: 12px;
  color: #64748b;
}

.sidebar-menu {
  border: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 6px 0;
  border-radius: 16px;
  height: 46px;
  line-height: 46px;
  color: #334155;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.14), rgba(14, 165, 233, 0.18));
  color: #0f172a;
  font-weight: 600;
}

.content-shell {
  gap: 18px;
}

.header-shell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role {
  font-size: 13px;
  color: #64748b;
  padding: 8px 12px;
  border-radius: 999px;
  background: #f8fafc;
}

.title {
  font-weight: 700;
  color: #0f172a;
}

.main-shell {
  padding: 24px;
}

.soft-button {
  border: none;
  border-radius: 999px;
  background: #f8fafc;
  color: #334155;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.soft-button-primary {
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  color: #fff;
}

/* Dark theme overrides scoped to this layout */
.dark-theme .layout-shell {
  background: radial-gradient(circle at top left, rgba(2,6,23,0.6), transparent 28%), radial-gradient(circle at bottom right, rgba(2,10,26,0.6), transparent 26%), #071122;
}
.dark-theme .sidebar-shell,
.dark-theme .header-shell,
.dark-theme .main-shell {
  background: rgba(6,10,18,0.6);
  box-shadow: 0 18px 50px rgba(2,6,23,0.6);
  color: #dbeafe;
}
.dark-theme .logo-badge {
  background: linear-gradient(135deg, #0f1724, #1e3a8a);
  box-shadow: 0 10px 24px rgba(2,6,23,0.6);
}
.dark-theme .logo-name { color: #e6eef8; }
.dark-theme .logo-subtitle { color: #9aa6b2; }
.dark-theme .sidebar-menu :deep(.el-menu-item) { color: #cbd5e1; }
.dark-theme .sidebar-menu :deep(.el-menu-item.is-active) { background: linear-gradient(135deg, rgba(37,99,235,0.12), rgba(6,182,212,0.08)); color: #e6eef8; }
.dark-theme .role { background: rgba(255,255,255,0.04); color: #cbd5e1; }
.dark-theme .title { color: #e6eef8; }
.dark-theme .soft-button { background: rgba(255,255,255,0.04); color: #e6eef8; }
.dark-theme .soft-button-primary { background: linear-gradient(135deg,#2563eb,#06b6d4); color: #fff; }

/* Theme switch styles */
.theme-switch {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}
.switch-track {
  width: 64px;
  height: 32px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.06);
  padding: 4px;
  display: flex;
  align-items: center;
  transition: background 0.22s ease;
  position: relative;
}
.switch-track.is-dark { background: linear-gradient(90deg,#374151,#0f1724); }
.switch-thumb {
  width: 24px;
  height: 24px;
  border-radius: 999px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(2,6,23,0.12);
  transform: translateX(0);
  transition: transform 0.22s cubic-bezier(.2,.9,.2,1), background 0.15s ease;
  z-index: 2;
}
.switch-thumb.is-dark { transform: translateX(32px); background: #0b1220; box-shadow: 0 6px 18px rgba(2,6,23,0.6); }
.track-label { position: absolute; top: 50%; transform: translateY(-50%); font-size: 12px; z-index: 1; pointer-events: none; }
.label-light { left: 10px; color: #0f172a; }
.label-dark { right: 10px; color: #64748b; }
.dark-theme .label-light { color: #9aa6b2; }
.dark-theme .label-dark { color: #e6eef8; }
</style>
