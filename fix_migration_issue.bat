@echo off
echo Fixing Flyway Migration Issue...
echo ==================================

echo.
echo Step 1: Stopping any running containers...
docker-compose down

echo.
echo Step 2: Starting fresh database...
docker-compose up -d db

echo.
echo Step 3: Waiting for database to be ready...
timeout /t 15 /nobreak > nul

echo.
echo Step 4: Migration issue temporarily disabled...
echo Flyway is now disabled in application.yml
echo.

echo Step 5: Testing application startup...
echo Running: mvnw.cmd spring-boot:run
echo.

call mvnw.cmd spring-boot:run

echo.
echo If the application starts successfully:
echo 1. Stop the application (Ctrl+C)
echo 2. Re-enable Flyway in application.yml (change enabled: false to enabled: true)
echo 3. Run the application again
echo.

pause
