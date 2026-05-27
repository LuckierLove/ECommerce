import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import MainLayout from "../layouts/MainLayout.vue";
import Login from "../pages/login/Login.vue";
import UserProducts from "../pages/user/Products.vue";
import UserCart from "../pages/user/Cart.vue";
import UserOrders from "../pages/user/Orders.vue";
import UserAddresses from "../pages/user/Addresses.vue";
import UserCoupons from "../pages/user/Coupons.vue";
import AdminUsers from "../pages/admin/Users.vue";
import AdminMerchants from "../pages/admin/Merchants.vue";
import AdminRoles from "../pages/admin/Roles.vue";
import AdminPermissions from "../pages/admin/Permissions.vue";
import MerchantProducts from "../pages/merchant/Products.vue";
import MerchantOrders from "../pages/merchant/Orders.vue";
import MerchantCoupons from "../pages/merchant/Coupons.vue";
import Logs from "../pages/common/Logs.vue";
import Unauthorized from "../pages/common/Unauthorized.vue";
import { useAuthStore } from "../stores/auth";

const routes: RouteRecordRaw[] = [
  { path: "/login", component: Login, meta: { public: true } },
  { path: "/403", component: Unauthorized, meta: { public: true } },
  {
    path: "/",
    component: MainLayout,
    redirect: "/user/products",
    meta: { requiresAuth: true },
    children: [
      { path: "user/products", component: UserProducts, meta: { roles: ["user"] } },
      { path: "user/cart", component: UserCart, meta: { roles: ["user"] } },
      { path: "user/orders", component: UserOrders, meta: { roles: ["user"] } },
      { path: "user/addresses", component: UserAddresses, meta: { roles: ["user"] } },
      { path: "user/coupons", component: UserCoupons, meta: { roles: ["user"] } },
      { path: "admin/users", component: AdminUsers, meta: { roles: ["admin"] } },
      { path: "admin/merchants", component: AdminMerchants, meta: { roles: ["admin"] } },
      { path: "admin/roles", component: AdminRoles, meta: { roles: ["admin"] } },
      { path: "admin/permissions", component: AdminPermissions, meta: { roles: ["admin"] } },
      { path: "merchant/products", component: MerchantProducts, meta: { roles: ["merchant"] } },
      { path: "merchant/orders", component: MerchantOrders, meta: { roles: ["merchant"] } },
      { path: "merchant/coupons", component: MerchantCoupons, meta: { roles: ["merchant"] } },
      { path: "logs", component: Logs, meta: { roles: ["admin"] } }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  if (to.meta.public) {
    return true;
  }

  const authStore = useAuthStore();
  if (!authStore.token) {
    return "/login";
  }

  const roles = to.meta.roles as string[] | undefined;
  if (roles && roles.length > 0 && !roles.includes(authStore.role)) {
    return "/403";
  }

  return true;
});

export default router;
