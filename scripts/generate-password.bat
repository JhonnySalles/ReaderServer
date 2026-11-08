@echo off
setlocal enabledelayedexpansion

echo ===============================================================================
echo                GERADOR DE SENHA CRIPTOGRAFADA (BCRYPT)
echo ===============================================================================
echo.

set "SERVER_DIR=%~dp0..\server"

if not "%~1"=="" (
    set "PASS=%~1"
    set "USER=%~2"
) else (
    set /p "PASS=Digite a nova senha: "
    set /p "USER=Digite o usuario [admin]: "
)

if "%USER%"=="" set "USER=admin"

if "%PASS%"=="" (
    echo [ERRO] A senha nao pode ser vazia.
    if "%~1"=="" pause
    exit /b 1
)

cd /d "%SERVER_DIR%"

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    call mvn "exec:java" "-Dexec.mainClass=br.com.fenix.readerserver.util.PasswordGenerator" "-Dexec.args=%PASS% %USER%" -q
) else if exist "mvnw.cmd" (
    call mvnw.cmd "exec:java" "-Dexec.mainClass=br.com.fenix.readerserver.util.PasswordGenerator" "-Dexec.args=%PASS% %USER%" -q
) else (
    echo [ERRO] Maven nao encontrado no PATH nem mvnw.cmd em %SERVER_DIR%.
    if "%~1"=="" pause
    exit /b 1
)

if "%~1"=="" pause

