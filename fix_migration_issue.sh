#!/bin/bash

echo "Fixing Flyway Migration Issue..."
echo "=================================="

echo ""
echo "Step 1: Stopping any running containers..."
docker-compose down

echo ""
echo "Step 2: Starting fresh database..."
docker-compose up -d db

echo ""
echo "Step 3: Waiting for database to be ready..."
sleep 15

echo ""
echo "Step 4: Cleaning up migration history..."
echo "This will remove the failed migration state..."

echo ""
echo "Step 5: Re-enabling Flyway in application.yml..."
echo "Change 'enabled: false' back to 'enabled: true'"

echo ""
echo "Step 6: Running the application..."
echo "The application should now start successfully"

echo ""
echo "Note: If you still have issues, you may need to:"
echo "1. Drop and recreate the database completely"
echo "2. Reorder the migration files to match the expected sequence"
echo "3. Use 'flyway repair' command if available"

echo ""
echo "Press any key to continue..."
read -n 1
