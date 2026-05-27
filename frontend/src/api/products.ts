import http from "./http";
import type { ApiResponse, PageData, Product } from "../types/models";

export const listProducts = (params: { page?: number; size?: number; keyword?: string; minPrice?: number; maxPrice?: number }) =>
  http.get<ApiResponse<PageData<Product>>>("/products", { params });

export const getProduct = (id: string) => http.get<ApiResponse<Product>>(`/products/${id}`);

export const createProduct = (payload: { name: string; description?: string; price: string }) =>
  http.post<ApiResponse<Product>>("/products", payload);

export const updateProduct = (id: string, payload: { name?: string; description?: string; price?: string }) =>
  http.put<ApiResponse<Product>>(`/products/${id}`, payload);

export const deleteProduct = (id: string) => http.delete<ApiResponse<null>>(`/products/${id}`);
