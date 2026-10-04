#!/bin/sh
set -e

# Configurações padrão caso não sejam passadas via ENV
API_BASE_URL=${VITE_API_BASE_URL:-"http://localhost:8083"}
API_USERNAME=${VITE_API_USERNAME:-"admin"}
API_PASSWORD=${VITE_API_PASSWORD:-"admin"}

echo "Inicializando ReaderServer Frontend..."
echo "API Base URL: ${API_BASE_URL}"
echo "API Username: ${API_USERNAME}"

# Cria arquivo de configuração runtime dinâmica
cat <<EOF > /usr/share/nginx/html/runtime-config.js
window.__RUNTIME_CONFIG__ = {
  VITE_API_BASE_URL: "${API_BASE_URL}",
  VITE_API_USERNAME: "${API_USERNAME}",
  VITE_API_PASSWORD: "${API_PASSWORD}"
};
EOF

# Executa comando padrão do Nginx
exec "$@"
