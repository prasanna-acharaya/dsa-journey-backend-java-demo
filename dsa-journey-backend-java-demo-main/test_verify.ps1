$token = 'eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJBRE1JTjAwMSIsImF1dGhvcml0aWVzIjoiUk9MRV9BRE1JTiIsImlhdCI6MTc2NjA4ODYwMCwiZXhwIjoxNzY2MDkyMjAwLCJpc3MiOiJkc2EtbG9hbi1tYW5hZ2VtZW50In0.rZ_zfMqVW38tphzn9_hP0N31NumMjeAEiYqa7GSNdk0NcJZ26lGnGgygkrqkLRIo'
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "--- Creating Lead ---"
$leadResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/v1/leads" -Method Post -Headers $headers -InFile "create_lead.json"
$leadResponse | ConvertTo-Json

Write-Host "`n--- Creating DSA ---"
$dsaResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/v1/dsa" -Method Post -Headers $headers -InFile "create_dsa.json"
$dsaResponse | ConvertTo-Json

Write-Host "`n--- Fetching Leads ---"
$leadsList = Invoke-RestMethod -Uri "http://localhost:8082/api/v1/leads?size=50" -Method Get -Headers $headers
$leadsList.content | Select-Object applicationReferenceNumber, customerName, productType, status | Format-Table

Write-Host "`n--- Fetching DSAs ---"
$dsaList = Invoke-RestMethod -Uri "http://localhost:8082/api/v1/dsa?size=50" -Method Get -Headers $headers
$dsaList.content | Select-Object uniqueCode, name, status, category | Format-Table
