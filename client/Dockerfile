# ---------- 构建阶段：Node (Alpine) 构建静态资源 ----------
FROM node:22-alpine AS build
WORKDIR /app

# 利用依赖缓存
COPY package*.json ./
RUN npm ci

# 构建（API 基础地址在构建期注入，默认走同源 /api 由 nginx 反代）
COPY . .
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

# ---------- 运行阶段：Nginx (Alpine) 提供静态资源 ----------
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
