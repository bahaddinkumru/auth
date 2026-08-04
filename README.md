# Auth Service

Basit bir Spring Boot kimlik doğrulama servisi.

## Çalıştırma

Uygulamayı ve veritabanını Docker üzerinden derleyip arka planda başlatmak için proje dizininde aşağıdaki komutu çalıştırın:

```bash
docker compose up -d --build

url user create http://localhost/api/v1/users/register

url http://localhost/api/v1/auth/login

url http://localhost/api/v1/auth/refresh

url http://localhost/api/v1/auth/logout 

refresh ve logout için login olduktan sonra verilen refresh token gir post olarak