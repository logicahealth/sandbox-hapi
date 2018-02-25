#!/usr/bin/env bash

docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_PASSWORD}
docker build --build-arg ACTIVE_ENV=$ACTIVE_ENV -t $DOCKER_IMAGE_COORDINATES ..
echo "docker push..."
docker push "$DOCKER_IMAGE_COORDINATES"
