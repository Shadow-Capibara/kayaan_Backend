# Kayaan Backend

Backend API สำหรับแอปพลิเคชัน Kayaan - ระบบจัดการการเรียนรู้และศึกษากลุ่ม

## 🚀 Features

- **Manual Content Generation**: สร้าง Note, Flashcard, Quiz ด้วยตนเอง
- **AI Content Generation**: สร้างเนื้อหาด้วย AI
- **Study Groups**: จัดการกลุ่มศึกษาและแชท
- **Study Streaks**: ติดตามความต่อเนื่องในการเรียน
- **Theme Management**: จัดการธีมและ UI customization
- **Authentication & Authorization**: ระบบความปลอดภัยด้วย JWT

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Database**
- **JWT Authentication**
- **Maven**

## 📁 Project Structure

```
kayaan_Backend/
├── src/
│   ├── main/
│   │   ├── java/se499/kayaanbackend/
│   │   │   ├── Manual_Generate/     # เนื้อหาที่สร้างด้วยตนเอง
│   │   │   ├── AI_Generate/         # เนื้อหาที่สร้างด้วย AI
│   │   │   ├── Study_Group/         # กลุ่มศึกษา
│   │   │   ├── Study_Streak/        # การติดตามความต่อเนื่อง
│   │   │   ├── Theme/              # จัดการธีม
│   │   │   ├── security/           # ความปลอดภัย
│   │   │   └── common/             # ไฟล์ร่วม
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
└── docker-compose.yml
```

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven
- MySQL

### Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd kayaan_Backend
   ```

2. **Setup Database**
   ```bash
   # Start MySQL with Docker
   docker-compose up -d mysql
   ```

3. **Run the application**
   ```bash
   # Development mode
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   
   # Or build and run
   ./mvnw clean package
   java -jar target/Kayaan-Backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
   ```

### Docker Setup

```bash
# Build and run with Docker
docker build -t kayaan-backend .
docker run -p 8080:8080 kayaan-backend
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `JWT_SECRET_KEY` | JWT secret key | (auto-generated) |
| `AVATAR_UPLOAD_DIR` | Avatar upload directory | `/app/uploads/avatars` |

### Profiles

- **dev**: Development environment
- **prod**: Production environment  
- **test**: Testing environment

## 📚 API Documentation

### Authentication
- `POST /api/auth/register` - ลงทะเบียนผู้ใช้
- `POST /api/auth/login` - เข้าสู่ระบบ
- `POST /api/auth/refresh` - รีเฟรช token

### Manual Content
- `POST /api/notes` - สร้าง Note
- `POST /api/flashcards` - สร้าง Flashcard
- `POST /api/quizzes` - สร้าง Quiz

### Study Groups
- `POST /api/groups` - สร้างกลุ่มศึกษา
- `GET /api/groups/{id}` - ดูข้อมูลกลุ่ม
- `POST /api/groups/{id}/members` - เพิ่มสมาชิก

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with test profile
./mvnw test -Dspring.profiles.active=test
```

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request 