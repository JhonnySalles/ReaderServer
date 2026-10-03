@echo off
title Reader Server - Importador de Midias
cd /d "%~dp0"

echo ======================================================
echo    Verificando dependencias do Importador...
echo ======================================================

if not exist node_modules (
    echo Instalando dependencias com Yarn...
    call yarn install
    if errorlevel 1 (
        echo Yarn falhou ou nao encontrado, tentando com npm...
        call npm install
    )
)

echo.
echo Iniciando script de importacao...
echo.
node import.js

pause
