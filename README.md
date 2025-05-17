# mail-baku

mail-baku is a mock / fake / dummy SMTP server that can be deployed on Tomcat.

### Requirements to Run

* Tomcat 8+
* Java 8+

### Requirements to Build

* JDK 8+
* Maven 3.3.9+

### Build

```
$ mvn package
```
The output artifact is `mail-baku.war`.

### Deploy

Place `mail-baku.war` in `$TOMCAT_HOME/webapps`.
Configure AJP as needed.

### Configuration

Supports `$TOMCAT_HOME/extensions/`. The available settings are as follows:
```
# conf/application.conf
mail-baku.mail.bind.address=0.0.0.0
mail-baku.mail.port=1025
mail-baku.allowed.addresses=127.0.0.1
```

