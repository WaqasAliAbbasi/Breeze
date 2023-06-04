FROM node:14-alpine  AS web-build
WORKDIR /home/web
COPY ./web /home/web
RUN npm install
RUN npm run build

FROM node:14-alpine  AS extension-build
WORKDIR /home/extension
RUN apk add --no-cache zip
COPY ./extension /home/extension
RUN npm install
RUN npm run build
RUN cd dist; zip -r ../breeze-extension.zip *

FROM gradle:7-jdk11 AS grade-build
WORKDIR /home/server
COPY --chown=gradle:gradle ./server/*.gradle.kts ./server/gradle.properties /home/server
RUN gradle dependencies
COPY --chown=gradle:gradle ./server /home/server
COPY --from=web-build /home/web/dist /home/server/src/main/resources/web
COPY --from=extension-build /home/extension/breeze-extension.zip /home/server/src/main/resources/web
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=grade-build /home/server/build/libs/*.jar /app/breeze-server.jar
ENTRYPOINT ["java","-jar","/app/breeze-server.jar"]