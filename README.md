# Kayaan Backend (Spring Boot)

ระบบ Kayaan (Backend) สำหรับฟีเจอร์หลัก: Study Group, Study Streaks, AI Generator (mock), และไฟล์ผ่าน MinIO พร้อม SSE

## Infra (Docker Compose)

อยู่ที่ `kayaan-infra/docker-compose.yml`

- Postgres 16 → DB: `kayaan`, USER: `kayaan`, PASS: `secret`, port: 5432
- Redis 7 → port 6379
- MinIO → API 9000 / Console 9001 (user: `minioadmin`, pass: `minioadmin123`)

เริ่ม:

```bash
cd kayaan-infra && docker compose up -d
```

เปิด console MinIO ที่ `http://localhost:9001` แล้วสร้าง bucket ชื่อ `kayaan-files`

## รัน Backend

```bash
./mvnw spring-boot:run
```

ค่าคอนฟิกหลักอยู่ใน `src/main/resources/application.yml` (PostgreSQL/Redis/MinIO)

## Endpoints หลัก

- Files
  - POST `/api/files/presign` {filename, mime} → {key, uploadUrl}
  - POST `/api/groups/{id}/files` {key,name,mime,postId}
- Study Group
  - POST `/api/groups` {name, description} → 201
  - GET  `/api/groups/{id}` → group + member count
  - POST `/api/groups/{id}/join` {inviteCode}
  - POST `/api/groups/{id}/leave`
  - POST `/api/groups/{id}/posts` {type[text|link|file], content, fileKey?}
  - GET  `/api/groups/{id}/posts`
  - POST `/api/posts/{postId}/comments` {content}
- Streaks
  - POST `/api/streaks/events` {type[create_content|review|quiz_complete]}
  - GET  `/api/streaks/me`
- SSE
  - GET  `/api/stream/events?clientId=dev` (TEXT_EVENT_STREAM)
- AI (mock)
  - POST `/api/ai/jobs` {type[quiz|flashcard|note], prompt}
  - GET  `/api/ai/jobs/{id}`

หมายเหตุ: mock auth ใช้ header `X-User-Id` กำหนด user id (ค่า default = 1)

## ทดสอบอย่างย่อ (cURL)

Presign และอัปโหลดตัวอย่าง:
```bash
curl -sX POST http://localhost:8080/api/files/presign \
 -H 'Content-Type: application/json' \
 -d '{"filename":"docs/test.txt","mime":"text/plain"}'
```
จะได้ `uploadUrl` → ใช้ PUT:
```bash
curl -X PUT "<uploadUrl>" -H 'Content-Type: text/plain' --data-binary @README.md
```

สร้าง Group และโพสต์ตัวอย่าง:
```bash
curl -sX POST http://localhost:8080/api/groups \
 -H 'Content-Type: application/json' -H 'X-User-Id: 1' \
 -d '{"name":"Demo","description":"Group demo"}'
```

SSE:
```bash
curl -N http://localhost:8080/api/stream/events?clientId=dev
```

AI Job:
```bash
curl -sX POST http://localhost:8080/api/ai/jobs \
 -H 'Content-Type: application/json' -H 'X-User-Id: 1' \
 -d '{"type":"note","prompt":"make draft"}'
```

## หมายเหตุ
- ใช้ Hibernate `ddl-auto=update` ชั่วคราว (ควรย้ายเป็น Flyway ภายหลัง)
- SSE ใช้เฉพาะ broadcast event พื้นฐาน
- Presigned PUT ต้องตั้ง `Content-Type` ให้ตรงกับ mime ที่ presign

