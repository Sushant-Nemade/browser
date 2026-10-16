@echo off
echo ========================================================
echo   Uploading Realm Stitchers to GitHub (Sushant-Nemade/browser)
echo ========================================================
echo.
echo Checking repository connection...
git push -u origin main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo --------------------------------------------------------
    echo If you see "Repository not found":
    echo 1. Make sure https://github.com/Sushant-Nemade/browser is created on GitHub.
    echo    Quick link: https://github.com/new?name=browser
    echo 2. Run this script again after creating the repo!
    echo --------------------------------------------------------
) else (
    echo.
    echo ========================================================
    echo   Successfully uploaded to GitHub!
    echo   Repository: https://github.com/Sushant-Nemade/browser
    echo ========================================================
)
echo.
pause
