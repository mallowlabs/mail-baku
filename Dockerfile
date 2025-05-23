# syntax=docker/dockerfile:1

# ===== Tools Stage =====
FROM amazoncorretto:8 AS tools

RUN yum install -y tar gzip && \
    yum clean all && \
    rm -rf /var/cache/yum

# ===== Build Stage =====
FROM tools AS build

WORKDIR /app

ENV MAVEN_VERSION=3.9.9
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

COPY . /app

RUN --mount=type=cache,target=/root/.m2 \
    mvn package

# ===== Run Stage =====
FROM tools

ENV TOMCAT_VERSION=9.0.105
RUN curl -fsSL https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar xz -C /opt && \
    mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat

COPY --from=build /app/target/mail-baku.war /opt/tomcat/webapps/
RUN mkdir -p /opt/tomcat/extensions/mail-baku/WEB-INF/classes

EXPOSE 8080 1025

CMD ["/opt/tomcat/bin/catalina.sh", "run"]
