import http from "./http";
import type { ApiResponse, PageData, Payment } from "../types/models";

export const listPayments = (params: { page?: number; size?: number; orderId?: string }) =>
  http.get<ApiResponse<PageData<Payment>>>("/payments", { params });

export const createPayment = (payload: { orderId: string; amount: string; paymentMethod: number }) =>
  http.post<ApiResponse<Payment>>("/payments", payload);

export const updatePayment = (id: string, payload: { amount: string; paymentMethod: number }) =>
  http.put<ApiResponse<Payment>>(`/payments/${id}`, payload);

export const deletePayment = (id: string) => http.delete<ApiResponse<null>>(`/payments/${id}`);
