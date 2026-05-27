import http from "./http";
import type { ApiResponse, PageData, User } from "../types/models";

export const getMe = () => http.get<ApiResponse<User>>("/users/me");

export const listUsers = (params: { page?: number; size?: number; keyword?: string }) =>
  http.get<ApiResponse<PageData<User>>>("/users", { params });

export const getUser = (id: string) => http.get<ApiResponse<User>>(`/users/${id}`);

export const createUser = (payload: { username: string; email: string; password: string }) =>
  http.post<ApiResponse<User>>("/users", payload);

export const updateUser = (id: string, payload: { username?: string; email?: string; password?: string }) =>
  http.put<ApiResponse<User>>(`/users/${id}`, payload);

export const deleteUser = (id: string) => http.delete<ApiResponse<null>>(`/users/${id}`);
