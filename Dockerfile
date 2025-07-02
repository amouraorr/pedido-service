# Etapa 1: Build do projeto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
