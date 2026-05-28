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
        <el-button type="primary" @click="openCreate">新增购物车项</el-button>
        <el-button @click="fetchItems">刷新</el-button>
      </div>
    </div>
    <el-table :data="items" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column label="商品" min-width="220">
        <template #default="scope">
          <div class="product-cell">
            <div class="product-name">{{ productName(scope.row.productId) }}</div>
            <div class="product-id">{{ scope.row.productId }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价" width="120">
        <template #default="scope">{{ scope.row.unitPrice || productPrice(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="小计" width="140">
        <template #default="scope">{{ itemSubtotal(scope.row) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" @click="openEdit(scope.row)">编辑</el-button>
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
    <div class="cart-summary">
      <div class="checkout-helpers">
        <el-form :inline="true">
          <el-form-item label="配送地址">
            <el-select v-model="selectedShippingAddress" placeholder="选择地址" style="width:320px">
              <el-option v-for="addr in addresses" :key="addr.id" :label="addr.street + ' ' + addr.city" :value="addr.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="优惠码">
            <el-input v-model="couponCode" placeholder="输入优惠码" style="width:160px" />
          </el-form-item>
        </el-form>
      </div>
      <div>小计：{{ formattedSubtotal }}</div>
      <div>税费：{{ formattedTax }}</div>
      <div>配送费：{{ formattedShipping }}</div>
      <div class="total">总计：{{ formattedTotal }}</div>
      <el-button type="primary" :loading="checkingOut" @click="checkout" :disabled="items.length===0">结算</el-button>
    </div>
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
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth";
import { ElMessage, ElMessageBox } from "element-plus";
import { createCartItem, deleteCartItem, listCartItems, updateCartItem } from "../../api/cart";
import { createOrder } from "../../api/orders";
import { listProducts } from "../../api/products";
import { listAddresses } from "../../api/addresses";
import type { CartItem, Product } from "../../types/models";

const items = ref<CartItem[]>([]);
const loading = ref(false);
const total = ref(0);
const errorMessage = ref("");
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const products = ref<Product[]>([]);
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
    const pageData = (res as any).data as { list: CartItem[]; total: number };
    items.value = pageData.list;
    total.value = pageData.total;
    errorMessage.value = "";
  } catch (error) {
    errorMessage.value = "获取购物车失败，请重试";
  } finally {
    loading.value = false;
  }
};

const productPrice = (productId: string) => products.value.find((p) => p.id === productId)?.price || "0.00";

const itemSubtotal = (item: CartItem) => {
  const price = item.unitPrice || productPrice(item.productId);
  return (Number(price) * item.quantity).toFixed(2);
};

const subtotal = computed(() => items.value.reduce((acc, it) => acc + Number(it.unitPrice || productPrice(it.productId)) * it.quantity, 0));
const tax = computed(() => +(subtotal.value * 0.08).toFixed(2));
const shipping = computed(() => (subtotal.value >= 500 ? 0 : 10));
const totalAmount = computed(() => subtotal.value + tax.value + shipping.value);

const formattedSubtotal = computed(() => subtotal.value.toFixed(2));
const formattedTax = computed(() => tax.value.toFixed(2));
const formattedShipping = computed(() => shipping.value.toFixed(2));
const formattedTotal = computed(() => totalAmount.value.toFixed(2));

const checkingOut = ref(false);
const addresses = ref<any[]>([]);
const selectedShippingAddress = ref<string | null>(null);
const couponCode = ref("");

const checkout = async () => {
  if (!authStore.userId) {
    ElMessage.error("未登录用户无法结算");
    return;
  }
  try {
    await ElMessageBox.confirm("确认提交订单并清空购物车？", "结算", { type: "warning" });
  } catch (e) {
    return;
  }
  checkingOut.value = true;
  try {
    const payload = {
      userId: authStore.userId,
      totalAmount: formattedTotal.value,
      subtotal: formattedSubtotal.value,
      items: items.value.map((it) => ({ productId: it.productId, quantity: it.quantity, price: it.unitPrice || productPrice(it.productId) })),
      shippingAddressId: selectedShippingAddress.value,
      couponCodes: couponCode.value ? [couponCode.value] : undefined
    };
    const res = await createOrder(payload as any);
    ElMessage.success("下单成功");
    // 清空购物车项
    for (const it of items.value) {
      try {
        await deleteCartItem(it.id);
      } catch {}
    }
    fetchItems();
    // 跳到我的订单页面
    router.push("/user/orders");
  } catch (error) {
    ElMessage.error("结算失败，请重试");
  } finally {
    checkingOut.value = false;
  }
};

const fetchProducts = async () => {
  try {
    const res = await listProducts({ page: 1, size: 1000 });
    const pageData = (res as any).data as { list: Product[] };
    products.value = pageData.list;
  } catch {
    products.value = [];
  }
};

const productName = (productId: string) => products.value.find((product) => product.id === productId)?.name || productId;

const router = useRouter();
const authStore = useAuthStore();

onMounted(() => {
  if (!authStore.token) {
    router.push("/login");
    return;
  }
  fetchProducts();
  fetchItems();
  fetchAddresses();
});

const fetchAddresses = async () => {
  try {
    const res = await listAddresses({ page: 1, size: 50, userId: authStore.userId });
    const pageData = (res as any).data as { list: any[] };
    addresses.value = pageData.list || [];
    if (addresses.value.length > 0) selectedShippingAddress.value = addresses.value[0].id;
  } catch {
    addresses.value = [];
  }
};

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

.product-cell {
  display: grid;
  gap: 2px;
}

.product-name {
  font-weight: 600;
}

.product-id {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.pager {
  margin-top: 12px;
  justify-content: flex-end;
}

.state {
  margin-top: 12px;
}
</style>
