FROM mozilla/sbt
MAINTAINER Shreyas
WORKDIR /app
RUN apt-get update && apt-get -y install dos2unix
COPY . .
EXPOSE 80 9021 8021 10021
ENTRYPOINT ["sh", "./start.sh"]