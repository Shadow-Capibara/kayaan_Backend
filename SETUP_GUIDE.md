# Kayaan Backend Setup Guide

## Prerequisites
- Java 17 or higher
- Docker and Docker Compose
- Maven (optional, project includes Maven Wrapper)

## Quick Start

### Option 1: Using Batch File (Windows)
```cmd
run_spring_boot.bat
```

### Option 2: Using PowerShell Script
```powershell
.\run_spring_boot.ps1
```

### Option 3: Manual Steps

#### Step 1: Start Database
```bash
docker-compose up -d db
```

#### Step 2: Wait for Database
Wait 10-15 seconds for MySQL to be ready.

#### Step 3: Run Application
```bash
# Using Maven Wrapper
mvnw.cmd spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

## Configuration

### Environment Variables
Copy `env.example` to `.env` and update:
```bash
cp env.example .env
```

Required variables:
- `OPENAI_API_KEY`: Your OpenAI API key

### Database Configuration
- Host: localhost
- Port: 3307
- Database: kayaan_db
- Username: root
- Password: password

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure Docker is running
   - Check if MySQL container is up: `docker ps`
   - Verify port 3307 is not blocked

2. **Port Already in Use**
   - Change port in `application.yml`
   - Kill process using the port

3. **Maven Dependencies**
   - Run: `mvnw.cmd clean install`

4. **Flyway Migration Issues**
   - Check database connection
   - Verify migration files in `src/main/resources/db/migration`

### Logs
Application logs are configured with DEBUG level. Check console output for detailed information.

## API Endpoints
Once running, the application will be available at:
- Main API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Development
- Hot reload is enabled
- Database auto-update is enabled
- CORS is configured for localhost:5173
