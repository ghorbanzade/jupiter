FROM openjdk:8-slim
MAINTAINER Pejman Ghorbanzade <pejman@ghorbanzade.com>

RUN apt-get update && apt-get install -y \
  jq \
&& rm -rf /var/lib/apt/lists/*

COPY . /jupiter
WORKDIR /jupiter

CMD /bin/bash
