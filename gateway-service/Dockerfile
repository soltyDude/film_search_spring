# Используем официальный образ Java
FROM openjdk:17-jdk-slim

# Задаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR файл в контейнер
COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Экспонируем порт, который используется Gateway
EXPOSE 8081

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
