<template>
  <div class="page">
    <div class="page-title">权限管理</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="权限名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="query.sortBy" placeholder="字段">
            <el-option label="创建时间" value="createdAt" />
            <el-option label="名称" value="name" />
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
        <el-button type="primary" @click="openCreate">新增权限</el-button>
        <el-button @click="fetchPermissions">刷新</el-button>
      </div>
    </div>
    <el-table :data="permissions" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="name" label="权限名称" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removePermission(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && permissions.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchPermissions">刷新</el-button>
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
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
import { createPermission, deletePermission, listPermissions, updatePermission } from "../../api/permissions";
import type { Permission } from "../../types/models";

const permissions = ref<Permission[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive({
  id: "",
  name: ""
});
const dialogTitle = computed(() => (isEdit.value ? "编辑权限" : "新增权限"));
const rules = {
  name: [{ required: true, message: "请输入权限名称", trigger: "blur" }]
};
const query = reactive({
  page: 1,
  size: 10,
  keyword: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:admin-permissions";

const fetchPermissions = async () => {
  loading.value = true;
  try {
    const res = await listPermissions({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      sortBy: query.sortBy,
      sortOrder: query.sortOrder
    });
    permissions.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取权限失败，请重试";
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
  fetchPermissions();
});

const resetForm = () => {
  form.id = "";
  form.name = "";
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: Permission) => {
  isEdit.value = true;
  form.id = row.id;
  form.name = row.name;
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updatePermission(form.id, { name: form.name });
      ElMessage.success("更新成功");
    } else {
      await createPermission({ name: form.name });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchPermissions();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removePermission = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该权限吗？", "提示", { type: "warning" });
    await deletePermission(id);
    ElMessage.success("删除成功");
    fetchPermissions();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchPermissions();
};

const handleReset = () => {
  query.keyword = "";
  query.page = 1;
  query.size = 10;
  query.sortBy = "createdAt";
  query.sortOrder = "desc";
  persistQuery();
  fetchPermissions();
};

const handlePage = (page: number) => {
  query.page = page;
  persistQuery();
  fetchPermissions();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  persistQuery();
  fetchPermissions();
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
