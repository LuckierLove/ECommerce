# API文档
格式为Apifox导出的Markdown格式，包含接口的URL、请求方法、请求参数、响应数据等信息。请根据实际项目需求进行修改和完善。

`所有接口均以/api开头，后续根据功能模块进行分类，例如/auth、/products、/orders等。`

`统一返回格式为JSON，包含code、message和data字段，例如：`
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    // 返回的数据内容
  }
}
```

`分页统一返回格式为：`
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "total": 100, // 总记录数
    "page": 1, // 当前页码
    "size": 10, // 每页记录数
    "list": [ // 当前页的数据列表
      {
        // 数据项内容
      }
    ]
  }
}
```

# 模型
所有模型均以JSON格式定义，包含字段名称、类型和描述。
参考`scheme.sql`文件中的数据库表结构进行定义，确保字段名称和类型与数据库一致。

## 用户模型
```json
{
  "id": "string", // 用户ID
  "username": "string", // 用户名
  "email": "string", // 邮箱地址
  "password": "string", // 密码字段在实际返回数据中应进行加密处理或不返回
  "createdAt": "string", // 创建时间 
  "updatedAt": "string" // 更新时间
}
```
## 商品模型
```json
{
  "id": "string", // 商品ID
  "name": "string", // 商品名称
  "description": "string", // 商品描述
  "price": "string", // 商品价格，单位为分
  "createdAt": "string", // 创建时间 
  "updatedAt": "string" // 更新时间
}
```

