# syntax=docker/dockerfile:1

# ===== Tools Stage =====
FROM amazoncorretto:11 AS tools

RUN yum install -y tar-1.26-35.amzn2.0.4 gzip-1.5-10.amzn2.0.1 && \
    yum clean all && \
    rm -rf /var/cache/yum

# ===== Build Stage =====
FROM tools AS build

WORKDIR /app

ENV MAVEN_VERSION=3.9.11
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

COPY . /app

RUN --mount=type=cache,target=/root/.m2 \
    mvn package

# ===== Run Stage =====
FROM tools

ENV TOMCAT_VERSION=9.0.111
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar xz -C /opt && \
    mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat

COPY --from=build /app/target/mail-baku.war /opt/tomcat/webapps/
RUN mkdir -p /opt/tomcat/extensions/mail-baku/WEB-INF/classes

EXPOSE 8080 1025

CMD ["/opt/tomcat/bin/catalina.sh", "run"]
