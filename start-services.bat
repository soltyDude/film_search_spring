@echo off
setlocal

echo Запуск Eureka (eureka-server)
start "EUREKA SERVER" cmd /k "title EUREKA SERVER && cd D:\GUIProgektii\stag\WEB_APP\MK\eureka-server && mvn spring-boot:run"

echo Запуск Film Service
start "FILM SERVICE" cmd /k "title FILM SERVICE && cd film-service && mvn spring-boot:run"

echo Запуск Review Service
start "REVIEW SERVICE" cmd /k "title REVIEW SERVICE && cd review-service && mvn spring-boot:run"

echo Запуск User Service
start "USER SERVICE" cmd /k "title USER SERVICE && cd user-service && mvn spring-boot:run"

echo Запуск Playlist Service
start "PLAYLIST SERVICE" cmd /k "title PLAYLIST SERVICE && cd playlist-service && mvn spring-boot:run"

echo Запуск Gateway (gateway-service)
start "GATEWAY" cmd /k "title GATEWAY && cd gateway-service && mvn spring-boot:run"

echo Все микросервисы запущены в отдельных окнах!
pause
