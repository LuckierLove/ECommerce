<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="logo">ECommerce</div>
      <el-menu :default-active="activePath" router>
        <el-menu-item v-for="item in filteredMenu" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">电商信息管理后台</span>
        <div class="header-actions">
          <span v-if="authStore.role" class="role">{{ roleLabel }}</span>
          <el-button v-if="authStore.token" type="danger" plain @click="handleLogout">退出</el-button>
          <el-button v-else type="primary" plain @click="goLogin">登录</el-button>
        </div>
      </el-header>
      <el-main>
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const activePath = computed(() => route.path);

const menuItems = [
  { path: "/user/products", label: "用户-商品", roles: ["user"] },
  { path: "/user/cart", label: "用户-购物车", roles: ["user"] },
  { path: "/user/orders", label: "用户-订单", roles: ["user"] },
  { path: "/user/addresses", label: "用户-地址", roles: ["user"] },
  { path: "/user/coupons", label: "用户-优惠券", roles: ["user"] },
  { path: "/admin/users", label: "管理员-用户", roles: ["admin"] },
  { path: "/admin/merchants", label: "管理员-商家", roles: ["admin"] },
  { path: "/admin/roles", label: "管理员-角色", roles: ["admin"] },
  { path: "/admin/permissions", label: "管理员-权限", roles: ["admin"] },
  { path: "/merchant/products", label: "商家-商品", roles: ["merchant"] },
  { path: "/merchant/orders", label: "商家-订单", roles: ["merchant"] },
  { path: "/merchant/coupons", label: "商家-优惠券", roles: ["merchant"] },
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

const goLogin = () => {
  router.push("/login");
};

const handleLogout = () => {
  authStore.clearToken();
  router.push("/login");
};
</script>

<style scoped>
.logo {
  font-size: 18px;
  font-weight: 700;
  padding: 16px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role {
  font-size: 13px;
  color: #6b7280;
}

.title {
  font-weight: 600;
}
</style>
