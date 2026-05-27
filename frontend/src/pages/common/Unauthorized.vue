<template>
  <div class="page unauthorized">
    <el-result icon="warning" title="无权限访问" sub-title="请切换到有权限的角色后再尝试。">
      <template #extra>
        <el-button type="primary" @click="goLogin">返回登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const goLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.unauthorized {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
