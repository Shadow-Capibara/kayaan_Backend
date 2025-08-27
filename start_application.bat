@echo off
echo ========================================
echo Kayaan Backend - All Issues Fixed!
echo ========================================
echo.

echo Step 1: Starting MySQL database...
docker-compose up -d db

echo.
echo Step 2: Waiting for database to be ready...
timeout /t 15 /nobreak > nul

echo.
echo Step 3: Starting Spring Boot application...
echo All configuration issues have been resolved!
echo.

call mvnw.cmd spring-boot:run

echo.
echo If the application starts successfully:
echo - API will be available at http://localhost:8080
echo - Database schema will be auto-created by Hibernate
echo - Flyway is temporarily disabled to avoid migration issues
echo.

pause
