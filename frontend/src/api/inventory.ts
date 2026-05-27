import http from "./http";
import type { ApiResponse, PageData, Inventory } from "../types/models";

export const listInventory = (params: { page?: number; size?: number; productId?: string }) =>
  http.get<ApiResponse<PageData<Inventory>>>("/inventory", { params });

export const createInventory = (payload: { productId: string; quantity: number }) =>
  http.post<ApiResponse<Inventory>>("/inventory", payload);

export const updateInventory = (id: string, payload: { quantity: number }) =>
  http.put<ApiResponse<Inventory>>(`/inventory/${id}`, payload);

export const deleteInventory = (id: string) => http.delete<ApiResponse<null>>(`/inventory/${id}`);
