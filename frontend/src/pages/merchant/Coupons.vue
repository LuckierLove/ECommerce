<template>
  <div class="page">
    <div class="page-title">商家优惠券</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
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
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增优惠券</el-button>
        <el-button @click="fetchCoupons">刷新</el-button>
      </div>
    </div>
    <el-table :data="coupons" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="discountAmount" label="优惠金额" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removeCoupon(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入优惠券编码" />
        </el-form-item>
        <el-form-item label="金额" prop="discountAmount">
          <el-input v-model="form.discountAmount" placeholder="请输入优惠金额" />
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
import { createCoupon, deleteCoupon, listCoupons, updateCoupon } from "../../api/coupons";
import type { Coupon } from "../../types/models";

const coupons = ref<Coupon[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive({
  id: "",
  code: "",
  discountAmount: ""
});
const dialogTitle = computed(() => (isEdit.value ? "编辑优惠券" : "新增优惠券"));
const rules = {
  code: [{ required: true, message: "请输入优惠券编码", trigger: "blur" }],
  discountAmount: [
    { required: true, message: "请输入优惠金额", trigger: "blur" },
    { pattern: /^\d+(\.\d{1,2})?$/, message: "金额格式不正确", trigger: "blur" }
  ]
};
const query = reactive({
  page: 1,
  size: 10,
  code: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:merchant-coupons";

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

onMounted(() => {
  loadQuery();
  fetchCoupons();
});

const resetForm = () => {
  form.id = "";
  form.code = "";
  form.discountAmount = "";
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: Coupon) => {
  isEdit.value = true;
  form.id = row.id;
  form.code = row.code;
  form.discountAmount = row.discountAmount;
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateCoupon(form.id, { code: form.code, discountAmount: form.discountAmount });
      ElMessage.success("更新成功");
    } else {
      await createCoupon({ code: form.code, discountAmount: form.discountAmount });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchCoupons();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removeCoupon = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该优惠券吗？", "提示", { type: "warning" });
    await deleteCoupon(id);
    ElMessage.success("删除成功");
    fetchCoupons();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchCoupons();
};

const handleReset = () => {
  query.code = "";
  query.page = 1;
  query.size = 10;
  query.sortBy = "createdAt";
  query.sortOrder = "desc";
  persistQuery();
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
