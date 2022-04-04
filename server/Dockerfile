FROM gitlab.acrobits.cz:5050/csp2/dockerfiles/java-11-jre
WORKDIR /
ADD ./docker-start.sh /
ADD ./build/libs/server.jar /
ADD VERSION /
EXPOSE 8117
ENTRYPOINT /docker-start.sh
