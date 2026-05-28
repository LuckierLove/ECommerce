import http from "./http";
import type { ApiResponse, OrderDetail, PageData, Order } from "../types/models";

export const listOrders = (params: { page?: number; size?: number; userId?: string }) =>
  http.get<ApiResponse<PageData<Order>>>("/orders", { params });

export const getOrder = (id: string) => http.get<ApiResponse<OrderDetail>>(`/orders/${id}`);

export const createOrder = (payload: { userId: string; totalAmount: string; items?: { productId: string; quantity: number; price?: string }[]; subtotal?: string; shippingAddressId?: string; billingAddressId?: string; couponCodes?: string[] }) =>
  http.post<ApiResponse<Order>>("/orders", payload);

export const updateOrder = (id: string, payload: { totalAmount?: string; status?: string; paymentStatus?: string }) =>
  http.put<ApiResponse<Order>>(`/orders/${id}`, payload);

export const deleteOrder = (id: string) => http.delete<ApiResponse<null>>(`/orders/${id}`);
