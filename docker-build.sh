#!/usr/bin/env bash

docker build -t hspconsortium/hspc-reference-api:latest .
docker run hspconsortium/hspc-reference-api:latest