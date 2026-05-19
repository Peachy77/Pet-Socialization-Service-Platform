# 构建阶段（用于生产）
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm config set registry https://registry.npmmirror.com
RUN npm install
COPY . .
RUN npm run build

# 生产阶段(构建请用docker build --target production -t (例test-prod镜像名称) .命令)
FROM nginx:alpine AS production 
RUN apk add --no-cache wget
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html

RUN adduser -D -u 1000 appuser && \
    chown -R appuser /usr/share/nginx/html && \
    chown -R appuser /var/cache/nginx && \
    chown -R appuser /var/log/nginx && \
    chown -R appuser /run && \
    mkdir -p /var/run/nginx && \
    chown -R appuser /var/run/nginx

# 修改 nginx 配置，让 pid 文件写到有权限的目录
RUN sed -i 's/pid \/run\/nginx.pid/pid \/tmp\/nginx.pid/g' /etc/nginx/nginx.conf

USER appuser

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# 开发阶段(构建请用docker build --target development -t (例test-dev镜像名称) .命令)
FROM node:18-alpine AS development
RUN apk add --no-cache wget
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 8080
CMD ["npm", "run", "serve"]