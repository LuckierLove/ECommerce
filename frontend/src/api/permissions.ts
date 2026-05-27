import http from "./http";
import type { ApiResponse, PageData, Permission } from "../types/models";

export const listPermissions = (params: { page?: number; size?: number; keyword?: string }) =>
  http.get<ApiResponse<PageData<Permission>>>("/permissions", { params });

export const createPermission = (payload: { name: string }) =>
  http.post<ApiResponse<Permission>>("/permissions", payload);

export const updatePermission = (id: string, payload: { name: string }) =>
  http.put<ApiResponse<Permission>>(`/permissions/${id}`, payload);

export const deletePermission = (id: string) => http.delete<ApiResponse<null>>(`/permissions/${id}`);
