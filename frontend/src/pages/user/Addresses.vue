<template>
  <div class="page">
    <div class="page-title">地址管理</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="用户ID">
          <el-input v-model="query.userId" placeholder="用户ID" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="query.city" placeholder="城市" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="query.state" placeholder="省份" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增地址</el-button>
        <el-button @click="fetchAddresses">刷新</el-button>
      </div>
    </div>
    <el-table :data="addresses" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="street" label="街道" />
      <el-table-column prop="city" label="城市" width="120" />
      <el-table-column prop="state" label="省份" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removeAddress(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && addresses.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchAddresses">刷新</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="街道" prop="street">
          <el-input v-model="form.street" placeholder="请输入街道" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="省份" prop="state">
          <el-input v-model="form.state" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="邮编" prop="zipCode">
          <el-input v-model="form.zipCode" placeholder="请输入邮编" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-input v-model="form.country" placeholder="请输入国家" />
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
import { createAddress, deleteAddress, listAddresses, updateAddress } from "../../api/addresses";
import type { Address } from "../../types/models";

const addresses = ref<Address[]>([]);
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
  street: "",
  city: "",
  state: "",
  zipCode: "",
  country: ""
});
const dialogTitle = computed(() => (isEdit.value ? "编辑地址" : "新增地址"));
const rules = {
  userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  street: [{ required: true, message: "请输入街道", trigger: "blur" }],
  city: [{ required: true, message: "请输入城市", trigger: "blur" }],
  state: [{ required: true, message: "请输入省份", trigger: "blur" }],
  zipCode: [
    { required: true, message: "请输入邮编", trigger: "blur" },
    { pattern: /^\d{4,10}$/, message: "邮编格式不正确", trigger: "blur" }
  ],
  country: [{ required: true, message: "请输入国家", trigger: "blur" }]
};
const query = reactive({
  page: 1,
  size: 10,
  userId: "",
  city: "",
  state: ""
});

const fetchAddresses = async () => {
  loading.value = true;
  try {
    const res = await listAddresses({
      page: query.page,
      size: query.size,
      userId: query.userId || undefined,
      city: query.city || undefined,
      state: query.state || undefined
    });
    addresses.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取地址失败，请重试";
  } finally {
    loading.value = false;
  }
};

onMounted(fetchAddresses);

const resetForm = () => {
  form.id = "";
  form.userId = "";
  form.street = "";
  form.city = "";
  form.state = "";
  form.zipCode = "";
  form.country = "";
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: Address) => {
  isEdit.value = true;
  form.id = row.id;
  form.userId = row.userId;
  form.street = row.street;
  form.city = row.city;
  form.state = row.state;
  form.zipCode = row.zipCode;
  form.country = row.country;
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateAddress(form.id, {
        street: form.street,
        city: form.city,
        state: form.state,
        zipCode: form.zipCode,
        country: form.country
      });
      ElMessage.success("更新成功");
    } else {
      await createAddress({
        userId: form.userId,
        street: form.street,
        city: form.city,
        state: form.state,
        zipCode: form.zipCode,
        country: form.country
      });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchAddresses();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removeAddress = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该地址吗？", "提示", { type: "warning" });
    await deleteAddress(id);
    ElMessage.success("删除成功");
    fetchAddresses();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  fetchAddresses();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.userId = "";
  query.city = "";
  query.state = "";
  fetchAddresses();
};

const handlePage = (page: number) => {
  query.page = page;
  fetchAddresses();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  fetchAddresses();
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
