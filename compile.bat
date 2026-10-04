@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ===============================================================================
echo                COMPILADOR E GERADOR DE IMAGENS DOCKER - READERSERVER
echo ===============================================================================
echo.

REM 1. Validação de Ferramentas
echo [1/4] Verificando dependencias locais...

where yarn >nul 2>nul
if %errorlevel% neq 0 (
    echo [AVISO] Yarn nao encontrado no PATH. Tentando com NPM...
    set PKG_MGR=npm
) else (
    set PKG_MGR=yarn
)

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] Docker nao encontrado no PATH. Instale o Docker Desktop/Engine.
    pause
    exit /b 1
)

echo [OK] Gerenciador frontend: %PKG_MGR%
echo [OK] Docker detectado.
echo.

REM 2. Compilação do Frontend
echo ===============================================================================
echo [2/4] Compilando Frontend (React + Vite)...
echo ===============================================================================
cd /d "%~dp0frontend"

if not exist "node_modules\" (
    echo Instalando dependencias do frontend...
    if "%PKG_MGR%"=="yarn" (
        call yarn install --frozen-lockfile
    ) else (
        call npm ci
    )
    if %errorlevel% neq 0 (
        echo [ERRO] Falha ao instalar dependencias do Frontend.
        pause
        exit /b 1
    )
)

echo Executando build de producao do frontend...
if "%PKG_MGR%"=="yarn" (
    call yarn build
) else (
    call npm run build
)

if %errorlevel% neq 0 (
    echo [ERRO] Falha ao compilar o Frontend.
    pause
    exit /b 1
)
echo [OK] Frontend compilado com sucesso!
echo.

REM 3. Compilação do Backend
echo ===============================================================================
echo [3/4] Compilando Backend Server (Kotlin + Spring Boot)...
echo ===============================================================================
cd /d "%~dp0server"

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo Usando Maven instalado no sistema...
    call mvn clean package -DskipTests
) else if exist "mvnw.cmd" (
    echo Usando Maven Wrapper...
    call mvnw.cmd clean package -DskipTests
) else (
    echo [AVISO] Maven local nao encontrado. A compilacao sera realizada dentro do Dockerfile multi-stage.
)

echo [OK] Etapa de preparacao do Server concluida!
echo.

REM 4. Geração das Imagens Docker
echo ===============================================================================
echo [4/4] Gerando Imagens Docker...
echo ===============================================================================
cd /d "%~dp0"

echo.
echo -> Construindo imagem Docker do Frontend (readerserver-frontend:latest)...
docker build -t readerserver-frontend:latest ./frontend
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao construir imagem Docker do Frontend.
    pause
    exit /b 1
)

echo.
echo -> Construindo imagem Docker do Server (readerserver-server:latest)...
docker build -t readerserver-server:latest ./server
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao construir imagem Docker do Server.
    pause
    exit /b 1
)

echo.
echo ===============================================================================
echo                       CONCLUIDO COM SUCESSO!
echo ===============================================================================
echo Imagens geradas:
echo   - readerserver-frontend:latest
echo   - readerserver-server:latest
echo.
echo Para executar com Docker Compose, utilize o arquivo docker-compose.example.yml:
echo   docker compose -f docker-compose.example.yml up -d
echo.
echo Dica para Raspberry Pi 4 (ARM64 cross-compile):
echo   docker buildx build --platform linux/arm64 -t seu-repo/readerserver-frontend:latest ./frontend --push
echo   docker buildx build --platform linux/arm64 -t seu-repo/readerserver-server:latest ./server --push
echo ===============================================================================
pause
