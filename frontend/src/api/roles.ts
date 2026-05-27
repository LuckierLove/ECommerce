import http from "./http";
import type { ApiResponse, PageData, Role } from "../types/models";

export const listRoles = (params: { page?: number; size?: number; keyword?: string }) =>
  http.get<ApiResponse<PageData<Role>>>("/roles", { params });

export const createRole = (payload: { name: string }) =>
  http.post<ApiResponse<Role>>("/roles", payload);

export const updateRole = (id: string, payload: { name: string }) =>
  http.put<ApiResponse<Role>>(`/roles/${id}`, payload);

export const deleteRole = (id: string) => http.delete<ApiResponse<null>>(`/roles/${id}`);
