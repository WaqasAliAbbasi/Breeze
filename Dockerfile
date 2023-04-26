FROM node:14-alpine  AS node-build
COPY ./web /home/web
WORKDIR /home/web
RUN npm install
RUN npm run build

FROM gradle:7-jdk11 AS grade-build
COPY --chown=gradle:gradle ./server /home/server
COPY --from=node-build /home/web/dist /home/server/src/main/resources/web
WORKDIR /home/server
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=grade-build /home/server/build/libs/*.jar /app/beamborg-server.jar
ENTRYPOINT ["java","-jar","/app/beamborg-server.jar"]