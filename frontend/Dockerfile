# Stage 1: Build
FROM node:22-alpine AS builder

WORKDIR /app

# Instala dependências aproveitando cache
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile --ignore-engines

# Copia código fonte (respeitando .dockerignore que ignora .env)
COPY . .

# Build do frontend de produção
RUN yarn build

# Stage 2: Serve via Nginx
FROM nginx:alpine

# Remove configuração padrão
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copia configuração customizada
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia arquivos estáticos compilados
COPY --from=builder /app/dist /usr/share/nginx/html

# Copia e dá permissão ao script de inicialização
COPY entrypoint.sh /docker-entrypoint.d/40-runtime-config.sh
RUN chmod +x /docker-entrypoint.d/40-runtime-config.sh

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
