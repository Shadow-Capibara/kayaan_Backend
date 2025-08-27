@echo off
echo Starting Kayaan Backend...
echo.

echo Step 1: Starting MySQL database...
docker-compose up -d db
echo.

echo Step 2: Waiting for database to be ready...
timeout /t 10 /nobreak > nul
echo.

echo Step 3: Running Spring Boot application...
mvnw.cmd spring-boot:run

pause
