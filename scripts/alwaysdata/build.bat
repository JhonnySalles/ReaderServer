@echo off
setlocal enabledelayedexpansion

echo ===============================================================================
echo       COMPILADOR UNIFICADO PARA DEPLOY NO ALWAYSDATA (BACKEND + FRONTEND)
echo ===============================================================================
echo.

REM -------------------------------------------------------------------------------
REM 1. Compilação do Backend (Spring Boot API)
REM -------------------------------------------------------------------------------
echo [1/2] Compilando Backend Server (Kotlin + Spring Boot)...
cd /d "%~dp0..\..\server"

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo [INFO] Compilando com Maven instalado no sistema...
    call mvn clean package -DskipTests
) else if exist "mvnw.cmd" (
    echo [INFO] Compilando com Maven Wrapper...
    call mvnw.cmd clean package -DskipTests
) else (
    echo [ERRO] Maven nao encontrado no PATH do sistema nem mvnw.cmd.
    pause
    exit /b 1
)

if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilacao do Backend Maven.
    pause
    exit /b 1
)

echo [OK] Backend compilado com sucesso!
echo.

REM -------------------------------------------------------------------------------
REM 2. Compilação do Frontend (React + Vite SPA)
REM -------------------------------------------------------------------------------
echo [2/2] Compilando Frontend Site (React + Vite)...
cd /d "%~dp0..\..\frontend"

where yarn >nul 2>nul
if %errorlevel% equ 0 (
    set PKG_MGR=yarn
) else (
    set PKG_MGR=npm
)

echo [INFO] Gerenciador detectado: !PKG_MGR!

if not exist "node_modules\" (
    echo [INFO] Instalando dependencias do frontend...
    if "!PKG_MGR!"=="yarn" (
        call yarn install
    ) else (
        call npm install
    )
    if !errorlevel! neq 0 (
        echo [ERRO] Falha ao instalar dependencias do frontend.
        pause
        exit /b 1
    )
)

echo [INFO] Executando build de producao do frontend (perfil Alwaysdata /readerserver/)...
if "!PKG_MGR!"=="yarn" (
    call yarn build:alwaysdata
) else (
    call npm run build:alwaysdata
)

if !errorlevel! neq 0 (
    echo [ERRO] Falha ao compilar o frontend.
    pause
    exit /b 1
)

echo [OK] Frontend compilado com sucesso!
echo.

REM -------------------------------------------------------------------------------
REM 3. Resumo Final e Instruções de Deploy
REM -------------------------------------------------------------------------------
echo ===============================================================================
echo                       CONCLUIDO COM SUCESSO!
echo ===============================================================================
echo.
echo 📦 Artefatos gerados:
echo   - Backend JAR:  server\target\
for %%f in ("%~dp0..\..\server\target\ReaderServer*.jar") do (
    echo       * %%~nxf
)
echo   - Frontend Dist: frontend\dist\ (Arquivos HTML/CSS/JS e .htaccess)
echo.
echo 🚀 Instrucoes de Deploy no Alwaysdata:
echo.
echo   1. BACKEND API (Caminho /readerserver/api):
echo      - Envie o arquivo ReaderServer-*.jar e scripts\alwaysdata\start.sh para:
echo        /home/suaconta/readerserver/
echo      - Atribua permissao: chmod +x /home/suaconta/readerserver/start.sh
echo      - No painel Alwaysdata: Site tipo "Programa do usuario", caminho /readerserver/api
echo.
echo   2. FRONTEND SITE (Caminho /readerserver):
echo      - Envie todo o conteudo de frontend\dist\ para a pasta existente:
echo        /home/suaconta/www/readerserver/
echo      - No painel Alwaysdata: Se o site principal ja aponta para /home/suaconta/www/,
echo        ele respondera diretamente em /readerserver/ sem configuracao extra.
echo ===============================================================================
pause
