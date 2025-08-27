@echo off
echo Testing compilation...
echo.

echo Step 1: Clean and compile...
call mvnw.cmd clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful!
    echo.
    echo Step 2: Starting Spring Boot application...
    call mvnw.cmd spring-boot:run
) else (
    echo.
    echo ❌ Compilation failed!
    echo Please check the error messages above.
)

pause
