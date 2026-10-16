Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Uploading Realm Stitchers to Sushant-Nemade/browser" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

$owner = "Sushant-Nemade"
$repoName = "browser"

# Prompt for GitHub Personal Access Token securely
Write-Host "Please enter your GitHub Personal Access Token (with 'repo' scope)." -ForegroundColor Yellow
Write-Host "(Need one? Create it at: https://github.com/settings/tokens/new?scopes=repo&description=BrowserGame)" -ForegroundColor Gray
Write-Host ""
$sec = Read-Host "GitHub Token (typing is hidden)" -AsSecureString
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec)
$token = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

if ([string]::IsNullOrWhiteSpace($token)) {
    Write-Host "No token provided. Aborted." -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Accept" = "application/vnd.github.v3+json"
    "User-Agent" = "RealmStitchersUploader"
}

Write-Host ""
Write-Host "1. Checking if repository '$owner/$repoName' exists on GitHub..." -ForegroundColor Cyan

$exists = $false
try {
    $existing = Invoke-RestMethod -Uri "https://api.github.com/repos/$owner/$repoName" -Headers $headers -Method Get
    $exists = $true
    Write-Host "   Repository already exists!" -ForegroundColor Green
} catch {
    Write-Host "   Repository does not exist yet. Creating '$owner/$repoName' on GitHub..." -ForegroundColor Cyan
    $body = @{
        name = $repoName
        description = "Realm Stitchers — Cross-Reality Multiplayer Combat (Three.js + WebRTC + Spring Boot)"
        homepage = "https://$($owner.ToLower()).github.io/$repoName/"
        private = $false
        has_issues = $true
        has_projects = $true
        has_wiki = $true
    } | ConvertTo-Json

    try {
        $created = Invoke-RestMethod -Uri "https://api.github.com/user/repos" -Headers $headers -Method Post -Body $body -ContentType "application/json"
        Write-Host "   Repository successfully created at: $($created.html_url)" -ForegroundColor Green
        $exists = $true
    } catch {
        Write-Host "   Failed to create repository: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.ErrorDetails) { Write-Host "   Details: $($_.ErrorDetails.Message)" -ForegroundColor Red }
        exit 1
    }
}

Write-Host "2. Pushing all files, commits, and workflows to main branch..." -ForegroundColor Cyan
$pushUrl = "https://${token}@github.com/${owner}/${repoName}.git"

git push $pushUrl main:main --force 2>&1

if ($LASTEXITCODE -eq 0) {
    # Ensure local remote is set cleanly without the token
    git remote set-url origin "https://github.com/${owner}/${repoName}.git"
    Write-Host ""
    Write-Host "========================================================" -ForegroundColor Green
    Write-Host "  SUCCESS! Project successfully published to:" -ForegroundColor Green
    Write-Host "  https://github.com/$owner/$repoName" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Green
    Start-Process "https://github.com/$owner/$repoName"
} else {
    Write-Host ""
    Write-Host "Git push failed. Please verify that your token has 'repo' (Full control of private/public repositories) permission enabled." -ForegroundColor Red
}
