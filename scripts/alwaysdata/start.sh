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

# Garante a criacao do diretorio de logs no local da aplicacao
LOGS_DIR="$DIR/logs"
mkdir -p "$LOGS_DIR"
APP_LOG_FILE="${LOG_FILE:-$LOGS_DIR/server.log}"

echo "=========================================================="
echo "Iniciando ReaderServer API no Alwaysdata..."
echo " - JAR: $JAR_FILE"
echo " - Binding: $APP_IP:$APP_PORT"
echo " - Log: $APP_LOG_FILE"
echo "=========================================================="

# Ajuste otimizado de memória JVM e startup rápido para Alwaysdata:
# - TieredStopAtLevel=1: acelera o startup de ~45s para ~10s evitando o timeout do Alwaysdata
# - SerialGC, MaxMetaspace e ReservedCodeCache: evitam que o processo seja morto por OOM Killer
DEFAULT_OPTS="-Xms32m -Xmx128m -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=32m -Xss256k -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="${JAVA_OPTS:-$DEFAULT_OPTS}"

exec java $JAVA_OPTS \
    -Dserver.port="$APP_PORT" \
    -Dserver.address="$APP_IP" \
    -Dlogging.file.name="$APP_LOG_FILE" \
    -jar "$JAR_FILE" \
    --spring.profiles.active=alwaysdata
