FROM maven:3.9-eclipse-temurin-23-alpine AS dependencies

...

FROM eclipse-temurin:23-jre-alpine AS extractor

...

FROM eclipse-temurin:23-jre-alpine AS final