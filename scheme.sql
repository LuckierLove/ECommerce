/*
    创建电商数据库
*/
create database ecommerce;

use commerce;

/*
    创建用户表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录用户的创建和更新时间
*/
create table users (
    id varchar(255) not null primary key,
    username varchar(255) not null unique,
    email varchar(255) not null unique,
    password varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建角色表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录角色的创建和更新时间
    默认角色为
    - 管理员
    - 买家
    - 商家
*/
create table roles (
    id varchar(255) not null primary key,
    name varchar(255) not null unique,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建用户角色关联表
    user_id 和 role_id 共同构成主键，确保每个用户和角色的组合唯一
    created_at 和 updated_at 由后端自动设置，记录关联的创建和更新时间
    后端使用逻辑外键，不使用物理外键约束
*/
create table user_roles (
    user_id varchar(255) not null,
    role_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (user_id, role_id),
);

/*
    创建权限表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录权限的创建和更新时间
    权限和角色用于请求不同的接口，控制用户访问不同的资源和操作
    根据后端接口，每一个接口有对应不同的权限，用户通过角色分配不同的权限来访问接口，实现细粒度的权限控制
*/
create table permissions (
    id varchar(255) not null primary key,
    name varchar(255) not null unique,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建角色权限关联表
    role_id 和 permission_id 共同构成主键，确保每个角色和权限的组合唯一
    created_at 和 updated_at 由后端自动设置，记录关联的创建和更新时间
    后端使用逻辑外键，不使用物理外键约束
*/
create table role_permissions (
    role_id varchar(255) not null,
    permission_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (role_id, permission_id),
);

/*
    创建商品表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录商品的创建和更新时间
*/
create table products (
    id varchar(255) not null primary key,
    name varchar(255) not null,
    description text,
    price decimal(10, 2) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建商家表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录商家的创建和更新时间
    商家和商品是一对多关系，一个商家可以有多个商品，但一个商品只能属于一个商家
*/
create table merchants (
    id varchar(255) not null primary key,
    name varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/**
    创建商家与用户账号关联表
    merchant_id 和 user_id 共同构成主键，确保每个商家和用户的组合唯一
    created_at 和 updated_at 由后端自动设置，记录关联的创建和更新时间

    进行注册时，商家用户会被分配一个商家账号，
    同时系统也会分配一个用户账号，商家用户可以通过用户账号登录系统，进行商家相关的操作，
    商家用户和商家账号是一对一关系，一个商家用户只能对应一个商家账号
    后端使用逻辑外键，不使用物理外键约束
*/
create table merchant_users (
    merchant_id varchar(255) not null,
    user_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (merchant_id, user_id),
);

/*
    创建商家商品关联表
    merchant_id 和 product_id 共同构成主键，确保每个商家和商品的组合唯一
    created_at 和 updated_at 由后端自动设置，记录关联的创建和更新时间
    后端使用逻辑外键，不使用物理外键约束
*/
create table merchant_products (
    merchant_id varchar(255) not null,
    product_id varchar(255) not null,
    quantity int not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (merchant_id, product_id),
);


/*
    创建订单表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    user_id 关联用户表，表示订单所属的用户
    created_at 和 updated_at 由后端自动设置，记录订单的创建和更新时间
*/
create table orders (
    id varchar(255) not null primary key,
    user_id varchar(255) not null,
    total_amount decimal(10, 2) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建订单项表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    order_id 关联订单表，表示订单项所属的订单
    product_id 关联商品表，表示订单项对应的商品
    created_at 和 updated_at 由后端自动设置，记录订单项的创建和更新时间
*/
create table order_items (
    id varchar(255) not null primary key,
    order_id varchar(255) not null,
    product_id varchar(255) not null,
    quantity int not null,
    price decimal(10, 2) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建购物车表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    user_id 关联用户表，表示购物车所属的用户
    created_at 和 updated_at 由后端自动设置，记录购物车的创建和更新时间
*/
create table carts (
    id varchar(255) not null primary key,
    user_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建购物车项表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    cart_id 关联购物车表，表示购物车项所属的购物车
    product_id 关联商品表，表示购物车项对应的商品
    created_at 和 updated_at 由后端自动设置，记录购物车项的创建和更新时间
*/
create table cart_items (
    id varchar(255) not null primary key,
    cart_id varchar(255) not null,
    product_id varchar(255) not null,
    quantity int not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建支付表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    order_id 关联订单表，表示支付所属的订单
    created_at 和 updated_at 由后端自动设置，记录支付的创建和更新时间
    默认不会实现支付功能，直接返回支付成功，后续可以根据需要扩展支付功能
    支付方法可以是支付宝、微信支付、银行卡。
    使用10进制表示，1表示支付宝，2表示微信支付，3表示银行卡，后续可以根据需要扩展支付方法
*/
create table payments (
    id varchar(255) not null primary key,
    order_id varchar(255) not null,
    amount decimal(10, 2) not null,
    payment_method int not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建地址表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    user_id 关联用户表，表示地址所属的用户
    created_at 和 updated_at 由后端自动设置，记录地址的创建和更新时间
*/
create table addresses (
    id varchar(255) not null primary key,
    user_id varchar(255) not null,
    street varchar(255) not null,
    city varchar(255) not null,
    state varchar(255) not null,
    zip_code varchar(20) not null,
    country varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建库存表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    product_id 关联商品表，表示库存对应的商品
    created_at 和 updated_at 由后端自动设置，记录库存的创建和更新时间
*/
create table inventory (
    id varchar(255) not null primary key,
    product_id varchar(255) not null,
    quantity int not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建优惠券表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    created_at 和 updated_at 由后端自动设置，记录优惠券的创建和更新时间
*/
create table coupons (
    id varchar(255) not null primary key,
    code varchar(255) not null unique,
    discount_amount decimal(10, 2) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);

/*
    创建订单优惠券关联表
    order_id 和 coupon_id 共同构成主键，确保每个订单和优惠券的组合唯一
    created_at 和 updated_at 由后端自动设置，记录关联的创建和更新时间
    后端使用逻辑外键，不使用物理外键约束
*/
create table order_coupons (
    order_id varchar(255) not null,
    coupon_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (order_id, coupon_id),
);

/*
    创建日志表
    id 将由后端使用snowflake算法生成，保证全局唯一且有序
    user_id 关联用户表，表示日志所属的用户
    created_at 和 updated_at 由后端自动设置，记录日志的创建和更新时间
    日志类型可以是登录日志、操作日志等，根据需要扩展日志类型
    使用10进制表示，1表示登录日志，2表示操作日志，后续可以根据需要扩展日志类型
*/
create table logs (
    id varchar(255) not null primary key,
    user_id varchar(255) not null,
    log_type int not null,
    message text not null,
    created_at timestamp not null,
    updated_at timestamp not null,
);