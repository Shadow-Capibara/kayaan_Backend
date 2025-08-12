#!/bin/bash

echo "🛠️  รัน Flyway Repair และ Start Application (ทางเลือก A)"
echo "======================================================"

# ตั้งค่าสี
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info "ขั้นตอนที่ 1: รัน Flyway Repair"
./mvnw -Dflyway.cleanDisabled=false flyway:repair

if [ $? -eq 0 ]; then
    print_success "Flyway Repair สำเร็จ"
else
    echo "❌ Flyway Repair ล้มเหลว"
    exit 1
fi

print_info "ขั้นตอนที่ 2: รัน Application"
./mvnw spring-boot:run
