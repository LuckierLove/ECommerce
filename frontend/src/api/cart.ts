import http from "./http";
import type { ApiResponse, PageData, Cart, CartItem } from "../types/models";

export const listCarts = (params: { page?: number; size?: number; userId?: string }) =>
  http.get<ApiResponse<PageData<Cart>>>("/carts", { params });

export const listCartItems = (params: { page?: number; size?: number; cartId?: string; productId?: string }) =>
  http.get<ApiResponse<PageData<CartItem>>>("/cart-items", { params });

export const createCartItem = (payload: { cartId: string; productId: string; quantity: number }) =>
  http.post<ApiResponse<CartItem>>("/cart-items", payload);

export const updateCartItem = (id: string, payload: { quantity: number }) =>
  http.put<ApiResponse<CartItem>>(`/cart-items/${id}`, payload);

export const deleteCartItem = (id: string) => http.delete<ApiResponse<null>>(`/cart-items/${id}`);
