<template>
  <div class="page">
    <div class="page-title">购物车</div>
    <div class="toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="购物车ID">
          <el-input v-model="query.cartId" placeholder="购物车ID" />
        </el-form-item>
        <el-form-item label="商品ID">
          <el-input v-model="query.productId" placeholder="商品ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增商品</el-button>
        <el-button @click="fetchItems">刷新</el-button>
      </div>
    </div>
    <el-table :data="items" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="productId" label="商品ID" />
      <el-table-column prop="quantity" label="数量" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="removeItem(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-alert v-if="errorMessage" type="error" show-icon :title="errorMessage" class="state" />
    <el-empty v-else-if="!loading && items.length === 0" description="暂无数据" class="state">
      <el-button type="primary" @click="fetchItems">刷新</el-button>
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
        <el-form-item label="购物车ID" prop="cartId">
          <el-input v-model="form.cartId" placeholder="请输入购物车ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" />
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
import { createCartItem, deleteCartItem, listCartItems, updateCartItem } from "../../api/cart";
import type { CartItem } from "../../types/models";

const items = ref<CartItem[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive({
  id: "",
  cartId: "",
  productId: "",
  quantity: 1
});
const dialogTitle = computed(() => (isEdit.value ? "编辑购物车项" : "新增购物车项"));
const rules = {
  cartId: [{ required: true, message: "请输入购物车ID", trigger: "blur" }],
  productId: [{ required: true, message: "请输入商品ID", trigger: "blur" }]
};
const query = reactive({
  page: 1,
  size: 10,
  cartId: "",
  productId: ""
});

const fetchItems = async () => {
  loading.value = true;
  try {
    const res = await listCartItems({
      page: query.page,
      size: query.size,
      cartId: query.cartId || undefined,
      productId: query.productId || undefined
    });
    items.value = res.data.list;
    total.value = res.data.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取购物车失败，请重试";
  } finally {
    loading.value = false;
  }
};

onMounted(fetchItems);

const resetForm = () => {
  form.id = "";
  form.cartId = "";
  form.productId = "";
  form.quantity = 1;
};

const openCreate = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: CartItem) => {
  isEdit.value = true;
  form.id = row.id;
  form.cartId = row.cartId;
  form.productId = row.productId;
  form.quantity = row.quantity;
  dialogVisible.value = true;
};

const submit = async () => {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateCartItem(form.id, { quantity: form.quantity });
      ElMessage.success("更新成功");
    } else {
      await createCartItem({ cartId: form.cartId, productId: form.productId, quantity: form.quantity });
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    fetchItems();
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    submitting.value = false;
  }
};

const removeItem = async (id: string) => {
  try {
    await ElMessageBox.confirm("确认删除该购物车项吗？", "提示", { type: "warning" });
    await deleteCartItem(id);
    ElMessage.success("删除成功");
    fetchItems();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const handleSearch = () => {
  query.page = 1;
  fetchItems();
};

const handleReset = () => {
  query.page = 1;
  query.size = 10;
  query.cartId = "";
  query.productId = "";
  fetchItems();
};

const handlePage = (page: number) => {
  query.page = page;
  fetchItems();
};

const handleSize = (size: number) => {
  query.size = size;
  query.page = 1;
  fetchItems();
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
