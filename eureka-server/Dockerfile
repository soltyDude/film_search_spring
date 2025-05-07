# Используем официальный образ Java
FROM openjdk:17-jdk-slim

# Задаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR файл в контейнер
COPY target/eureka-server-0.0.1-SNAPSHOT.jar /app/app.jar

# Экспонируем порт, который используется Eureka
EXPOSE 8761

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
