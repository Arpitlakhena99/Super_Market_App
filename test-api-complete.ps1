# ============================================================
# SUPERMARKET API - COMPLETE TESTING SCRIPT (PowerShell)
# Platforms: Windows
# ============================================================

# Configuration
$API_URL = "http://localhost:8080"
$TEST_USERNAME = "test_$(Get-Date -UFormat %s)"
$TEST_PASSWORD = "password123"
$PASS = 0
$FAIL = 0

# Colors
$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Blue = "Blue"

Write-Host "========================================" -ForegroundColor $Blue
Write-Host "SUPERMARKET API - COMPLETE TEST SUITE" -ForegroundColor $Blue
Write-Host "========================================" -ForegroundColor $Blue
Write-Host "Target API: $API_URL" -ForegroundColor $Yellow
Write-Host ""

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Endpoint,
        [string]$Data,
        [string]$Token,
        [int]$ExpectedCode,
        [string]$TestName
    )

    $url = "$API_URL$Endpoint"
    $headers = @{"Content-Type" = "application/json"}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    try {
        if ($Data) {
            $response = Invoke-WebRequest -Uri $url -Method $Method -Headers $headers -Body $Data
        } else {
            $response = Invoke-WebRequest -Uri $url -Method $Method -Headers $headers
        }
        $httpCode = $response.StatusCode
        $body = $response.Content
    } catch {
        $httpCode = $_.Exception.Response.StatusCode.value__
        $body = $_.Exception.Response | ConvertTo-Json
    }

    if ($httpCode -eq $ExpectedCode) {
        Write-Host "✓ PASS - $TestName (HTTP $httpCode)" -ForegroundColor $Green
        $script:PASS++
    } else {
        Write-Host "✗ FAIL - $TestName (Expected $ExpectedCode, got $httpCode)" -ForegroundColor $Red
        $script:FAIL++
    }
    Write-Host "Response: $body"
    Write-Host ""
    return $body
}

# =============================================================
# TEST 1: REGISTER USER
# =============================================================
Write-Host "[TEST 1] REGISTER USER" -ForegroundColor $Yellow

$registerData = @{
    username = $TEST_USERNAME
    password = $TEST_PASSWORD
} | ConvertTo-Json

$registerResponse = Invoke-WebRequest -Uri "$API_URL/api/auth/register" -Method POST `
    -Headers @{"Content-Type" = "application/json"} -Body $registerData

Write-Host ($registerResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 2: LOGIN USER
# =============================================================
Write-Host "[TEST 2] LOGIN USER" -ForegroundColor $Yellow

$loginData = @{
    username = $TEST_USERNAME
    password = $TEST_PASSWORD
} | ConvertTo-Json

$loginResponse = Invoke-WebRequest -Uri "$API_URL/api/auth/login" -Method POST `
    -Headers @{"Content-Type" = "application/json"} -Body $loginData

$loginJson = $loginResponse.Content | ConvertFrom-Json
$TOKEN = $loginJson.token

Write-Host ($loginJson | ConvertTo-Json -Depth 10)
Write-Host ""

if (-not $TOKEN) {
    Write-Host "✗ ERROR: Failed to get token" -ForegroundColor $Red
    exit 1
}

Write-Host "✓ Token obtained: $($TOKEN.Substring(0, 20))..." -ForegroundColor $Green
Write-Host ""

# =============================================================
# TEST 3: GET ALL PRODUCTS (WITH TOKEN)
# =============================================================
Write-Host "[TEST 3] GET ALL PRODUCTS" -ForegroundColor $Yellow

$productsResponse = Invoke-WebRequest -Uri "$API_URL/api/products" -Method GET `
    -Headers @{"Authorization" = "Bearer $TOKEN"}

Write-Host ($productsResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 4: GET PRODUCT BY ID
# =============================================================
Write-Host "[TEST 4] GET PRODUCT BY ID" -ForegroundColor $Yellow

$productResponse = Invoke-WebRequest -Uri "$API_URL/api/products/1" -Method GET `
    -Headers @{"Authorization" = "Bearer $TOKEN"}

