@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ===============================================================================
echo                 EXECUTOR DE TESTES AUTOMATIZADOS - READERSERVER
echo ===============================================================================
echo.

set TARGET=%~1
if "%TARGET%"=="" set TARGET=all

set FRONT_STATUS=NAO EXECUTADO
set BACK_STATUS=NAO EXECUTADO
set OVERALL_ERROR=0

REM ===============================================================================
REM 1. Testes do Frontend (Vitest)
REM ===============================================================================
if /i "%TARGET%"=="backend" goto :skip_frontend
if /i "%TARGET%"=="server" goto :skip_frontend
if /i "%TARGET%"=="back" goto :skip_frontend

echo -------------------------------------------------------------------------------
echo [1/2] Executando Testes do Frontend (React + Vitest)...
echo -------------------------------------------------------------------------------
cd /d "%~dp0frontend"

set PKG_MGR=npm
where yarn >nul 2>nul
if %errorlevel% equ 0 (
    set PKG_MGR=yarn
)

echo Usando gerenciador: %PKG_MGR%

if not exist "node_modules\" (
    echo [INFO] node_modules nao encontrado. Instalando dependencias...
    if "%PKG_MGR%"=="yarn" (
        call yarn install
    ) else (
        call npm install
    )
    if !errorlevel! neq 0 (
        echo [ERRO] Falha ao instalar dependencias do Frontend.
        set FRONT_STATUS=FALHOU na instalacao
        set OVERALL_ERROR=1
        goto :after_frontend
    )
)

if "%PKG_MGR%"=="yarn" (
    call yarn test
) else (
    call npm test
)

if !errorlevel! neq 0 (
    echo [FALHA] Testes do Frontend falharam.
    set FRONT_STATUS=FALHOU
    set OVERALL_ERROR=1
) else (
    echo [SUCESSO] Testes do Frontend passaram com exito!
    set FRONT_STATUS=PASSOU
)

:after_frontend
echo.
cd /d "%~dp0"

:skip_frontend

REM ===============================================================================
REM 2. Testes do Backend (JUnit / MockK / Spring Boot)
REM ===============================================================================
if /i "%TARGET%"=="frontend" goto :skip_backend
if /i "%TARGET%"=="front" goto :skip_backend

echo -------------------------------------------------------------------------------
echo [2/2] Executando Testes do Backend Server (Kotlin + Spring Boot)...
echo -------------------------------------------------------------------------------
cd /d "%~dp0server"

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo Usando Maven instalado no sistema...
    call mvn test
    if !errorlevel! neq 0 (
        echo [FALHA] Testes do Backend falharam.
        set BACK_STATUS=FALHOU
        set OVERALL_ERROR=1
    ) else (
        echo [SUCESSO] Testes do Backend passaram com exito!
        set BACK_STATUS=PASSOU
    )
    goto :after_backend
)

if exist "mvnw.cmd" (
    echo Usando Maven Wrapper...
    call mvnw.cmd test
    if !errorlevel! neq 0 (
        echo [FALHA] Testes do Backend falharam.
        set BACK_STATUS=FALHOU
        set OVERALL_ERROR=1
    ) else (
        echo [SUCESSO] Testes do Backend passaram com exito!
        set BACK_STATUS=PASSOU
    )
    goto :after_backend
)

echo [AVISO] Maven local [mvn] nao encontrado no PATH do sistema.
echo Para executar os testes do backend:
echo   - Instale o Maven ou adicione ao PATH
echo   - Ou execute os testes diretamente pela sua IDE [IntelliJ, VS Code, etc.]
set BACK_STATUS=PULADO [Maven ausente no PATH]

:after_backend
echo.
cd /d "%~dp0"

:skip_backend

REM ===============================================================================
REM Resumo Final
REM ===============================================================================
echo ===============================================================================
echo                               RESUMO DOS TESTES
echo ===============================================================================
echo   - Frontend: %FRONT_STATUS%
echo   - Backend:  %BACK_STATUS%
echo ===============================================================================
echo.

if %OVERALL_ERROR% neq 0 (
    echo [ATENCAO] Houve falhas durante a execucao dos testes.
) else (
    echo [OK] Execucao concluida!
)
echo.

pause
exit /b %OVERALL_ERROR%
