Write-Host "Starting Kayaan Backend..." -ForegroundColor Green
Write-Host ""

Write-Host "Step 1: Starting MySQL database..." -ForegroundColor Yellow
docker-compose up -d db
Write-Host ""

Write-Host "Step 2: Waiting for database to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10
Write-Host ""

Write-Host "Step 3: Running Spring Boot application..." -ForegroundColor Yellow
./mvnw spring-boot:run

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
