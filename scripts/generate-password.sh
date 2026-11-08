#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER_DIR="$DIR/../server"

PASS="$1"
USER="$2"

if [ -z "$PASS" ]; then
    read -sp "Digite a nova senha: " PASS
    echo ""
    read -p "Digite o usuario [admin]: " USER
fi

if [ -z "$USER" ]; then
    USER="admin"
fi

if [ -z "$PASS" ]; then
    echo "[ERRO] A senha nao pode ser vazia."
    exit 1
fi

cd "$SERVER_DIR"
if command -v mvn &> /dev/null; then
    mvn exec:java -Dexec.mainClass="br.com.fenix.readerserver.util.PasswordGenerator" -Dexec.args="$PASS $USER" -q
elif [ -f "./mvnw" ]; then
    ./mvnw exec:java -Dexec.mainClass="br.com.fenix.readerserver.util.PasswordGenerator" -Dexec.args="$PASS $USER" -q
else
    echo "[ERRO] Maven ou mvnw nao encontrado em $SERVER_DIR."
    exit 1
fi

