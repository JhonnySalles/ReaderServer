#!/bin/bash
# ==============================================================================
# Script de Inicialização do ReaderServer no Alwaysdata (Programa do Usuário)
# ==============================================================================
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Alwaysdata exporta $PORT e $IP no ambiente do Programa do Usuário
APP_PORT="${PORT:-8083}"
APP_IP="${IP:-0.0.0.0}"

# Localiza o arquivo JAR no diretório atual ou em server/target/
JAR_FILE=$(ls -t ReaderServer*.jar target/ReaderServer*.jar ../server/target/ReaderServer*.jar 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "[ERRO] Arquivo ReaderServer*.jar nao encontrado no diretorio $DIR."
    echo "Certifique-se de transferir o arquivo .jar compilado para esta pasta."
    exit 1
fi

echo "=========================================================="
echo "Iniciando ReaderServer API no Alwaysdata..."
echo " - JAR: $JAR_FILE"
echo " - Binding: $APP_IP:$APP_PORT"
echo "=========================================================="

# Ajuste otimizado de memória JVM para Alwaysdata (Serial GC reduz overhead de RAM)
JAVA_OPTS="${JAVA_OPTS:--Xms64m -Xmx256m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom}"

exec java $JAVA_OPTS \
    -Dserver.port="$APP_PORT" \
    -Dserver.address="$APP_IP" \
    -jar "$JAR_FILE" \
    --spring.profiles.active=alwaysdata
