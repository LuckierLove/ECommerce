<template>
  <div class="page">
    <div class="page-title">我的优惠券</div>
    <el-form :inline="true" :model="query" class="toolbar">
      <el-form-item label="编码">
        <el-input v-model="query.code" placeholder="优惠券编码" />
      </el-form-item>
      <el-form-item label="排序">
        <el-select v-model="query.sortBy" placeholder="字段">
          <el-option label="创建时间" value="createdAt" />
          <el-option label="优惠金额" value="discountAmount" />
        </el-select>
      </el-form-item>
      <el-form-item label="顺序">
        <el-select v-model="query.sortOrder">
          <el-option label="降序" value="desc" />
          <el-option label="升序" value="asc" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="coupons" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="discountAmount" label="优惠金额" width="120" />
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && coupons.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchCoupons">刷新</el-button>
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
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth";
import { ElMessage } from "element-plus";
import { listCoupons } from "../../api/coupons";
import type { Coupon } from "../../types/models";

const coupons = ref<Coupon[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const query = reactive({
  page: 1,
  size: 10,
  code: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:user-coupons";

const fetchCoupons = async () => {
  loading.value = true;
  try {
    const res = await listCoupons({
      page: query.page,
      size: query.size,
      code: query.code || undefined,
      sortBy: query.sortBy,
      sortOrder: query.sortOrder
    });
    coupons.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取优惠券失败，请重试";
  } finally {
    loading.value = false;
  }
};

const loadQuery = () => {
  const raw = localStorage.getItem(storageKey);
  if (!raw) return;
  try {
    const saved = JSON.parse(raw) as typeof query;
    Object.assign(query, saved);
  } catch {
    localStorage.removeItem(storageKey);
  }
};

const persistQuery = () => {
  localStorage.setItem(storageKey, JSON.stringify(query));
};

const router = useRouter();
const authStore = useAuthStore();

onMounted(() => {
  if (!authStore.token) {
    router.push("/login");
    return;
  }
  loadQuery();
  fetchCoupons();
});

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchCoupons();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.code = "";
  fetchCoupons();
};

const handlePage = (page: number) => {
  query.page = page;
  persistQuery();
  fetchCoupons();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  persistQuery();
  fetchCoupons();
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
