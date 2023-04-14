# Application Insights Sample Code
## Target
* Java 11 
* Spring Boot
* Maven
* Application Insights Agent


## 1. プロジェクトの作り方
***WSL 上の Ubuntu で開発する場合***

* Spring Initializrでアプリのスケルトンを作成   
  https://start.spring.io/
  * 出力は War
  * 実行環境は Tomcat
  * Dependency に Spring Web と lombok を追加する。

* WSL に接続した VSCode でプロジェクトを開く

* Controller 実装を追加 (実装内容はサンプルコードを参照)

* SpringBootServletInitializer を追加

* pom.xml に以下を追加
```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <exclusions>
        <exclusion>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <dependency>
      <groupId>com.microsoft.azure</groupId>
      <artifactId>applicationinsights-web-auto</artifactId>
      <version>2.6.4</version>
    </dependency>

    <dependency>
      <groupId>com.microsoft.azure</groupId>
      <artifactId>applicationinsights-logging-log4j2</artifactId>
      <version>[2.0,)</version>
    </dependency>
```
* src/main/resources に log4j2.xml と ApplicationInsights.xml を追加
 

## 2. プロジェクトのローカル実行方法
### 2-1. コマンドプロンプトからの実行


以下のコマンドで、http://localhost:8080/ でローカルでサーバが立ち上がる。

``` bash
mvn clean spring-boot:run 
```

もしくは、Jarを生成して直接Javaから実行する。

``` bash
mvn package
java -jar target\spring-boot-0.0.1-SNAPSHOT.jar 
```


もしくは、生成された War を Tomcat にデプロイする。

※ mvnのspring-bootプラグインが対応しているコマンドを確認する場合は、```mvn spring-boot:help``` を実行する。

## 3. Application Insights のIKeyの設定
Applicationinsights.xml に設定する。

## 5. Azureへの発行
### 5-1. VSCode で発行する
以下のページを参照し、VSCode の拡張機能を用いて War ファイルをデプロイする
https://docs.microsoft.com/ja-jp/learn/modules/create-publish-webapp-app-service-vs-code/4-publish-app-azure-app-service-vs-code


