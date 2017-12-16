FROM openjdk:8
MAINTAINER Pejman Ghorbanzade <pejman@ghorbanzade.com>

COPY . /jupiter
WORKDIR /jupiter

CMD /bin/bash
