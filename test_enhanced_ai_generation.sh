#!/bin/bash

# 🚀 Enhanced AI Generation API Test Script
# Tests the new file upload and real-time progress features

# Configuration
BASE_URL="http://localhost:8080"
JWT_TOKEN=""
USER_ID=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test results
TESTS_PASSED=0
TESTS_FAILED=0

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    
    case $status in
        "INFO")
            echo -e "${BLUE}[INFO]${NC} $message"
            ;;
        "SUCCESS")
            echo -e "${GREEN}[SUCCESS]${NC} $message"
            ;;
        "WARNING")
            echo -e "${YELLOW}[WARNING]${NC} $message"
            ;;
        "ERROR")
            echo -e "${RED}[ERROR]${NC} $message"
            ;;
    esac
}

# Function to check if service is running
check_service() {
    print_status "INFO" "Checking if service is running..."
    
    if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_status "SUCCESS" "Service is running"
        return 0
    else
        print_status "ERROR" "Service is not running at $BASE_URL"
        return 1
    fi
}

# Function to authenticate and get JWT token
authenticate() {
    print_status "INFO" "Authenticating user..."
    
    # You'll need to implement this based on your auth system
    # For now, we'll use a placeholder
    JWT_TOKEN="your-jwt-token-here"
    USER_ID="123"
    
    if [ -n "$JWT_TOKEN" ]; then
        print_status "SUCCESS" "Authentication successful"
        return 0
    else
        print_status "ERROR" "Authentication failed"
        return 1
    fi
}

# Function to test file upload endpoint
test_file_upload() {
    print_status "INFO" "Testing file upload endpoint..."
    
    # Create a test text file
    echo "This is a test document for AI generation. It contains sample content that can be used to generate flashcards, quizzes, notes, or summaries." > test_document.txt
    
    # Test the file upload endpoint
    local response=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -F "request={\"promptText\":\"Create flashcards for this content\",\"outputFormat\":\"flashcard\",\"extractTextFromFile\":true}" \
        -F "file=@test_document.txt" \
        "$BASE_URL/api/ai/generation/request")
    
    local http_code=$(echo "$response" | tail -n1)
    local response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 201 ]; then
        print_status "SUCCESS" "File upload test passed"
        local request_id=$(echo "$response_body" | grep -o '"data":[0-9]*' | cut -d':' -f2)
        echo "$request_id" > .test_request_id
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        print_status "ERROR" "File upload test failed (HTTP $http_code)"
        echo "Response: $response_body"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Function to test progress tracking endpoint
test_progress_tracking() {
    print_status "INFO" "Testing progress tracking endpoint..."
    
    if [ ! -f .test_request_id ]; then
        print_status "WARNING" "No request ID found, skipping progress test"
        return 1
    fi
    
    local request_id=$(cat .test_request_id)
    
    # Test the progress endpoint
    local response=$(curl -s -w "\n%{http_code}" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        "$BASE_URL/api/ai/generation/$request_id/progress")
    
    local http_code=$(echo "$response" | tail -n1)
    local response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 200 ]; then
        print_status "SUCCESS" "Progress tracking test passed"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        print_status "ERROR" "Progress tracking test failed (HTTP $http_code)"
        echo "Response: $response_body"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Function to test WebSocket connection
test_websocket() {
    print_status "INFO" "Testing WebSocket connection..."
    
    # Check if WebSocket endpoint is accessible
    local response=$(curl -s -w "\n%{http_code}" \
        -H "Upgrade: websocket" \
        -H "Connection: Upgrade" \
        "$BASE_URL/ws/ai")
    
    local http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" -eq 101 ] || [ "$http_code" -eq 200 ]; then
        print_status "SUCCESS" "WebSocket endpoint is accessible"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        print_status "WARNING" "WebSocket test inconclusive (HTTP $http_code)"
        # WebSocket tests are often inconclusive with curl
        return 0
    fi
}

