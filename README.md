# Запуск в Tomcat (Windows)

1) Установить TomCat 10-ой версии
2) Выполнить в директории проекта `mvn clean package`
3) Скопировать `tinvest.war` из target директории в `apache-tomcat-10/webapps`
4) Перейти в директорию `apache-tomcat-10/bin` и выполнить `.\catalina.bat run`

Приложение запуститься на хосте: http://localhost:8080
