export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageData<T> {
  total: number;
  page: number;
  size: number;
  list: T[];
}

export interface User {
  id: string;
  username: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}

export interface Role {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface Permission {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: string;
  createdAt: string;
  updatedAt: string;
}

export interface Merchant {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface Order {
  id: string;
  userId: string;
  totalAmount: string;
  subtotal?: string;
  taxAmount?: string;
  shippingCost?: string;
  currency?: string;
  status?: string;
  paymentStatus?: string;
  itemsCount?: number;
  billingAddressId?: string | null;
  shippingAddressId?: string | null;
  shippingMethod?: string | null;
  trackingNumber?: string | null;
  note?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OrderDetail extends Order {
  items: OrderItem[];
}

export interface OrderItem {
  id: string;
  orderId: string;
  productId: string;
  quantity: number;
  price: string;
  sku?: string | null;
  name?: string | null;
  unitPrice?: string | null;
  taxAmount?: string | null;
  discountAmount?: string | null;
  subtotal?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Cart {
  id: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
}

export interface CartItem {
  id: string;
  cartId: string;
  productId: string;
  quantity: number;
  unitPrice?: string | null;
  selectedOptions?: string | null;
  subtotal?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Payment {
  id: string;
  orderId: string;
  amount: string;
  paymentMethod: number;
  createdAt: string;
  updatedAt: string;
}

export interface Address {
  id: string;
  userId: string;
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  createdAt: string;
  updatedAt: string;
}

export interface Inventory {
  id: string;
  productId: string;
  quantity: number;
  createdAt: string;
  updatedAt: string;
}

export interface Coupon {
  id: string;
  code: string;
  discountAmount: string;
  createdAt: string;
  updatedAt: string;
}

export interface LogRecord {
  id: string;
  userId: string;
  logType: number;
  message: string;
  createdAt: string;
  updatedAt: string;
}
