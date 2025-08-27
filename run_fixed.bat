@echo off
echo ========================================
echo Kayaan Backend - Fixed Compilation
echo ========================================
echo.

echo Step 1: Testing compilation...
echo Running: mvnw.cmd clean compile
echo.

REM Use call to execute the command
call mvnw.cmd clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful!
    echo.
    echo Step 2: Starting Spring Boot application...
    echo Running: mvnw.cmd spring-boot:run
    echo.
    call mvnw.cmd spring-boot:run
) else (
    echo.
    echo ❌ Compilation failed!
    echo.
    echo Common solutions:
    echo 1. Make sure Java 17+ is installed
    echo 2. Make sure Docker is running for database
    echo 3. Check if port 8080 is available
    echo.
    echo Press any key to exit...
    pause > nul
)

echo.
echo Press any key to exit...
pause > nul
