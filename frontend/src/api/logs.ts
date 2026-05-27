import http from "./http";
import type { ApiResponse, PageData, LogRecord } from "../types/models";

export const listLogs = (params: { page?: number; size?: number; userId?: string; logType?: number }) =>
  http.get<ApiResponse<PageData<LogRecord>>>("/logs", { params });

export const createLog = (payload: { userId: string; logType: number; message: string }) =>
  http.post<ApiResponse<LogRecord>>("/logs", payload);

export const updateLog = (id: string, payload: { logType: number; message: string }) =>
  http.put<ApiResponse<LogRecord>>(`/logs/${id}`, payload);

export const deleteLog = (id: string) => http.delete<ApiResponse<null>>(`/logs/${id}`);
