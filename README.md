# Auth Service

Basit bir Spring Boot kimlik doğrulama servisi.

## Çalıştırma

Uygulamayı ve veritabanını Docker üzerinden derleyip arka planda başlatmak için proje dizininde aşağıdaki komutu çalıştırın:

```bash
docker compose up -d --build

url user create http://localhost/api/v1/users/register

login http://localhost/api/v1/auth/login

refresh http://localhost/api/v1/auth/refresh

logout http://localhost/api/v1/auth/logout 

refresh ve logout için post olarak login olduğumuzda bize verilen refresh tokenı giriyoruzS