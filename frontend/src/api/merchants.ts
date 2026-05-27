import http from "./http";
import type { ApiResponse, PageData, Merchant } from "../types/models";

export const listMerchants = (params: { page?: number; size?: number; keyword?: string }) =>
  http.get<ApiResponse<PageData<Merchant>>>("/merchants", { params });

export const createMerchant = (payload: { name: string }) =>
  http.post<ApiResponse<Merchant>>("/merchants", payload);

export const updateMerchant = (id: string, payload: { name: string }) =>
  http.put<ApiResponse<Merchant>>(`/merchants/${id}`, payload);

export const deleteMerchant = (id: string) => http.delete<ApiResponse<null>>(`/merchants/${id}`);
