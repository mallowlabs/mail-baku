# mail-baku

mail-baku is a mock / fake / dummy SMTP server built with [Quarkus](https://quarkus.io/).

### Requirements to Run

* Java 17

### Requirements to Build

* JDK 17
* Maven 3.9.11

### Build

```
$ mvn package
```

The output artifact is `target/quarkus-app/`.

### Run

```
$ java -jar target/quarkus-app/quarkus-run.jar
```

The web UI is served on `http://localhost:8080/` and the SMTP server listens on port `1025`.

For development, use the Quarkus dev mode (live reload):

```
$ mvn quarkus:dev
```

### Configuration

The available settings are as follows:

```properties
mail-baku.mail.bind.address=0.0.0.0
mail-baku.mail.port=1025
mail-baku.allowed.addresses=127.0.0.1
```

Settings can be given as environment variables
(e.g. `MAIL_BAKU_MAIL_PORT=2525`), or via an external properties file:

```
$ QUARKUS_CONFIG_LOCATIONS=/path/to/application.properties java -jar target/quarkus-app/quarkus-run.jar
```

### Docker

```bash
$ docker image build -t mail-baku .
$ docker container run --rm -p 8080:8080 -p 1025:1025 mail-baku
```
