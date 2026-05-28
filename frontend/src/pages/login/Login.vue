<template>
  <div class="page login">
    <el-card class="login-card">
      <div class="page-title">
        登录
        <div class="login-theme-toggle">
          <div class="theme-switch" @click="toggleTheme" role="switch" :aria-checked="isDark">
            <div class="switch-track" :class="{ 'is-dark': isDark }">
              <span class="track-label label-light">浅</span>
              <div class="switch-thumb" :class="{ 'is-dark': isDark }"></div>
              <span class="track-label label-dark">深</span>
            </div>
          </div>
        </div>
      </div>
      <el-form :model="form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
            <el-option label="商家" value="merchant" />
          </el-select>
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { login } from "../../api/auth";
import { useAuthStore } from "../../stores/auth";
import { useThemeStore } from "../../stores/theme";

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: "",
  password: "",
  role: "user"
});
const loading = ref(false);
const themeStore = useThemeStore();
const isDark = computed({
  get: () => themeStore.dark,
  set: (v: boolean) => themeStore.setDark(v),
});

function toggleTheme() {
  themeStore.toggle();
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning("请输入用户名和密码");
    return;
  }
  loading.value = true;
  try {
    const res = await login({ username: form.username, password: form.password });
    const token = res.data.token;
    authStore.setToken(token);
    try {
      const parts = token.split(".");
      if (parts.length >= 2) {
        const payload = JSON.parse(atob(parts[1]));
        const roleFromToken = payload.role as string | undefined;
        authStore.setRole(roleFromToken || form.role);
      } else {
        authStore.setRole(form.role);
      }
    } catch (e) {
      authStore.setRole(form.role);
    }
    try {
      await authStore.loadCurrentUser();
    } catch {
      // If the user lookup fails, keep the login token and role so the session still works.
    }
    ElMessage.success("登录成功");
    // 根据角色跳转到对应首页，避免默认 / 重定向到 user 页面导致权限不匹配
    const role = authStore.role;
    if (role === "merchant") {
      router.push("/merchant/orders");
    } else if (role === "admin") {
      router.push("/admin/users");
    } else {
      router.push("/user/products");
    }
  } catch (error) {
    ElMessage.error("登录失败");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh !important;
  padding: 24px;
}

.login-card {
  width: min(420px, 100%);
  backdrop-filter: blur(10px);
}

.login-card :deep(.el-card__body) {
  padding: 30px;
}

.login-card :deep(.el-form) {
  display: grid;
  gap: 4px;
}

.login-card :deep(.el-form-item) {
  margin-bottom: 18px;
}

.login-card :deep(.el-button) {
  width: 100%;
  margin-top: 8px;
  border-radius: 999px;
  box-shadow: 0 14px 30px rgba(37, 99, 235, 0.22);
}

.login-theme-toggle {
  display: inline-block;
  float: right;
  margin-left: 12px;
}
</style>
