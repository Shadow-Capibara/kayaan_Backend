#!/bin/bash

# Setup environment variables for Kayaan Backend
echo "🔐 Setting up environment variables for Kayaan Backend"
echo "=================================================="

# Check if .env file exists
if [ -f ".env" ]; then
    echo "✅ .env file found"
    source .env
else
    echo "⚠️  .env file not found"
    echo "Please create .env file with your Supabase service key:"
    echo "SUPABASE_SERVICE_KEY=your_service_key_here"
    echo ""
    echo "Or run: cp env.example .env"
    exit 1
fi

# Check if SUPABASE_SERVICE_KEY is set
if [ -z "$SUPABASE_SERVICE_KEY" ]; then
    echo "❌ SUPABASE_SERVICE_KEY is not set"
    echo "Please add your Supabase service key to .env file"
    exit 1
fi

echo "✅ SUPABASE_SERVICE_KEY is set"
echo "✅ Environment variables loaded successfully"

# Test if application can start
echo ""
echo "🚀 Testing application startup..."
echo "Press Ctrl+C to stop after startup"

# Export for current session
export SUPABASE_SERVICE_KEY

# Start application
./mvnw spring-boot:run
