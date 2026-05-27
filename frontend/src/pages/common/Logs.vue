<template>
  <div class="page">
    <div class="page-title">日志</div>
    <el-form :inline="true" :model="query" class="toolbar">
      <el-form-item label="用户ID">
        <el-input v-model="query.userId" placeholder="输入用户ID" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.logType" placeholder="全部">
          <el-option label="全部" :value="undefined" />
          <el-option label="登录" :value="1" />
          <el-option label="操作" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="logs" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="logType" label="类型" width="120" />
      <el-table-column prop="message" label="内容" />
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && logs.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchLogs">刷新</el-button>
    </el-empty>
    <el-pagination
      class="pager"
      :current-page="query.page"
      :page-size="query.size"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @current-change="handlePage"
      @size-change="handleSize"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { listLogs } from "../../api/logs";
import type { LogRecord } from "../../types/models";

const logs = ref<LogRecord[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const query = reactive({
  page: 1,
  size: 10,
  userId: "",
  logType: undefined as number | undefined
});

const fetchLogs = async () => {
  loading.value = true;
  try {
    const res = await listLogs({
      page: query.page,
      size: query.size,
      userId: query.userId || undefined,
      logType: query.logType
    });
    logs.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取日志失败，请重试";
  } finally {
    loading.value = false;
  }
};

onMounted(fetchLogs);

const handleSearch = () => {
  query.page = 1;
  fetchLogs();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.userId = "";
  query.logType = undefined;
  fetchLogs();
};

const handlePage = (page: number) => {
  query.page = page;
  fetchLogs();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  fetchLogs();
};
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}

.pager {
  margin-top: 12px;
  justify-content: flex-end;
}

.state {
  margin-top: 12px;
}
</style>
