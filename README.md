# mail-baku

mail-baku is a mock / fake / dummy SMTP server that can be deployed on Tomcat.

### Requirements to Run

* Tomcat 9
* Java 8

### Requirements to Build

* JDK 8
* Maven 3.9.9

### Build

```
$ mvn package
```
The output artifact is `mail-baku.war`.

### Deploy

Place `mail-baku.war` in `$TOMCAT_HOME/webapps`.
Make the directory `$TOMCAT_HOME/extensions/mail-baku/WEB-INF/classes`.

### Configuration

Put `application.conf` to `$TOMCAT_HOME/extensions/mail-baku/WEB-INF/classes/conf/application.conf`.
The available settings are as follows:
```
# conf/application.conf
mail-baku.mail.bind.address=0.0.0.0
mail-baku.mail.port=1025
mail-baku.allowed.addresses=127.0.0.1
```

### Docker

```bash
$ docker image build -t mail-baku .
$ docker container run --rm -p 8080:8080 -p 1025:1025 mail-baku
```

build stage:

```bash
$ docker image build -t mail-baku:build --target build .
$ docker container run --rm -p 8080:8080 mail-baku:build mvn ninja:run
```
