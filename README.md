# mail-baku
mail-baku は、Tomcat にデプロイできる mock / fake / dummy SMTP サーバです。

### 実行に必要な要件
* Tomcat 7+
* Java 8+

### ビルドに必要な要件
* JDK 8+
* Maven 3.3.9+

### ビルド

```
$ mvn package
```
`mail-baku.war` が成果物です。

### デプロイ
`$TOMCAT_HOME/webapps` に `mail-baku.war` を置いて下さい。
必要に応じて AJP の設定をしてください。

### 設定
`$TOMCAT_HOME/extensions/` に対応しています。有効な設定は以下です。
```
# application.conf
mail-baku.mail.bind.address=0.0.0.0
mail-baku.mail.port=1025
mail-baku.allowed.addresses=127.0.0.1
```

