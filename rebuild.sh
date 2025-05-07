#!/bin/bash

services=(film-service review-service playlist-service user-service gateway)

echo "===== Сборка всех сервисов ====="
for service in "${services[@]}"
do
    echo "-> $service"
    (cd $service && mvn clean package -DskipTests)
done

echo "===== Остановка и удаление контейнеров ====="
docker compose down

echo "===== Перезапуск контейнеров с пересборкой ====="
docker compose up --build -d
