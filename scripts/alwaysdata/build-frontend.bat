@echo off
setlocal
cd /d "%~dp0..\..\frontend"

echo ===============================================================================
echo        COMPILANDO SITE (FRONTEND REACT) PARA DEPLOY NO ALWAYSDATA
echo ===============================================================================
echo.

where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] Node.js / npm nao encontrado no PATH do sistema.
    pause
    exit /b 1
)

echo [INFO] Executando build de producao do frontend...
call npm run build

if %errorlevel% neq 0 (
    echo [ERRO] Falha no build do frontend.
    pause
    exit /b 1
)

echo.
echo ===============================================================================
echo [SUCESSO] Build do site gerado com sucesso!
echo Arquivos estaticos prontos em: frontend\dist\
echo ===============================================================================
echo.
echo Instrucoes de Deploy do Site no Alwaysdata:
echo 1. Faca upload de todo o conteudo da pasta 'frontend\dist' para o diretorio do
echo    site no Alwaysdata (ex: /home/suaconta/readersite/).
echo 2. No painel do Alwaysdata (Web ^> Sites):
echo    - Crie/configure o site como tipo "Arquivos estaticos" (Static files).
echo    - Defina o Diretorio raiz como o caminho onde colocou o dist (ex: /home/suaconta/readersite).
echo 3. Os logs do site (acessos e erros HTTP) ficarao automaticamente acessiveis em:
echo    /home/suaconta/admin/logs/sites/[nome-do-site]/
echo ===============================================================================
pause
