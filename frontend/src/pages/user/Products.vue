<template>
  <div class="page">
    <div class="page-title">商品列表</div>
    <el-form :inline="true" :model="query" class="toolbar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="商品名称" />
      </el-form-item>
      <el-form-item label="价格">
        <el-input v-model="query.minPrice" placeholder="最低价" />
      </el-form-item>
      <el-form-item label="-">
        <el-input v-model="query.maxPrice" placeholder="最高价" />
      </el-form-item>
      <el-form-item label="排序">
        <el-select v-model="query.sortBy" placeholder="字段">
          <el-option label="创建时间" value="createdAt" />
          <el-option label="价格" value="price" />
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
    <el-table :data="products" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="price" label="价格" width="120" />
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && products.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchProducts">刷新</el-button>
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
import { listProducts } from "../../api/products";
import type { Product } from "../../types/models";

const products = ref<Product[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const query = reactive({
  page: 1,
  size: 10,
  keyword: "",
  minPrice: "",
  maxPrice: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:user-products";

const fetchProducts = async () => {
  loading.value = true;
  try {
    const res = await listProducts({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      minPrice: query.minPrice ? Number(query.minPrice) : undefined,
      maxPrice: query.maxPrice ? Number(query.maxPrice) : undefined,
      sortBy: query.sortBy,
      sortOrder: query.sortOrder
    });
    products.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取商品失败，请重试";
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

onMounted(() => {
  loadQuery();
  fetchProducts();
});

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchProducts();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.keyword = "";
  query.minPrice = "";
  query.maxPrice = "";
  query.sortBy = "createdAt";
  query.sortOrder = "desc";
  persistQuery();
  fetchProducts();
};

const handlePage = (page: number) => {
  query.page = page;
  persistQuery();
  fetchProducts();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  persistQuery();
  fetchProducts();
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
