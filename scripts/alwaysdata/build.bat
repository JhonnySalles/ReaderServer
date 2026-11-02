@echo off
setlocal
cd /d "%~dp0..\..\server"

echo ===============================================================================
echo        COMPILANDO READERSERVER PARA DEPLOY NO ALWAYSDATA (FAT JAR)
echo ===============================================================================
echo.

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo [INFO] Compilando com Maven instalado no sistema...
    call mvn clean package -DskipTests
) else if exist "mvnw.cmd" (
    echo [INFO] Compilando com Maven Wrapper...
    call mvnw.cmd clean package -DskipTests
) else (
    echo [ERRO] Maven nao encontrado no PATH do sistema.
    pause
    exit /b 1
)

if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilacao do Maven.
    pause
    exit /b 1
)

echo.
echo ===============================================================================
echo [SUCESSO] Build gerado com sucesso!
echo Arquivos em: server\target\
echo ===============================================================================
for %%f in (target\ReaderServer*.jar) do (
    echo - %%~nxf
)
echo.
echo Instrucoes de Deploy:
echo 1. Faca upload do arquivo .jar para a pasta do ReaderServer no Alwaysdata via SFTP/SSH.
echo 2. Faca upload do script scripts\alwaysdata\start.sh para a mesma pasta.
echo 3. Configure o site no Alwaysdata como "Programa do Usuario" apontando para start.sh.
echo ===============================================================================
pause
