#!/usr/bin/env bash

set -e

echo "starting $0..."

[[ -z "$1" ]] && { echo "usage: $0 {test | prod} port memory(mb)"; exit 1; } || echo "ENV: $1"
[[ -z "$2" ]] && { echo "usage: $0 {test | prod} port memory(mb)"; exit 1; } || echo "ENV: $2"
[[ -z "$3" ]] && { echo "usage: $0 {test | prod} port memory(mb)"; exit 1; } || echo "ENV: $3"
[[ -z "$4" ]] && { POM="pom.xml"; } || POM="${4}/pom.xml"
echo "POM: $POM"

export DOCKER_REPO="hspconsortium"; echo DOCKER_REPO
export IMAGE_NAME=$(mvn -q -f ${POM} -Dexec.executable="echo" -Dexec.args='${project.artifactId}' --non-recursive exec:exec); echo $IMAGE_NAME
export PROJECT_VERSION=$(mvn -q -f ${POM} -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec); echo $PROJECT_VERSION
export IMAGE_COORDINATES="${DOCKER_REPO}/${IMAGE_NAME}:${PROJECT_VERSION}"; echo $IMAGE_COORDINATES;
export IMAGE_PORT="$2"; echo $IMAGE_PORT;
export IMAGE_MEMORY_RESERVATION="$3"; echo $IMAGE_MEMORY_RESERVATION;
export SPRING_PROFILES_ACTIVE="$1"; echo $SPRING_PROFILES_ACTIVE;
export AWS_SERVICE_NAME=$IMAGE_NAME; echo $AWS_SERVICE_NAME
export AWS_CONTAINER_NAME=$IMAGE_NAME; echo $AWS_CONTAINER_NAME
export AWS_SERVICE_NAME=$IMAGE_NAME; echo $AWS_SERVICE_NAME

echo "building set_env.sh..."
echo "#!/usr/bin/env bash" >> set_env.sh
echo "export DOCKER_REPO=$DOCKER_REPO" >> set_env.sh
echo "export IMAGE_NAME=$IMAGE_NAME" >> set_env.sh
echo "export PROJECT_VERSION=$PROJECT_VERSION" >> set_env.sh
echo "export IMAGE_COORDINATES=$IMAGE_COORDINATES" >> set_env.sh
echo "export IMAGE_PORT=$IMAGE_PORT" >> set_env.sh
echo "export IMAGE_MEMORY_RESERVATION=$IMAGE_MEMORY_RESERVATION" >> set_env.sh
echo "export SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" >> set_env.sh
echo "export AWS_CONTAINER_NAME=$AWS_CONTAINER_NAME" >> set_env.sh
echo "export AWS_SERVICE_NAME=$AWS_SERVICE_NAME" >> set_env.sh

chmod 755 set_env.sh
cat set_env.sh

echo "finished $0"
