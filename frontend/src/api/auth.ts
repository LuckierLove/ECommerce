import http from "./http";
import type { ApiResponse } from "../types/models";

export interface LoginResponse {
  token: string;
}

export const login = (payload: { username: string; password: string }) =>
  http.post<ApiResponse<LoginResponse>>("/auth/login", payload);

export const register = (payload: { username: string; email: string; password: string }) =>
  http.post<ApiResponse<null>>("/auth/register", payload);
