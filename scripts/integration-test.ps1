# 前后端联调测试（PowerShell）
# 用法：.\scripts\integration-test.ps1 -BaseUrl "http://8.166.140.82"

param(
    [string]$BaseUrl = "http://127.0.0.1",
    [string]$ApiDirect = "http://127.0.0.1:8080"
)

$pass = 0
$fail = 0

function Test-Endpoint {
    param([string]$Name, [string]$Url)
    Write-Host ""
    Write-Host "==> $Name"
    Write-Host "    $Url"
    try {
        $resp = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 15
        Write-Host "[PASS] HTTP $($resp.StatusCode)" -ForegroundColor Green
        if ($resp.Content.Length -gt 0) {
            $preview = $resp.Content.Substring(0, [Math]::Min(200, $resp.Content.Length))
            Write-Host "    $preview"
        }
        $script:pass++
        return $true
    } catch {
        Write-Host "[FAIL] $($_.Exception.Message)" -ForegroundColor Red
        $script:fail++
        return $false
    }
}

Write-Host "========== 后端直连 ($ApiDirect) =========="
Test-Endpoint "stats/users" "$ApiDirect/api/stats/users"
Test-Endpoint "users" "$ApiDirect/api/users"
Test-Endpoint "health/all" "$ApiDirect/api/health/all"

Write-Host ""
Write-Host "========== 经前端/Nginx ($BaseUrl) =========="
Test-Endpoint "前端首页" "$BaseUrl/"
Test-Endpoint "API stats/users" "$BaseUrl/api/stats/users"
Test-Endpoint "API users" "$BaseUrl/api/users"

Write-Host ""
Write-Host "========== 结果 =========="
Write-Host "通过: $pass  失败: $fail"
if ($fail -gt 0) { exit 1 }
