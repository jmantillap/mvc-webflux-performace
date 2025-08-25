#!/bin/bash
# 1. Compilar con Maven
mvn clean package -DskipTests

# 2. Construir la imagen
docker build -t fibonacci-webflux:latest .

# 3. Etiquetar y subir al registry local
docker tag fibonacci-mvc:latest 192.168.1.6:5000/fibonacci-webflux:latest
# save en local-regitry la imagen--> image: registry:2
docker push 192.168.1.6:5000/fibonacci-webflux:latest

#chmod +x build_and_push.sh
#./build_and_push.sh