# Spring Boot JWT 登录项目

功能：用户注册、登录、BCrypt 密码加密、JWT 鉴权、MySQL 持久化、当前用户查询。

## 环境

- Java 17
- Maven 3.9+
- MySQL 8

## 启动

先设置环境变量。不要把真实密钥写入配置文件。

Windows PowerShell：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="数据库密码"
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET=[Convert]::ToBase64String($bytes)
mvn spring-boot:run
```

Linux/macOS：

```bash
export DB_USERNAME=root
export DB_PASSWORD='数据库密码'
export JWT_SECRET="$(openssl rand -base64 32)"
mvn spring-boot:run
```

## 接口

- `POST /api/auth/register`：`{"username":"demo","email":"demo@example.com","password":"password123"}`
- `POST /api/auth/login`：`{"username":"demo","password":"password123"}`
- `GET /api/users/me`：请求头携带 `Authorization: Bearer <token>`

数据库只保存 BCrypt 哈希。JWT 密钥只从环境变量读取。生产环境应使用 HTTPS，并将 `ddl-auto` 改为 `validate`。