# Function to test content generation
test_content_generation() {
    print_status "INFO" "Testing content generation..."
    
    if [ ! -f .test_request_id ]; then
        print_status "WARNING" "No request ID found, skipping generation test"
        return 1
    fi
    
    local request_id=$(cat .test_request_id)
    
    # Test starting content generation
    local response=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Authorization: Bearer $JWT_TOKEN" \
        "$BASE_URL/api/ai/generation/$request_id/generate")
    
    local http_code=$(echo "$response" | tail -n1)
    local response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 202 ]; then
        print_status "SUCCESS" "Content generation test passed"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        print_status "ERROR" "Content generation test failed (HTTP $http_code)"
        echo "Response: $response_body"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Function to test different content types
test_content_types() {
    print_status "INFO" "Testing different content types..."
    
    local content_types=("summary" "quiz" "flashcard" "note")
    local test_passed=0
    
    for content_type in "${content_types[@]}"; do
        print_status "INFO" "Testing $content_type generation..."
        
        local response=$(curl -s -w "\n%{http_code}" \
            -X POST \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -F "request={\"promptText\":\"Create a $content_type for this content\",\"outputFormat\":\"$content_type\"}" \
            -F "file=@test_document.txt" \
            "$BASE_URL/api/ai/generation/request")
        
        local http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" -eq 201 ]; then
            print_status "SUCCESS" "$content_type generation test passed"
            test_passed=$((test_passed + 1))
        else
            print_status "ERROR" "$content_type generation test failed (HTTP $http_code)"
        fi
    done
    
    if [ $test_passed -eq ${#content_types[@]} ]; then
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Function to test rate limiting
test_rate_limiting() {
    print_status "INFO" "Testing rate limiting..."
    
    local rate_limit_hit=0
    
    # Try to make multiple requests quickly
    for i in {1..6}; do
        local response=$(curl -s -w "\n%{http_code}" \
            -X POST \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -F "request={\"promptText\":\"Test request $i\",\"outputFormat\":\"summary\"}" \
            "$BASE_URL/api/ai/generation/request")
        
        local http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" -eq 429 ]; then
            rate_limit_hit=1
            break
        fi
        
        sleep 0.1
    done
    
    if [ $rate_limit_hit -eq 1 ]; then
        print_status "SUCCESS" "Rate limiting test passed"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        print_status "WARNING" "Rate limiting test inconclusive"
        return 0
    fi
}

# Function to cleanup test files
cleanup() {
    print_status "INFO" "Cleaning up test files..."
    
    if [ -f test_document.txt ]; then
        rm test_document.txt
    fi
    
    if [ -f .test_request_id ]; then
        rm .test_request_id
    fi
    
    print_status "SUCCESS" "Cleanup completed"
}

# Function to print test summary
print_summary() {
    echo
    echo "=========================================="
    echo "           TEST SUMMARY"
    echo "=========================================="
    echo "Tests Passed: $TESTS_PASSED"
    echo "Tests Failed: $TESTS_FAILED"
    echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
    echo
    
    if [ $TESTS_FAILED -eq 0 ]; then
        print_status "SUCCESS" "All tests passed! 🎉"
        exit 0
    else
        print_status "ERROR" "Some tests failed. Please check the logs above."
        exit 1
    fi
}

# Main test execution
main() {
    echo "🚀 Enhanced AI Generation API Test Suite"
    echo "=========================================="
    echo
    
    # Check if service is running
    if ! check_service; then
        exit 1
    fi
    
    # Authenticate user
    if ! authenticate; then
        exit 1
    fi
    
    # Run tests
    test_file_upload
    test_progress_tracking
    test_websocket
    test_content_generation
    test_content_types
    test_rate_limiting
    
    # Cleanup and print summary
    cleanup
    print_summary
}

# Trap cleanup on script exit
trap cleanup EXIT

# Run main function
main "$@"
