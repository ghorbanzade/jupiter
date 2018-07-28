FROM openjdk:8-slim
label maintainer="pejman@ghorbanzade.com"

RUN groupadd -r jupiter && useradd --no-log-init -r -g jupiter jupiter

COPY . /home/jupiter/app
RUN chown -v -R jupiter:jupiter /home/jupiter
WORKDIR /home/jupiter/app
USER jupiter

RUN ./gradlew build

CMD /bin/bash
