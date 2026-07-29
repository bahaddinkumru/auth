# Auth Service

Basit bir Spring Boot kimlik doğrulama servisi.

## Çalıştırma

1. Docker Compose ile PostgreSQL veritabanını başlat:

```bash
docker compose up -d
```

2. Uygulamayı Maven ile çalıştır:

```bash
./mvnw spring-boot:run
```

Windows için:

```powershell
mvnw.cmd spring-boot:run
```

## Uygulama bilgisi

- URL: `http://localhost:8081/api/v1`
- Veritabanı: `postgresql://localhost:5432/reopiya_auth_db`
- Kullanıcı: `reopiya_user`
- Parola: `reopiya_password`

## Not

Docker Compose, proje kökünde bulunan `docker-compose.yml` dosyasını kullanır.
