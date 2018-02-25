#!/usr/bin/env bash

mvn -V -B -s settings.xml deploy -P DEPLOY-HSPC,hspc-nexus
rm reference-api-webapp-multitenant/target/*-sources.jar
rm reference-api-webapp-multitenant/target/*-javadoc.jar