Write-Host ($productResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 5: GET ALL CATEGORIES
# =============================================================
Write-Host "[TEST 5] GET ALL CATEGORIES" -ForegroundColor $Yellow

$categoriesResponse = Invoke-WebRequest -Uri "$API_URL/api/categories" -Method GET `
    -Headers @{"Authorization" = "Bearer $TOKEN"}

Write-Host ($categoriesResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 6: GET ALL CUSTOMERS
# =============================================================
Write-Host "[TEST 6] GET ALL CUSTOMERS" -ForegroundColor $Yellow

$customersResponse = Invoke-WebRequest -Uri "$API_URL/api/customers" -Method GET `
    -Headers @{"Authorization" = "Bearer $TOKEN"}

Write-Host ($customersResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 7: CREATE CUSTOMER
# =============================================================
Write-Host "[TEST 7] CREATE CUSTOMER" -ForegroundColor $Yellow

$customerData = @{
    name = "Test Customer"
    email = "test_$(Get-Date -UFormat %s)@example.com"
    phone = "1234567890"
    address = "123 Test St"
} | ConvertTo-Json

$createCustomerResponse = Invoke-WebRequest -Uri "$API_URL/api/customers" -Method POST `
    -Headers @{"Authorization" = "Bearer $TOKEN"; "Content-Type" = "application/json"} `
    -Body $customerData

$customerJson = $createCustomerResponse.Content | ConvertFrom-Json
$CUSTOMER_ID = $customerJson.id

Write-Host ($customerJson | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 8: GET ALL ORDERS
# =============================================================
Write-Host "[TEST 8] GET ALL ORDERS" -ForegroundColor $Yellow

$ordersResponse = Invoke-WebRequest -Uri "$API_URL/api/orders" -Method GET `
    -Headers @{"Authorization" = "Bearer $TOKEN"}

Write-Host ($ordersResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 9: CREATE ORDER
# =============================================================
Write-Host "[TEST 9] CREATE ORDER" -ForegroundColor $Yellow

$orderData = @{
    customerId = $CUSTOMER_ID
    items = @(
        @{
            productId = 1
            quantity = 2
            unitPrice = 50.00
        }
    )
} | ConvertTo-Json

$createOrderResponse = Invoke-WebRequest -Uri "$API_URL/api/orders" -Method POST `
    -Headers @{"Authorization" = "Bearer $TOKEN"; "Content-Type" = "application/json"} `
    -Body $orderData

Write-Host ($createOrderResponse.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10)
Write-Host ""

# =============================================================
# TEST 10: TRY TO CREATE PRODUCT (SHOULD FAIL)
# =============================================================
Write-Host "[TEST 10] TRY TO CREATE PRODUCT - SHOULD FAIL (403)" -ForegroundColor $Yellow

$productData = @{
    name = "Test Product"
    sku = "TST001"
    price = 100.00
    stock = 50
} | ConvertTo-Json

try {
    $createProductResponse = Invoke-WebRequest -Uri "$API_URL/api/products" -Method POST `
        -Headers @{"Authorization" = "Bearer $TOKEN"; "Content-Type" = "application/json"} `
        -Body $productData
    Write-Host "✗ FAIL - Should have been rejected" -ForegroundColor $Red
    $script:FAIL++
} catch {
    $httpCode = $_.Exception.Response.StatusCode.value__
    if ($httpCode -eq 403) {
        Write-Host "✓ PASS - Correctly rejected (HTTP $httpCode)" -ForegroundColor $Green
        $script:PASS++
    } else {
        Write-Host "✗ FAIL - Expected 403, got $httpCode" -ForegroundColor $Red
        $script:FAIL++
    }
}
Write-Host ""

# =============================================================
# TEST 11: REQUEST WITHOUT TOKEN (SHOULD FAIL)
# =============================================================
Write-Host "[TEST 11] REQUEST WITHOUT TOKEN - SHOULD FAIL (401)" -ForegroundColor $Yellow

try {
    $noTokenResponse = Invoke-WebRequest -Uri "$API_URL/api/products" -Method GET
    Write-Host "✗ FAIL - Should have been rejected" -ForegroundColor $Red
    $script:FAIL++
} catch {
    $httpCode = $_.Exception.Response.StatusCode.value__
    if ($httpCode -eq 401) {
        Write-Host "✓ PASS - Correctly rejected (HTTP $httpCode)" -ForegroundColor $Green
        $script:PASS++
    } else {
        Write-Host "✗ FAIL - Expected 401, got $httpCode" -ForegroundColor $Red
        $script:FAIL++
    }
}
Write-Host ""

# =============================================================
# TEST 12: REQUEST WITH INVALID TOKEN (SHOULD FAIL)
# =============================================================
Write-Host "[TEST 12] REQUEST WITH INVALID TOKEN - SHOULD FAIL (401)" -ForegroundColor $Yellow

try {
    $invalidTokenResponse = Invoke-WebRequest -Uri "$API_URL/api/products" -Method GET `
        -Headers @{"Authorization" = "Bearer invalid.token.here"}
    Write-Host "✗ FAIL - Should have been rejected" -ForegroundColor $Red
    $script:FAIL++
} catch {
    $httpCode = $_.Exception.Response.StatusCode.value__
    if ($httpCode -eq 401) {
        Write-Host "✓ PASS - Correctly rejected (HTTP $httpCode)" -ForegroundColor $Green
        $script:PASS++
    } else {
        Write-Host "✗ FAIL - Expected 401, got $httpCode" -ForegroundColor $Red
        $script:FAIL++
    }
}
Write-Host ""

# =============================================================
# TEST 13: INVALID CREDENTIALS (SHOULD FAIL)
# =============================================================
Write-Host "[TEST 13] LOGIN WITH INVALID PASSWORD - SHOULD FAIL (401)" -ForegroundColor $Yellow

$invalidLoginData = @{
    username = $TEST_USERNAME
    password = "wrongpassword"
} | ConvertTo-Json

try {
    $invalidLoginResponse = Invoke-WebRequest -Uri "$API_URL/api/auth/login" -Method POST `
        -Headers @{"Content-Type" = "application/json"} -Body $invalidLoginData
    Write-Host "✗ FAIL - Should have been rejected" -ForegroundColor $Red
    $script:FAIL++
} catch {
    $httpCode = $_.Exception.Response.StatusCode.value__
    if ($httpCode -eq 401) {
        Write-Host "✓ PASS - Correctly rejected (HTTP $httpCode)" -ForegroundColor $Green
        $script:PASS++
    } else {
        Write-Host "✗ FAIL - Expected 401, got $httpCode" -ForegroundColor $Red
        $script:FAIL++
    }
}
Write-Host ""

# =============================================================
# SUMMARY
# =============================================================
Write-Host "========================================" -ForegroundColor $Blue
Write-Host "TEST SUMMARY" -ForegroundColor $Blue
Write-Host "========================================" -ForegroundColor $Blue
Write-Host "✓ PASSED: $PASS" -ForegroundColor $Green
Write-Host "✗ FAILED: $FAIL" -ForegroundColor $Red
Write-Host ""

if ($FAIL -eq 0) {
    Write-Host "✓ ALL TESTS PASSED!" -ForegroundColor $Green
    exit 0
} else {
    Write-Host "✗ SOME TESTS FAILED" -ForegroundColor $Red
    exit 1
}

