import http from "./http";
import type { ApiResponse, PageData, Address } from "../types/models";

export const listAddresses = (params: { page?: number; size?: number; userId?: string; city?: string; state?: string }) =>
  http.get<ApiResponse<PageData<Address>>>("/addresses", { params });

export const createAddress = (payload: { userId: string; street: string; city: string; state: string; zipCode: string; country: string }) =>
  http.post<ApiResponse<Address>>("/addresses", payload);

export const updateAddress = (
  id: string,
  payload: { street: string; city: string; state: string; zipCode: string; country: string }
) => http.put<ApiResponse<Address>>(`/addresses/${id}`, payload);

export const deleteAddress = (id: string) => http.delete<ApiResponse<null>>(`/addresses/${id}`);
