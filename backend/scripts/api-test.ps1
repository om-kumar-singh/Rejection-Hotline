$base = "http://localhost:8080/api/v1"
$results = @()

function Test-Endpoint {
    param($Name, $Method, $Url, $Body, $Headers, $ExpectedStatus)
    try {
        $params = @{ Uri = $Url; Method = $Method; ContentType = "application/json" }
        if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 5) }
        if ($Headers) { $params.Headers = $Headers }
        $response = Invoke-WebRequest @params -UseBasicParsing
        $ok = $response.StatusCode -eq $ExpectedStatus
        $results += [PSCustomObject]@{ Test = $Name; Status = $(if ($ok) { "PASS" } else { "FAIL" }); Code = $response.StatusCode; Expected = $ExpectedStatus }
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        $ok = $code -eq $ExpectedStatus
        $results += [PSCustomObject]@{ Test = $Name; Status = $(if ($ok) { "PASS" } else { "FAIL" }); Code = $code; Expected = $ExpectedStatus }
    }
}

$email = "apitest-$(Get-Random)@example.com"
$regBody = @{ fullName = "API Tester"; email = $email; password = "password123" }
$reg = Invoke-RestMethod -Uri "$base/auth/register" -Method POST -ContentType "application/json" -Body ($regBody | ConvertTo-Json)
$token = $reg.accessToken
$auth = @{ Authorization = "Bearer $token" }

Test-Endpoint "Register duplicate email" POST "$base/auth/register" $regBody $null 409
Test-Endpoint "Login valid" POST "$base/auth/login" @{ email = $email; password = "password123" } $null 200
Test-Endpoint "Login invalid password" POST "$base/auth/login" @{ email = $email; password = "wrong" } $null 401
Test-Endpoint "GET /auth/me" GET "$base/auth/me" $null $auth 200

$appBody = @{
    companyName = "Google"; jobRole = "Backend Engineer"; appliedDate = "2026-06-20"
    emailStatus = "SENT"; replyReceived = $false; applicationStatus = "APPLIED"
    hrName = "Jane"; hrEmail = "jane@google.com"
}
$app = Invoke-RestMethod -Uri "$base/applications" -Method POST -Headers $auth -ContentType "application/json" -Body ($appBody | ConvertTo-Json)
$appId = $app.id

Test-Endpoint "GET applications" GET "$base/applications?page=0&size=10" $null $auth 200
Test-Endpoint "GET application by id" GET "$base/applications/$appId" $null $auth 200
$updateBody = $appBody.Clone()
$updateBody.companyName = "Google LLC"
Test-Endpoint "PUT application" PUT "$base/applications/$appId" $updateBody $auth 200
Test-Endpoint "GET dashboard" GET "$base/dashboard/summary" $null $auth 200
Test-Endpoint "GET notifications" GET "$base/notifications" $null $auth 200
Test-Endpoint "GET unread count" GET "$base/notifications/unread-count" $null $auth 200
Test-Endpoint "GET analytics monthly" GET "$base/analytics/applications-by-month" $null $auth 200
Test-Endpoint "GET analytics rates" GET "$base/analytics/rates" $null $auth 200
Test-Endpoint "Unauthorized access" GET "$base/applications" $null $null 401
Test-Endpoint "Invalid JWT" GET "$base/applications" $null @{ Authorization = "Bearer invalid.token" } 401

Test-Endpoint "Mark follow-up sent" POST "$base/applications/$appId/mark-follow-up-sent" $null $auth 200
Test-Endpoint "Reset follow-up" POST "$base/applications/$appId/reset-follow-up" $null $auth 200
Test-Endpoint "DELETE application" DELETE "$base/applications/$appId" $null $auth 204

Test-Endpoint "Validation error empty title" POST "$base/applications" @{ companyName = ""; jobRole = "X"; appliedDate = "2026-06-20"; emailStatus = "SENT" } $auth 400

$results | Format-Table -AutoSize
$passed = ($results | Where-Object { $_.Status -eq "PASS" }).Count
$failed = ($results | Where-Object { $_.Status -eq "FAIL" }).Count
Write-Output "`nSummary: $passed passed, $failed failed out of $($results.Count) tests"
