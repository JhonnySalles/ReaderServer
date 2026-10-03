@echo off
setlocal
cd /d "%~dp0"

echo ===================================================
echo             Iniciando ReaderServer Frontend
echo ===================================================
echo.

REM Verifica se o yarn esta instalado
where yarn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] O gerenciador de pacotes Yarn nao foi encontrado no PATH.
    echo Por favor, instale o Yarn ou certifique-se de que ele esta nas variaveis de ambiente.
    echo.
    pause
    exit /b 1
)

REM Instala dependencias caso o node_modules nao exista
if not exist "node_modules\" (
    echo Instalando dependencias do projeto com Yarn...
    call yarn install
    if %errorlevel% neq 0 (
        echo [ERRO] Falha ao instalar dependencias com o Yarn.
        pause
        exit /b 1
    )
    echo.
)

REM Inicia o servidor Vite de desenvolvimento
echo Iniciando o servidor de desenvolvimento...
call yarn dev

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] O servidor foi encerrado inesperadamente.
    pause
)
