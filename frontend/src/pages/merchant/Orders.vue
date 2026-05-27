<template>
  <div class="page">
    <div class="page-title">商家订单</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="用户ID">
          <el-input v-model="query.userId" placeholder="用户ID" />
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="query.sortBy" placeholder="字段">
            <el-option label="创建时间" value="createdAt" />
            <el-option label="总金额" value="totalAmount" />
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
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增订单</el-button>
        <el-button @click="fetchOrders">刷新</el-button>
      </div>
    </div>
    <el-table :data="orders" v-loading="loading" border>
      <el-table-column prop="id" label="订单ID" width="200" />
      <el-table-column prop="userId" label="用户ID" width="180" />
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removeOrder(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && orders.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchOrders">刷新</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入订单总金额" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { createOrder, deleteOrder, listOrders, updateOrder } from "../../api/orders";
import type { Order } from "../../types/models";

const orders = ref<Order[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive({
  id: "",
  userId: "",
  totalAmount: ""
});
const dialogTitle = computed(() => (isEdit.value ? "编辑订单" : "新增订单"));
const rules = {
  userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  totalAmount: [
    { required: true, message: "请输入总金额", trigger: "blur" },
    { pattern: /^\d+(\.\d{1,2})?$/, message: "金额格式不正确", trigger: "blur" }
  ]
};
const query = reactive({
  page: 1,
  size: 10,
  userId: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:merchant-orders";

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await listOrders({
      page: query.page,
      size: query.size,
      userId: query.userId || undefined,
      sortBy: query.sortBy,
      sortOrder: query.sortOrder
    });
    orders.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取订单失败，请重试";
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
  fetchOrders();
});

const resetForm = () => {
  form.id = "";
  form.userId = "";
  form.totalAmount = "";
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: Order) => {
  isEdit.value = true;
  form.id = row.id;
  form.userId = row.userId;
  form.totalAmount = row.totalAmount;
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateOrder(form.id, { totalAmount: form.totalAmount });
      ElMessage.success("更新成功");
    } else {
      await createOrder({ userId: form.userId, totalAmount: form.totalAmount });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchOrders();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removeOrder = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该订单吗？", "提示", { type: "warning" });
    await deleteOrder(id);
    ElMessage.success("删除成功");
    fetchOrders();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchOrders();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.userId = "";
  query.sortBy = "createdAt";
  query.sortOrder = "desc";
  persistQuery();
  fetchOrders();
};

const handlePage = (page: number) => {
  query.page = page;
  persistQuery();
  fetchOrders();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  persistQuery();
  fetchOrders();
};
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.pager {
  margin-top: 12px;
  justify-content: flex-end;
}

.state {
  margin-top: 12px;
}
</style>
