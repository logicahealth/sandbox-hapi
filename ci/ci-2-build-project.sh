#!/usr/bin/env bash

mvn -V -B -f ../pom.xml -s ../settings.xml deploy -P DEPLOY-HSPC,hspc-nexus
rc=$?
if [[ ${rc} -ne 0 ]] ; then
  echo 'mvn build failed'; exit $rc
fi

rm ../reference-api-webapp/target/*-sources.jar
rm ../reference-api-webapp/target/*-javadoc.jar
