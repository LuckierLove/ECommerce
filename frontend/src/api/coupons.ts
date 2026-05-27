import http from "./http";
import type { ApiResponse, PageData, Coupon } from "../types/models";

export const listCoupons = (params: { page?: number; size?: number; code?: string }) =>
  http.get<ApiResponse<PageData<Coupon>>>("/coupons", { params });

export const createCoupon = (payload: { code: string; discountAmount: string }) =>
  http.post<ApiResponse<Coupon>>("/coupons", payload);

export const updateCoupon = (id: string, payload: { code: string; discountAmount: string }) =>
  http.put<ApiResponse<Coupon>>(`/coupons/${id}`, payload);

export const deleteCoupon = (id: string) => http.delete<ApiResponse<null>>(`/coupons/${id}`);
