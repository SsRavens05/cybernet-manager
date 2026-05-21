# Database setup

Project uses Oracle JDBC.

1. Create your Oracle schema from `C:\Users\ACER\Downloads\DBDOAN.sql`.
2. Configure the app in `src/main/resources/com/example/cybergame_management/database.properties`.

You can also override the file with environment variables:

- `CYBERGAME_DB_ENABLED=true`
- `CYBERGAME_DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1`
- `CYBERGAME_DB_USER=<oracle_user>`
- `CYBERGAME_DB_PASSWORD=<oracle_password>`

When `cybergame.db.enabled=false`, the app keeps using sample data so JavaFX screens still load without Oracle.

Current database-backed flows:

- Login reads `APP_ACCOUNT` joined with `APP_USER`.
- Customer list reads `KHACHHANG` joined with `APP_USER`.
- Customer update writes `KHACHHANG.SODIEMTICHLUY`.
- Customer delete soft-deletes by setting `KHACHHANG.IS_DELETE = 1`.

Note: the provided schema has `KHACHHANG.SODIEMTICHLUY` but no account-balance column. Until the schema adds a balance column, the database-backed customer screen shows `0đ` for account balance and reads real accumulated points from `SODIEMTICHLUY`.
