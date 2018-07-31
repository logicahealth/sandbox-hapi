#!/usr/bin/env bash

tag="hspconsortium/hspc-reference-api:latest"
if [ $# -gt 0 ]; then
  tag=$1
fi

target_env="local"
if [ $# -gt 1 ]; then
  target_env=$2
fi

# files must be in a folder or subfolder
mkdir -p target
cp ../reference-api-webapp/target/*.jar target

docker \
  build -t $tag \
  --build-arg TARGET_ENV=$target_env
  .
