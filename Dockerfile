FROM openjdk:8-slim
label maintainer="pejman@ghorbanzade.com"

COPY . /jupiter
WORKDIR /jupiter

CMD /bin/bash