## 角色模型
```json
{
  "id": "string", // 角色ID
  "name": "string", // 角色名称
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 权限模型
```json
{
  "id": "string", // 权限ID
  "name": "string", // 权限名称
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```


## 商家模型
```json
{
  "id": "string", // 商家ID
  "name": "string", // 商家名称
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```


## 订单模型
```json
{
  "id": "string", // 订单ID
  "userId": "string", // 用户ID
  "totalAmount": "string", // 订单总金额
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 订单项模型
```json
{
  "id": "string", // 订单项ID
  "orderId": "string", // 订单ID
  "productId": "string", // 商品ID
  "quantity": 0, // 数量
  "price": "string", // 单价
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 购物车模型
```json
{
  "id": "string", // 购物车ID
  "userId": "string", // 用户ID
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 购物车项模型
```json
{
  "id": "string", // 购物车项ID
  "cartId": "string", // 购物车ID
  "productId": "string", // 商品ID
  "quantity": 0, // 数量
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 支付模型
```json
{
  "id": "string", // 支付ID
  "orderId": "string", // 订单ID
  "amount": "string", // 支付金额
  "paymentMethod": 1, // 支付方式: 1支付宝 2微信 3银行卡
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 地址模型
```json
{
  "id": "string", // 地址ID
  "userId": "string", // 用户ID
  "street": "string", // 街道
  "city": "string", // 城市
  "state": "string", // 省份
  "zipCode": "string", // 邮编
  "country": "string", // 国家
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 库存模型
```json
{
  "id": "string", // 库存ID
  "productId": "string", // 商品ID
  "quantity": 0, // 库存数量
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```

## 优惠券模型
```json
{
  "id": "string", // 优惠券ID
  "code": "string", // 优惠券编码
  "discountAmount": "string", // 优惠金额
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```


## 日志模型
```json
{
  "id": "string", // 日志ID
  "userId": "string", // 用户ID
  "logType": 1, // 日志类型: 1登录 2操作
  "message": "string", // 日志内容
  "createdAt": "string", // 创建时间
  "updatedAt": "string" // 更新时间
}
```



# 认证相关接口
## 用户注册
- URL: /api/auth/register
- 方法: POST
- 请求参数:
```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```
- 响应数据:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```
## 用户登录
- URL: /api/auth/login
- 方法: POST
- 请求参数:
```json
{
  "username": "string",
  "password": "string"
}
```
- 响应数据:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "string" // JWT令牌
  }
}
```
# 用户相关接口
## 获取用户信息
- URL: /api/users/me
- 方法: GET
- 请求参数: 无
- 响应数据:
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    // 其他用户信息字段
  }
}
```

# 用户管理接口
## 获取用户列表
- URL: /api/users
- 方法: GET
- 请求参数: page, size, keyword
- 响应数据: 分页统一返回格式

## 获取用户详情
- URL: /api/users/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 用户模型

## 创建用户
- URL: /api/users
- 方法: POST
- 请求参数:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```
- 响应数据: 用户模型

## 更新用户
- URL: /api/users/{id}
- 方法: PUT
- 请求参数:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```
- 响应数据: 用户模型

## 删除用户
- URL: /api/users/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 角色管理接口
## 获取角色列表
- URL: /api/roles
- 方法: GET
- 请求参数: page, size, keyword
- 响应数据: 分页统一返回格式

## 获取角色详情
- URL: /api/roles/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 角色模型

## 创建角色
- URL: /api/roles
- 方法: POST
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 角色模型

## 更新角色
- URL: /api/roles/{id}
- 方法: PUT
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 角色模型

## 删除角色
- URL: /api/roles/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 权限管理接口
## 获取权限列表
- URL: /api/permissions
- 方法: GET
- 请求参数: page, size, keyword
- 响应数据: 分页统一返回格式

## 获取权限详情
- URL: /api/permissions/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 权限模型

## 创建权限
- URL: /api/permissions
- 方法: POST
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 权限模型

## 更新权限
- URL: /api/permissions/{id}
- 方法: PUT
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 权限模型

## 删除权限
- URL: /api/permissions/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式


# 商品管理接口
## 获取商品列表
- URL: /api/products
- 方法: GET
- 请求参数: page, size, keyword, minPrice, maxPrice
- 响应数据: 分页统一返回格式

## 获取商品详情
- URL: /api/products/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 商品模型

## 创建商品
- URL: /api/products
- 方法: POST
- 请求参数:
```json
{
  "name": "string",
  "description": "string",
  "price": "string"
}
```
- 响应数据: 商品模型

## 更新商品
- URL: /api/products/{id}
- 方法: PUT
- 请求参数:
```json
{
  "name": "string",
  "description": "string",
  "price": "string"
}
```
- 响应数据: 商品模型

## 删除商品
- URL: /api/products/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 商家管理接口
## 获取商家列表
- URL: /api/merchants
- 方法: GET
- 请求参数: page, size, keyword
- 响应数据: 分页统一返回格式

## 获取商家详情
- URL: /api/merchants/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 商家模型

## 创建商家
- URL: /api/merchants
- 方法: POST
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 商家模型

## 更新商家
- URL: /api/merchants/{id}
- 方法: PUT
- 请求参数:
```json
{
  "name": "string"
}
```
- 响应数据: 商家模型

## 删除商家
- URL: /api/merchants/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式


# 订单管理接口
## 获取订单列表
- URL: /api/orders
- 方法: GET
- 请求参数: page, size, userId
- 响应数据: 分页统一返回格式

## 获取订单详情
- URL: /api/orders/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 订单模型

## 创建订单
- URL: /api/orders
- 方法: POST
- 请求参数:
```json
{
  "userId": "string",
  "totalAmount": "string"
}
```
- 响应数据: 订单模型

## 更新订单
- URL: /api/orders/{id}
- 方法: PUT
- 请求参数:
```json
{
  "totalAmount": "string"
}
```
- 响应数据: 订单模型

## 删除订单
- URL: /api/orders/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 订单项管理接口
## 获取订单项列表
- URL: /api/order-items
- 方法: GET
- 请求参数: page, size, orderId, productId
- 响应数据: 分页统一返回格式

## 获取订单项详情
- URL: /api/order-items/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 订单项模型

## 创建订单项
- URL: /api/order-items
- 方法: POST
- 请求参数:
```json
{
  "orderId": "string",
  "productId": "string",
  "quantity": 0,
  "price": "string"
}
```
- 响应数据: 订单项模型

## 更新订单项
- URL: /api/order-items/{id}
- 方法: PUT
- 请求参数:
```json
{
  "quantity": 0,
  "price": "string"
}
```
- 响应数据: 订单项模型

## 删除订单项
- URL: /api/order-items/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 购物车管理接口
## 获取购物车列表
- URL: /api/carts
- 方法: GET
- 请求参数: page, size, userId
- 响应数据: 分页统一返回格式

## 获取购物车详情
- URL: /api/carts/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 购物车模型

## 创建购物车
- URL: /api/carts
- 方法: POST
- 请求参数:
```json
{
  "userId": "string"
}
```
- 响应数据: 购物车模型

## 更新购物车
- URL: /api/carts/{id}
- 方法: PUT
- 请求参数:
```json
{
  "userId": "string"
}
```
- 响应数据: 购物车模型

## 删除购物车
- URL: /api/carts/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 购物车项管理接口
## 获取购物车项列表
- URL: /api/cart-items
- 方法: GET
- 请求参数: page, size, cartId, productId
- 响应数据: 分页统一返回格式

## 获取购物车项详情
- URL: /api/cart-items/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 购物车项模型

## 创建购物车项
- URL: /api/cart-items
- 方法: POST
- 请求参数:
```json
{
  "cartId": "string",
  "productId": "string",
  "quantity": 0
}
```
- 响应数据: 购物车项模型

## 更新购物车项
- URL: /api/cart-items/{id}
- 方法: PUT
- 请求参数:
```json
{
  "quantity": 0
}
```
- 响应数据: 购物车项模型

## 删除购物车项
- URL: /api/cart-items/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 支付管理接口
## 获取支付列表
- URL: /api/payments
- 方法: GET
- 请求参数: page, size, orderId
- 响应数据: 分页统一返回格式

## 获取支付详情
- URL: /api/payments/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 支付模型

## 创建支付
- URL: /api/payments
- 方法: POST
- 请求参数:
```json
{
  "orderId": "string",
  "amount": "string",
  "paymentMethod": 1
}
```
- 响应数据: 支付模型

## 更新支付
- URL: /api/payments/{id}
- 方法: PUT
- 请求参数:
```json
{
  "amount": "string",
  "paymentMethod": 1
}
```
- 响应数据: 支付模型

## 删除支付
- URL: /api/payments/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 地址管理接口
## 获取地址列表
- URL: /api/addresses
- 方法: GET
- 请求参数: page, size, userId, city, state
- 响应数据: 分页统一返回格式

## 获取地址详情
- URL: /api/addresses/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 地址模型

## 创建地址
- URL: /api/addresses
- 方法: POST
- 请求参数:
```json
{
  "userId": "string",
  "street": "string",
  "city": "string",
  "state": "string",
  "zipCode": "string",
  "country": "string"
}
```
- 响应数据: 地址模型

## 更新地址
- URL: /api/addresses/{id}
- 方法: PUT
- 请求参数:
```json
{
  "street": "string",
  "city": "string",
  "state": "string",
  "zipCode": "string",
  "country": "string"
}
```
- 响应数据: 地址模型

## 删除地址
- URL: /api/addresses/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 库存管理接口
## 获取库存列表
- URL: /api/inventory
- 方法: GET
- 请求参数: page, size, productId
- 响应数据: 分页统一返回格式

## 获取库存详情
- URL: /api/inventory/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 库存模型

## 创建库存
- URL: /api/inventory
- 方法: POST
- 请求参数:
```json
{
  "productId": "string",
  "quantity": 0
}
```
- 响应数据: 库存模型

## 更新库存
- URL: /api/inventory/{id}
- 方法: PUT
- 请求参数:
```json
{
  "quantity": 0
}
```
- 响应数据: 库存模型

## 删除库存
- URL: /api/inventory/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式

# 优惠券管理接口
## 获取优惠券列表
- URL: /api/coupons
- 方法: GET
- 请求参数: page, size, code
- 响应数据: 分页统一返回格式

## 获取优惠券详情
- URL: /api/coupons/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 优惠券模型

## 创建优惠券
- URL: /api/coupons
- 方法: POST
- 请求参数:
```json
{
  "code": "string",
  "discountAmount": "string"
}
```
- 响应数据: 优惠券模型

## 更新优惠券
- URL: /api/coupons/{id}
- 方法: PUT
- 请求参数:
```json
{
  "code": "string",
  "discountAmount": "string"
}
```
- 响应数据: 优惠券模型

## 删除优惠券
- URL: /api/coupons/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式


# 日志管理接口
## 获取日志列表
- URL: /api/logs
- 方法: GET
- 请求参数: page, size, userId, logType
- 响应数据: 分页统一返回格式

## 获取日志详情
- URL: /api/logs/{id}
- 方法: GET
- 请求参数: 无
- 响应数据: 日志模型

## 创建日志
- URL: /api/logs
- 方法: POST
- 请求参数:
```json
{
  "userId": "string",
  "logType": 1,
  "message": "string"
}
```
- 响应数据: 日志模型

## 更新日志
- URL: /api/logs/{id}
- 方法: PUT
- 请求参数:
```json
{
  "logType": 1,
  "message": "string"
}
```
- 响应数据: 日志模型

## 删除日志
- URL: /api/logs/{id}
- 方法: DELETE
- 请求参数: 无
- 响应数据: 基础返回格式