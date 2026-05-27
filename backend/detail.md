# 后端项目文档

## 项目简介
本后端为电商信息管理网站提供 RESTful API 服务，采用 Spring Boot + Kotlin 开发，MyBatis-Plus 负责数据访问，MySQL 存储业务数据，Redis 用于缓存，JWT + Sa-Token 负责认证与鉴权。

## 技术栈
- Spring Boot
- Kotlin
- Gradle
- MyBatis-Plus
- MySQL
- Redis
- JWT
- Sa-Token

## 项目结构
```
backend/
	build.gradle.kts
	src/
		main/
			kotlin/
				com/example/ecommerce/
					config/        # 配置(数据库、Redis、Sa-Token、跨域等)
					controller/    # 控制器(REST API)
					service/       # 业务逻辑
					mapper/    # 数据访问(MyBatis-Plus Mapper)
					model/
						entity/      # 数据库实体
						dto/         # 请求/响应 DTO
						vo/          # 视图模型
					security/      # 认证、鉴权、拦截器
					common/        # 统一返回、异常、常量、工具
			resources/
				application.yml
				mapper/          # MyBatis XML(如有)
```

## 统一约定
- API 前缀: `/api`
- 统一返回格式:
```json
{
	"code": 200,
	"message": "请求成功",
	"data": {}
}
```
- 分页返回格式:
```json
{
	"code": 200,
	"message": "请求成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"list": []
	}
}
```

## 认证与鉴权
- 登录后返回 JWT Token，前端以 `Authorization: Bearer <token>` 方式携带。
- Sa-Token 负责登录态、权限校验与注解拦截。
- 角色模型: 管理员、买家、商家。

## 核心业务模块
- 用户与权限: 用户注册、登录、个人信息、角色与权限管理。
- 商品与商家: 商品管理、商家管理、库存与上架管理。
- 订单与支付: 订单创建、订单项、支付记录。
- 购物车: 购物车与购物车项。
- 优惠券: 优惠券维护与订单使用。
- 地址: 用户地址管理。
- 日志: 登录日志与操作日志。

## 数据模型概览
与数据库表保持一致，核心表包括:
- users
- roles
- permissions
- products
- merchants
- orders
- order_items
- carts
- cart_items
- payments
- addresses
- inventory
- coupons
- logs

## 接口说明
完整接口定义见项目根目录的 [api.md](../api.md)。