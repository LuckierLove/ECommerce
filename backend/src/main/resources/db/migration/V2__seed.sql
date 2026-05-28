-- Seed data migration converted from data.sql

INSERT IGNORE INTO roles (id, name, created_at, updated_at) VALUES
('role-admin', 'admin', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-user', 'user', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-merchant', 'merchant', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO permissions (id, name, created_at, updated_at) VALUES
('perm-users-read', 'users.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-users-write', 'users.write', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-products-read', 'products.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-orders-read', 'orders.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-logs-read', 'logs.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-merchants-read', 'merchants.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('perm-coupons-read', 'coupons.read', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

-- Plaintext passwords for seeded users (for testing only):
-- admin    => admin123
-- alice    => alice123
-- merchant1=> merchant123
-- Note: 'bob' password plaintext not present in codebase; hash kept as-is.
INSERT IGNORE INTO users (id, username, email, password, created_at, updated_at) VALUES
('user-admin', 'admin', 'admin@example.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('user-alice', 'alice', 'alice@example.com', '4e40e8ffe0ee32fa53e139147ed559229a5930f89c2204706fc174beb36210b3', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('user-merchant', 'merchant1', 'merchant@example.com', '0e3183c45e8ef9bc95fc8a2dc83f040149d2c7193312aa0740da9c0d50b1f439', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('user-bob', 'bob', 'bob@example.com', '8d059c3640b97180dd2ee453e20d34ab0cb0f2eccbe87d01915a8e578a202b11', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

-- bob      => bob123

INSERT IGNORE INTO user_roles (user_id, role_id, created_at, updated_at) VALUES
('user-admin', 'role-admin', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('user-alice', 'role-user', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('user-merchant', 'role-merchant', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO role_permissions (role_id, permission_id, created_at, updated_at) VALUES
('role-admin', 'perm-users-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-users-write', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-products-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-orders-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-logs-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-merchants-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-admin', 'perm-coupons-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-user', 'perm-products-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-user', 'perm-orders-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-user', 'perm-coupons-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-merchant', 'perm-products-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-merchant', 'perm-orders-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('role-merchant', 'perm-merchants-read', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO merchants (id, name, created_at, updated_at) VALUES
('merchant-1', '星火商贸', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-2', '云集数码', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-3', '南风优选', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO merchant_users (merchant_id, user_id, created_at, updated_at) VALUES
('1014', '1011', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO products (id, name, description, price, created_at, updated_at) VALUES
('product-1', '轻薄笔记本', '14英寸办公本', 4999.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('product-2', '无线鼠标', '静音便携', 79.90, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('product-3', '机械键盘', 'RGB背光键盘', 299.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('product-4', '蓝牙耳机', '降噪通勤耳机', 359.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('product-5', '显示器', '27英寸2K显示器', 1299.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO merchant_products (merchant_id, product_id, quantity, created_at, updated_at) VALUES
('merchant-1', 'product-1', 20, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-2', 'product-2', 150, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-3', 'product-3', 80, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-1', 'product-4', 60, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('merchant-2', 'product-5', 35, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO inventory (id, product_id, quantity, created_at, updated_at) VALUES
('inventory-1', 'product-1', 20, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('inventory-2', 'product-2', 150, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('inventory-3', 'product-3', 80, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('inventory-4', 'product-4', 60, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('inventory-5', 'product-5', 35, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO carts (id, user_id, created_at, updated_at) VALUES
('cart-alice', 'user-alice', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('cart-merchant', 'user-merchant', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO cart_items (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
('cart-item-1', 'cart-alice', 'product-1', 1, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('cart-item-2', 'cart-alice', 'product-2', 2, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO orders (id, user_id, total_amount, subtotal, tax_amount, shipping_cost, currency, status, payment_status, items_count, created_at, updated_at) VALUES
('order-1', 'user-alice', 5278.90, 5278.90, 0.00, 0.00, 'CNY', 'CREATED', 'PAID', 3, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO order_items (id, order_id, product_id, quantity, price, unit_price, subtotal, created_at, updated_at) VALUES
('order-item-1', 'order-1', 'product-1', 1, 4999.00, 4999.00, 4999.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('order-item-2', 'order-1', 'product-2', 2, 79.90, 79.90, 159.80, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO payments (id, order_id, amount, payment_method, created_at, updated_at) VALUES
('payment-1', 'order-1', 5278.90, 1, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO addresses (id, user_id, street, city, state, zip_code, country, created_at, updated_at) VALUES
('address-1', 'user-alice', '中关村大街1号', '北京', '北京', '100080', '中国', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('address-2', 'user-merchant', '滨江路88号', '杭州', '浙江', '310000', '中国', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO coupons (id, code, discount_amount, created_at, updated_at) VALUES
('coupon-1', 'WELCOME10', 10.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('coupon-2', 'VIP50', 50.00, '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO order_coupons (order_id, coupon_id, created_at, updated_at) VALUES
('order-1', 'coupon-1', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO logs (id, user_id, log_type, message, created_at, updated_at) VALUES
('log-1', 'user-admin', 1, '管理员登录', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('log-2', 'user-alice', 2, '浏览商品列表', '2026-05-27 10:00:00', '2026-05-27 10:00:00'),
('log-3', 'user-merchant', 2, '发布商品活动', '2026-05-27 10:00:00', '2026-05-27 10:00:00');

INSERT IGNORE INTO merchant_notifications (id, merchant_id, order_id, user_id, message, created_at) VALUES
('notice-1', '1014', '1031', '1010', '有新的订单提交，订单号 1031，金额 5278.90，商品数量 3', '2026-05-27 10:00:00'),
('notice-2', '1014', '1031', '1010', '有新的订单提交，订单号 1031，金额 5278.90，商品数量 3', '2026-05-27 10:00:00');
