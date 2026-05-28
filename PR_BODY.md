Title: feat: expand orders/cart schema, add coupons/shipping, inventory & auto-pay

Summary:
- Expand `orders`, `order_items`, `cart_items` schema and `DatabaseInitializer` to include subtotal, tax, shipping, currency, status, unit_price, sku, discount fields.
- Implemented order checkout enhancements in `InMemoryStore`: tax/shipping calculation, coupon application, inventory availability check and deduction, auto-pay support and payments persistence.
- Frontend: `Cart.vue` adds shipping address selection and coupon input; order payload now includes `shippingAddressId` and `couponCodes`.
- Added a unit test `InMemoryStoreOrderTest` for checkout flow (inventory deduction + auto-pay).

Files changed (high level):
- backend/src/main/kotlin/com/example/ecommerce/config/DatabaseInitializer.kt
- backend/src/main/kotlin/com/example/ecommerce/service/InMemoryStore.kt
- backend/src/main/kotlin/com/example/ecommerce/model/Models.kt
- backend/src/test/kotlin/com/example/ecommerce/service/InMemoryStoreOrderTest.kt
- frontend/src/pages/user/Cart.vue

Notes & Migration:
- `DatabaseInitializer` updated; on a fresh DB the new tables/columns will be created automatically. For existing databases run the provided schema migration or update your DB before starting the app to avoid seed import failures.
- Seed data updated to match new columns.

Test & Status:
- I ran `gradle clean test` locally: compilation succeeds. Most existing tests pass.
- The added test runs but currently fails in the workspace test run because the test asserts DB-backed payment persistence; this may require an environment with the payments table present for integration-style verification.

Review & Follow-ups:
- Please review business logic in `InMemoryStore.kt` (inventory deduction, coupon application, payment handling).
- Confirm DB migration strategy for production (Flyway/Liquibase recommended).
- I can either: 1) fix/adjust the failing test to be purely in-memory, 2) prepare Flyway migrations, or 3) split the PR into smaller commits. Tell me which you'd prefer.

Create PR:
- Branch: `feature/expanded-orders-cart` (already pushed)
- GitHub new-PR URL: https://github.com/LuckierLove/ECommerce/pull/new/feature/expanded-orders-cart

If you want me to create the PR directly I can run `gh pr create` but the environment needs GitHub CLI auth (run `gh auth login` or provide a `GH_TOKEN`).
