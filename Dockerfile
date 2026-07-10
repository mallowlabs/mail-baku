# syntax=docker/dockerfile:1
# check=error=true

# ===== Tools Stage =====
FROM public.ecr.aws/amazonlinux/amazonlinux:2023.12.20260706.1@sha256:fa9b7970f7141cbc63846a1110e4ac8ef6d0103d971bd3e68e60e517bad5576a AS tools

ARG version=17.0.19.10-1
ARG package_version=1

SHELL ["/bin/bash", "-o", "pipefail", "-c"]
# hadolint ignore=DL3041
RUN set -eux && \
    ARCH="$(rpm --query --queryformat='%{ARCH}' rpm)" && \
    rpm --import file:///etc/pki/rpm-gpg/RPM-GPG-KEY-amazon-linux-2023 && \
    echo "localpkg_gpgcheck=1" >> /etc/dnf/dnf.conf && \
    CORRETO_TEMP=$(mktemp -d) && \
    pushd "${CORRETO_TEMP}" && \
    RPM_LIST=("java-17-amazon-corretto-headless-$version.amzn2023.${package_version}.${ARCH}.rpm") && \
    for rpm in "${RPM_LIST[@]}"; do \
        curl --fail -O "https://corretto.aws/downloads/resources/$(echo $version | tr '-' '.')/${rpm}" && \
        rpm -K "${CORRETO_TEMP}/${rpm}" | grep -F "${CORRETO_TEMP}/${rpm}: digests signatures OK"; \
    done && \
    dnf install -y "${CORRETO_TEMP}"/*.rpm && \
    popd && \
    rm -rf "/usr/lib/jvm/java-17-amazon-corretto.${ARCH}/lib/src.zip" && \
    rm -rf "${CORRETO_TEMP}" && \
    dnf clean all && \
    sed -i '/localpkg_gpgcheck=1/d' /etc/dnf/dnf.conf

ENV LANG=C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto

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

ENV TOMCAT_VERSION=9.0.118
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL --retry 5 --retry-connrefused --retry-delay 5 --connect-timeout 5 \
    https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar xz -C /opt && \
    mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat && \
    rm -rf /opt/tomcat/webapps/ROOT \
           /opt/tomcat/webapps/manager \
           /opt/tomcat/webapps/host-manager \
           /opt/tomcat/webapps/docs \
           /opt/tomcat/webapps/examples

RUN dnf install -y shadow-utils-4.9 && \
    groupadd -r -g 1000 tomcat && \
    useradd -r -u 1000 -g tomcat -d /opt/tomcat -s /sbin/nologin tomcat && \
    dnf remove -y shadow-utils && \
    dnf autoremove -y && \
    dnf clean all && \
    rm -rf /var/cache/dnf

COPY --from=build /app/target/mail-baku.war /opt/tomcat/webapps/
COPY docker/ROOT/ /opt/tomcat/webapps/ROOT/
RUN mkdir -p /opt/tomcat/extensions/mail-baku/WEB-INF/classes && \
    chown -R tomcat:tomcat /opt/tomcat

EXPOSE 8080 1025

USER tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
