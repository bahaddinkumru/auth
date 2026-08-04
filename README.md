# Auth Service

Basit bir Spring Boot kimlik doğrulama servisi.

## Çalıştırma

Uygulamayı ve veritabanını Docker üzerinden derleyip arka planda başlatmak için proje dizininde aşağıdaki komutu çalıştırın:

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
