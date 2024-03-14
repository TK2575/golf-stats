#!/usr/bin/env bash
# from project root
# build jar
mvn package
# build docker image from jar
# FIXME Dockerfile requires changes to the jar name when the version changes in pom.xml
docker build -t golf-stats:latest -f docker/Dockerfile .
# spin up docker compose (app + redis)
docker-compose -f docker/docker-compose.yml up --detach
# bring down services
docker-compose -f docker/docker-compose.yml down