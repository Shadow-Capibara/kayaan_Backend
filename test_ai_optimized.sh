#!/bin/bash

# Test script for optimized AI service
# ทดสอบการทำงานของ AI service ที่ปรับปรุงแล้ว

echo "🧪 Testing Optimized AI Service..."
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080"

# Test data
TEST_PROMPT="Create a simple flashcard about Java programming"
TEST_FORMAT="flashcard"

echo -e "${BLUE}1. Testing AI Service Configuration...${NC}"

# Check if service is running
if curl -s "$BASE_URL/actuator/health" > /dev/null; then
    echo -e "${GREEN}✅ Service is running${NC}"
else
    echo -e "${RED}❌ Service is not running. Please start the application first.${NC}"
    exit 1
fi

echo -e "${BLUE}2. Testing OpenAI Configuration...${NC}"

# Check OpenAI configuration
if [ -z "$OPENAI_API_KEY" ]; then
    echo -e "${YELLOW}⚠️  OPENAI_API_KEY not set. Please set it first:${NC}"
    echo "export OPENAI_API_KEY='your-api-key-here'"
    echo ""
    echo -e "${BLUE}Or create a .env file:${NC}"
    echo "cp env.example .env"
    echo "# Edit .env with your actual API key"
    echo ""
else
    echo -e "${GREEN}✅ OPENAI_API_KEY is set${NC}"
fi

echo -e "${BLUE}3. Testing AI Generation Endpoint...${NC}"

# Test AI generation (without auth for now)
echo -e "${YELLOW}Testing with prompt: '$TEST_PROMPT'${NC}"
echo -e "${YELLOW}Output format: $TEST_FORMAT${NC}"

# Create test request
TEST_REQUEST=$(cat <<EOF
{
  "prompt": "$TEST_PROMPT",
  "outputFormat": "$TEST_FORMAT",
  "additionalContext": "Keep it simple and educational"
}
EOF
)

echo -e "${BLUE}Request payload:${NC}"
echo "$TEST_REQUEST" | jq '.' 2>/dev/null || echo "$TEST_REQUEST"

echo ""
echo -e "${BLUE}4. Making API call...${NC}"

# Make the API call
RESPONSE=$(curl -s -X POST "$BASE_URL/api/ai/generate" \
  -H "Content-Type: application/json" \
  -d "$TEST_REQUEST" \
  -w "\nHTTP_STATUS:%{http_code}")

# Extract HTTP status and response body
HTTP_STATUS=$(echo "$RESPONSE" | tail -n1 | sed 's/.*HTTP_STATUS://')
RESPONSE_BODY=$(echo "$RESPONSE" | head -n -1)

echo -e "${BLUE}Response Status: $HTTP_STATUS${NC}"

if [ "$HTTP_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ API call successful!${NC}"
    echo ""
    echo -e "${BLUE}Response:${NC}"
    echo "$RESPONSE_BODY" | jq '.' 2>/dev/null || echo "$RESPONSE_BODY"
    
    # Validate JSON response
    if echo "$RESPONSE_BODY" | jq empty 2>/dev/null; then
        echo -e "${GREEN}✅ Response is valid JSON${NC}"
    else
        echo -e "${RED}❌ Response is not valid JSON${NC}"
    fi
    
    # Check response length
    RESPONSE_LENGTH=$(echo "$RESPONSE_BODY" | wc -c)
    echo -e "${BLUE}Response length: ${RESPONSE_LENGTH} characters${NC}"
    
    if [ "$RESPONSE_LENGTH" -lt 1000 ]; then
        echo -e "${GREEN}✅ Response is concise (under 1000 chars)${NC}"
    else
        echo -e "${YELLOW}⚠️  Response is longer than expected${NC}"
    fi
    
else
    echo -e "${RED}❌ API call failed with status: $HTTP_STATUS${NC}"
    echo ""
    echo -e "${BLUE}Error response:${NC}"
    echo "$RESPONSE_BODY"
    
    # Common error handling
    case $HTTP_STATUS in
        401)
            echo -e "${YELLOW}💡 This might be an authentication issue. Try with a valid JWT token.${NC}"
            ;;
        500)
            echo -e "${YELLOW}💡 This might be a server error. Check the application logs.${NC}"
            ;;
        503)
            echo -e "${YELLOW}💡 Service might be unavailable. Check if OpenAI API is accessible.${NC}"
            ;;
    esac
fi

echo ""
echo -e "${BLUE}5. Performance Metrics...${NC}"

# Check if metrics endpoint is available
if curl -s "$BASE_URL/actuator/metrics" > /dev/null; then
    echo -e "${GREEN}✅ Metrics endpoint available${NC}"
    
    # Get AI generation metrics if available
    AI_METRICS=$(curl -s "$BASE_URL/actuator/metrics/ai.generation.requests" 2>/dev/null)
    if [ ! -z "$AI_METRICS" ]; then
        echo -e "${BLUE}AI Generation Metrics:${NC}"
        echo "$AI_METRICS" | jq '.' 2>/dev/null || echo "$AI_METRICS"
    fi
else
    echo -e "${YELLOW}⚠️  Metrics endpoint not available${NC}"
fi

echo ""
echo -e "${BLUE}6. Configuration Summary...${NC}"

echo -e "${BLUE}Current AI Configuration:${NC}"
echo "Model: gpt-5-nano"
echo "Max Tokens: 256"
echo "Temperature: 0.1"
echo "Timeout: 30 seconds"
echo "System Prompt: 'JSON only. Be concise.'"

echo ""
echo -e "${GREEN}🎉 Test completed!${NC}"

if [ "$HTTP_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ AI service is working correctly with optimized configuration${NC}"
    echo ""
    echo -e "${BLUE}Next steps:${NC}"
    echo "1. Set your OPENAI_API_KEY if not already set"
    echo "2. Test with different output formats (quiz, note, summary)"
    echo "3. Monitor token usage in OpenAI Dashboard"
    echo "4. Check application logs for detailed information"
else
    echo -e "${RED}❌ Some tests failed. Please check the errors above.${NC}"
    echo ""
    echo -e "${BLUE}Troubleshooting:${NC}"
    echo "1. Ensure the application is running"
    echo "2. Check if OPENAI_API_KEY is set correctly"
    echo "3. Verify OpenAI API is accessible"
    echo "4. Check application logs for errors"
fi

echo ""
echo -e "${BLUE}For more information, see: README_AI_OPTIMIZATION.md${NC}"
