# 前端项目文档

## 项目简介
前端采用 Vue 3 + Element Plus，提供普通用户、管理员、商家三类界面，已与后端 API 基础 CRUD 对接，提供可运行的页面骨架与接口调用。

## 技术栈
- Bun
- Vue 3
- Vue Router
- Pinia
- Element Plus
- Axios
- Vite

## 项目结构
```
frontend/
	index.html
	package.json
	vite.config.ts
	src/
		api/            # API 调用
		layouts/        # 布局
		pages/          # 页面
		router/         # 路由
		stores/         # 状态管理
		styles/         # 全局样式
		types/          # 类型定义
		App.vue
		main.ts
```

## 开发与运行
```bash
bun install
bun run dev
```

## 环境变量
默认以 `/api` 作为接口前缀，如需自定义可在 `.env` 中配置:
```
VITE_API_BASE=/api
```

## 功能模块
`根据不同的用户分成不同的功能模块，分别是普通用户界面、管理员界面和商家界面。`
`利用 Vue Router 实现前端路由，使用 Axios 进行数据请求，采用 Element Plus 组件库进行界面设计。`

### 普通用户
- 浏览商品列表
- 添加商品到购物车
- 提交订单
- 查看订单状态
- 管理个人地址(增删改查)
- 查看优惠券

### 管理员
- 管理用户(增删改查)
- 管理商家(增删改查)
- 管理角色与权限

### 商家
- 发布商品
- 管理商品(增删改查)
- 查看订单
- 管理优惠券(增删改查)

### 通用功能
- 登录/注册
- 个人信息管理
- 搜索功能
- 消息通知