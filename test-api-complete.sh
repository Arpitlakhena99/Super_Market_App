#!/bin/bash

# ============================================================
# SUPERMARKET API - COMPLETE TESTING SCRIPT
# Platforms: Linux/Mac
# ============================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
API_URL="http://localhost:8080"
TEST_USERNAME="test_$(date +%s)"
TEST_PASSWORD="password123"

# Counter
PASS=0
FAIL=0

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}SUPERMARKET API - COMPLETE TEST SUITE${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${YELLOW}Target API: ${API_URL}${NC}"
echo ""

# Helper function to make requests
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    local expected_code=$5
    local test_name=$6

    local url="${API_URL}${endpoint}"
    local response
    local http_code

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $token")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $token" \
            -d "$data")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)

    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓ PASS${NC} - $test_name (HTTP $http_code)"
        ((PASS++))
        echo "$body"
        echo ""
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} - $test_name (Expected $expected_code, got $http_code)"
        ((FAIL++))
        echo "Response: $body"
        echo ""
        return 1
    fi
}

# =============================================================
# TEST 1: REGISTER USER
# =============================================================
echo -e "${YELLOW}[TEST 1] REGISTER USER${NC}"

register_response=$(curl -s -X POST "$API_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USERNAME\",\"password\":\"$TEST_PASSWORD\"}")

echo "$register_response"
echo ""

# =============================================================
# TEST 2: LOGIN USER
# =============================================================
echo -e "${YELLOW}[TEST 2] LOGIN USER${NC}"

login_response=$(curl -s -X POST "$API_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USERNAME\",\"password\":\"$TEST_PASSWORD\"}")

