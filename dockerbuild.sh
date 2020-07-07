#!/usr/bin/env bash

#generate backend jar
cd ./back-end-server/
./gradlew build

cd ..
cp ./back-end-server/build/libs/dept-video-server.jar ./dockercompose/dept-video-server/dept-video-server.jar

#generate ui
cd ./front-end-react/
npm install
npm run build
cd ..
cp -r ./front-end-react/build/* ./dockercompose/nginx/var/www/html/

docker-compose up -d