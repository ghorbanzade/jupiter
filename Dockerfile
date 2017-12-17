FROM openjdk:8-slim
MAINTAINER Pejman Ghorbanzade <pejman@ghorbanzade.com>

COPY . /jupiter
WORKDIR /jupiter

CMD /bin/bash