TOKEN=$(echo "$login_response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "$login_response"
echo ""

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ ERROR: Failed to get token${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Token obtained: ${TOKEN:0:20}...${NC}"
echo ""

# =============================================================
# TEST 3: GET ALL PRODUCTS (WITH TOKEN)
# =============================================================
echo -e "${YELLOW}[TEST 3] GET ALL PRODUCTS${NC}"

curl -s -X GET "$API_URL/api/products" \
    -H "Authorization: Bearer $TOKEN" | python -m json.tool 2>/dev/null || curl -s -X GET "$API_URL/api/products" -H "Authorization: Bearer $TOKEN"
echo ""

# =============================================================
# TEST 4: GET PRODUCT BY ID
# =============================================================
echo -e "${YELLOW}[TEST 4] GET PRODUCT BY ID${NC}"

curl -s -X GET "$API_URL/api/products/1" \
    -H "Authorization: Bearer $TOKEN" | python -m json.tool 2>/dev/null || curl -s -X GET "$API_URL/api/products/1" -H "Authorization: Bearer $TOKEN"
echo ""

# =============================================================
# TEST 5: GET ALL CATEGORIES
# =============================================================
echo -e "${YELLOW}[TEST 5] GET ALL CATEGORIES${NC}"

curl -s -X GET "$API_URL/api/categories" \
    -H "Authorization: Bearer $TOKEN" | python -m json.tool 2>/dev/null || curl -s -X GET "$API_URL/api/categories" -H "Authorization: Bearer $TOKEN"
echo ""

# =============================================================
# TEST 6: GET ALL CUSTOMERS
# =============================================================
echo -e "${YELLOW}[TEST 6] GET ALL CUSTOMERS${NC}"

curl -s -X GET "$API_URL/api/customers" \
    -H "Authorization: Bearer $TOKEN" | python -m json.tool 2>/dev/null || curl -s -X GET "$API_URL/api/customers" -H "Authorization: Bearer $TOKEN"
echo ""

# =============================================================
# TEST 7: CREATE CUSTOMER
# =============================================================
echo -e "${YELLOW}[TEST 7] CREATE CUSTOMER${NC}"

create_customer_response=$(curl -s -X POST "$API_URL/api/customers" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Test Customer\",\"email\":\"test_$(date +%s)@example.com\",\"phone\":\"1234567890\",\"address\":\"123 Test St\"}")

CUSTOMER_ID=$(echo "$create_customer_response" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "$create_customer_response"
echo ""

# =============================================================
# TEST 8: GET ALL ORDERS
# =============================================================
echo -e "${YELLOW}[TEST 8] GET ALL ORDERS${NC}"

curl -s -X GET "$API_URL/api/orders" \
    -H "Authorization: Bearer $TOKEN" | python -m json.tool 2>/dev/null || curl -s -X GET "$API_URL/api/orders" -H "Authorization: Bearer $TOKEN"
echo ""

# =============================================================
# TEST 9: CREATE ORDER
# =============================================================
echo -e "${YELLOW}[TEST 9] CREATE ORDER${NC}"

create_order_response=$(curl -s -X POST "$API_URL/api/orders" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"customerId\":$CUSTOMER_ID,\"items\":[{\"productId\":1,\"quantity\":2,\"unitPrice\":50.00}]}")

echo "$create_order_response"
echo ""

# =============================================================
# TEST 10: AUTHORIZATION - TRY TO CREATE PRODUCT (SHOULD FAIL)
# =============================================================
echo -e "${YELLOW}[TEST 10] TRY TO CREATE PRODUCT - SHOULD FAIL (403)${NC}"

create_product_response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL/api/products" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Test Product\",\"sku\":\"TST001\",\"price\":100.00,\"stock\":50}")

http_code=$(echo "$create_product_response" | tail -n1)
body=$(echo "$create_product_response" | head -n-1)

if [ "$http_code" = "403" ]; then
    echo -e "${GREEN}✓ PASS${NC} - Correctly rejected (HTTP $http_code)"
    ((PASS++))
else
    echo -e "${RED}✗ FAIL${NC} - Expected 403, got $http_code"
    ((FAIL++))
fi
echo "Response: $body"
echo ""

# =============================================================
# TEST 11: AUTHENTICATION - NO TOKEN (SHOULD FAIL)
# =============================================================
echo -e "${YELLOW}[TEST 11] REQUEST WITHOUT TOKEN - SHOULD FAIL (401)${NC}"

no_token_response=$(curl -s -w "\n%{http_code}" -X GET "$API_URL/api/products")

http_code=$(echo "$no_token_response" | tail -n1)
body=$(echo "$no_token_response" | head -n-1)

if [ "$http_code" = "401" ]; then
    echo -e "${GREEN}✓ PASS${NC} - Correctly rejected (HTTP $http_code)"
    ((PASS++))
else
    echo -e "${RED}✗ FAIL${NC} - Expected 401, got $http_code"
    ((FAIL++))
fi
echo "Response: $body"
echo ""

# =============================================================
# TEST 12: AUTHENTICATION - INVALID TOKEN (SHOULD FAIL)
# =============================================================
echo -e "${YELLOW}[TEST 12] REQUEST WITH INVALID TOKEN - SHOULD FAIL (401)${NC}"

invalid_token_response=$(curl -s -w "\n%{http_code}" -X GET "$API_URL/api/products" \
    -H "Authorization: Bearer invalid.token.here")

http_code=$(echo "$invalid_token_response" | tail -n1)
body=$(echo "$invalid_token_response" | head -n-1)

if [ "$http_code" = "401" ]; then
    echo -e "${GREEN}✓ PASS${NC} - Correctly rejected (HTTP $http_code)"
    ((PASS++))
else
    echo -e "${RED}✗ FAIL${NC} - Expected 401, got $http_code"
    ((FAIL++))
fi
echo "Response: $body"
echo ""

# =============================================================
# TEST 13: INVALID CREDENTIALS (SHOULD FAIL)
# =============================================================
echo -e "${YELLOW}[TEST 13] LOGIN WITH INVALID PASSWORD - SHOULD FAIL (401)${NC}"

invalid_login_response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USERNAME\",\"password\":\"wrongpassword\"}")

http_code=$(echo "$invalid_login_response" | tail -n1)
body=$(echo "$invalid_login_response" | head -n-1)

if [ "$http_code" = "401" ]; then
    echo -e "${GREEN}✓ PASS${NC} - Correctly rejected (HTTP $http_code)"
    ((PASS++))
else
    echo -e "${RED}✗ FAIL${NC} - Expected 401, got $http_code"
    ((FAIL++))
fi
echo "Response: $body"
echo ""

# =============================================================
# SUMMARY
# =============================================================
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}TEST SUMMARY${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ PASSED: $PASS${NC}"
echo -e "${RED}✗ FAILED: $FAIL${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}✓ ALL TESTS PASSED!${NC}"
    exit 0
else
    echo -e "${RED}✗ SOME TESTS FAILED${NC}"
    exit 1
fi

