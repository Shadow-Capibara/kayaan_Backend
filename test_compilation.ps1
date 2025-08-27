Write-Host "Testing compilation..." -ForegroundColor Green
Write-Host ""

Write-Host "Step 1: Clean and compile..." -ForegroundColor Yellow
$compileResult = & ./mvnw clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Compilation successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Step 2: Starting Spring Boot application..." -ForegroundColor Yellow
    & ./mvnw spring-boot:run
} else {
    Write-Host ""
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    Write-Host "Please check the error messages above."
}

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
