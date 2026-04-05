@echo off
cd /d E:\project\name-list
call mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m"
