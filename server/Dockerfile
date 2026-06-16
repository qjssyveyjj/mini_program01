# ---------- 构建阶段：使用 Maven + Temurin 17 (Alpine) 打包 ----------
FROM m.daocloud.io/docker.io/library/maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 先复制 pom 以利用依赖缓存
COPY pom.xml ./
RUN mvn -B dependency:go-offline || true

# 再复制源码并打包（跳过测试）
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- 运行阶段：使用 Temurin 17 JRE (Alpine) ----------
FROM m.daocloud.io/docker.io/library/eclipse-temurin:17-jre-alpine
WORKDIR /app

# 创建非 root 用户运行
RUN addgroup -S app && adduser -S app -G app

# 复制构建产物
COPY --from=build /app/target/server-*.jar app.jar

# 准备上传目录
RUN mkdir -p /app/uploads && chown -R app:app /app

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
