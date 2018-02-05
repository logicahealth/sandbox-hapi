#!/usr/bin/env bash

while ! exec 6<>/dev/tcp/${DB_HOST}/${DB_PORT}; do
    echo "Trying to connect to MySQL at ${DB_PORT}..."
    sleep 10
done

java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar
