<template>
  <div class="page">
    <div class="page-title">用户管理</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="用户名/邮箱" />
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="query.sortBy" placeholder="字段">
            <el-option label="创建时间" value="createdAt" />
            <el-option label="用户名" value="username" />
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
        <el-button type="primary" @click="openCreate">新增用户</el-button>
        <el-button @click="fetchUsers">刷新</el-button>
      </div>
    </div>
    <el-table :data="users" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removeUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && users.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchUsers">刷新</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="创建时必填，编辑时可留空" />
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
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth";
import { ElMessage, ElMessageBox } from "element-plus";
import { createUser, deleteUser, listUsers, updateUser } from "../../api/users";
import type { User } from "../../types/models";

const users = ref<User[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive({
  id: "",
  username: "",
  email: "",
  password: ""
});
const dialogTitle = computed(() => (isEdit.value ? "编辑用户" : "新增用户"));
const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式不正确", trigger: "blur" }
  ]
};
const query = reactive({
  page: 1,
  size: 10,
  keyword: "",
  sortBy: "createdAt",
  sortOrder: "desc"
});
const storageKey = "query:admin-users";

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await listUsers({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      sortBy: query.sortBy,
      sortOrder: query.sortOrder
    });
    users.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取用户失败，请重试";
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
  fetchUsers();
});

const resetForm = () => {
  form.id = "";
  form.username = "";
  form.email = "";
  form.password = "";
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: User) => {
  isEdit.value = true;
  form.id = row.id;
  form.username = row.username;
  form.email = row.email;
  form.password = "";
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  if (!isEdit.value && !form.password) {
    ElMessage.warning("新增用户需要设置密码");
    return;
  }
  submitting.value = true;
  try {
    if (isEdit.value) {
      const payload: { username?: string; email?: string; password?: string } = {
        username: form.username,
        email: form.email
      };
      if (form.password) {
        payload.password = form.password;
      }
      await updateUser(form.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createUser({ username: form.username, email: form.email, password: form.password });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchUsers();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removeUser = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该用户吗？", "提示", { type: "warning" });
    await deleteUser(id);
    ElMessage.success("删除成功");
    fetchUsers();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  persistQuery();
  fetchUsers();
};

const handleReset = () => {
  query.keyword = "";
  query.page = 1;
  query.size = 10;
  query.sortBy = "createdAt";
  query.sortOrder = "desc";
  persistQuery();
  fetchUsers();
};

const handlePage = (page: number) => {
  query.page = page;
  persistQuery();
  fetchUsers();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  persistQuery();
  fetchUsers();
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
