# syntax=docker/dockerfile:1
# check=error=true

# ===== Tools Stage =====
FROM amazoncorretto:17-al2023@sha256:a689595bb1ce147a77e019e44d600cb4c532031df20aa07d576b49b18e2dffed AS tools

RUN dnf update -y --security && \
    dnf install -y tar-1.34 gzip-1.12 && \
    dnf clean all && \
    rm -rf /var/cache/dnf

# ===== Build Stage =====
FROM tools AS build

WORKDIR /app

ENV MAVEN_VERSION=3.9.11
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL --retry 5 --retry-connrefused --retry-delay 5 --connect-timeout 5 \
    https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

COPY . /app

RUN --mount=type=cache,target=/root/.m2 \
    mvn package --batch-mode --no-transfer-progress -DskipTests

# ===== Run Stage =====
FROM tools

ENV TOMCAT_VERSION=9.0.111
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL --retry 5 --retry-connrefused --retry-delay 5 --connect-timeout 5 \
    https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar xz -C /opt && \
    mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat

RUN dnf install -y shadow-utils-4.9 && \
    groupadd -r -g 1000 tomcat && \
    useradd -r -u 1000 -g tomcat -d /opt/tomcat -s /sbin/nologin tomcat && \
    dnf remove -y shadow-utils && \
    dnf autoremove -y && \
    dnf clean all && \
    rm -rf /var/cache/dnf

COPY --from=build /app/target/mail-baku.war /opt/tomcat/webapps/
RUN mkdir -p /opt/tomcat/extensions/mail-baku/WEB-INF/classes && \
    chown -R tomcat:tomcat /opt/tomcat

EXPOSE 8080 1025

USER tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
