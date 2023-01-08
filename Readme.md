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
  * 出力は Jar
  * Dependency に Spring Web と lombok を追加する。

* WSL に接続した VSCode でプロジェクトを開く

* Controller 実装を追加 (実装内容はサンプルコードを参照)

## 2. プロジェクトのローカル実行方法
### 2-1. コマンドプロンプトからの実行


以下のコマンドで、http://localhost:8080/ でローカルでサーバが立ち上がる。

``` bash
mvn clean spring-boot:run 
```

もしくは、Jarを生成して直接Javaから実行する。

``` bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

※ mvnのspring-bootプラグインが対応しているコマンドを確認する場合は、```mvn spring-boot:help``` を実行する。

### 2-2. VSCodeからの実行
* VSCodeに拡張機能をひととおり入れる
  * Java Extension Pack
  * Azure、Maven for Java
  * Spring Boot Extension pack
* VSCodeのメニューの [Run] - [Start Debugging] を実行
* なんかいい感じにVSCodeがサーバを立ち上げてくれる

## 3. Application Insights の接続文字列の設定
### 3-1. 環境変数で設定する
``` bash
export APPLICATIONINSIGHTS_CONNECTION_STRING="InstrumentationKey=249345....."
```

### 3-2. applicationinsights.json で設定する
エージェントの applicationinsights-agent-3.X.X.jar ファイルと同じディレクトリに applicationinsights.json を作成し、以下のように設定する

``` JSON
{
  "connectionString": "InstrumentationKey=249345....."
}
```
applicationinsights.json の記載方法の詳細は、以下を参照
https://learn.microsoft.com/ja-jp/azure/azure-monitor/app/java-standalone-config



## 4. Application Insights の有効化
### 4-1. App Service での自動インストルメンテーション
この場合はコード変更は不要。App Service で Application Insights を有効化すると、App Service のランタイム側で Application Insights が自動的にアタッチされる。

### 4-2. エージェントの JAR を Java VM にアタッチする
起動時に以下のコマンドで Application Insights のエージェントをアタッチする。

``` Bash
# エージェントのダウンロード
wget https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.7/applicationinsights-agent-3.4.7.jar

# アプリの実行
java -javaagent:applicationinsights-agent-3.4.7.jar -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 4-3. コードの中で Spring Boot 用の Application Insights を有効化する
以下の方法で有効化する。
https://learn.microsoft.com/ja-jp/azure/azure-monitor/app/java-spring-boot#enabling-programmatically

  * ```applicationinsights-runtime-attach``` を pom.xml に追加する。
    ``` XML
    <dependency>
      <groupId>com.microsoft.azure</groupId>
      <artifactId>applicationinsights-runtime-attach</artifactId>
      <version>3.4.3</version>
    </dependency>
    ```
  * main メソッドの最初に ```ApplicationInsights.attach();``` を追加する。
    ``` Java
    @SpringBootApplication
    public class SpringBootApp {

      public static void main(String[] args) {
        ApplicationInsights.attach();
        SpringApplication.run(SpringBootApp.class, args);
      }
    }
    ```


## 5. Azureへの発行

### 5-1. VSCode で発行する
以下のページを参照し、VSCode の拡張機能を用いてデプロイする
https://docs.microsoft.com/ja-jp/learn/modules/create-publish-webapp-app-service-vs-code/4-publish-app-azure-app-service-vs-code


### 5-2. Github 経由で発行する
Github Action を用いることで、Github のリポジトリにコードが Push されたら、自動的に App Service にデプロイできる。
具体的な手順は以下を参照

https://dev.azure.com/ASIMKnowledgeBase/ASIM%20Knowledge%20Base/_wiki/wikis/ASIM-Knowledge-Base.wiki/2981/%E3%82%A2%E3%83%97%E3%83%AA%E3%82%92-App-Service-%E3%81%AB%E3%83%87%E3%83%97%E3%83%AD%E3%82%A4%E3%81%99%E3%82%8B%E6%96%B9%E6%B3%95-(Github-Action-%E3%82%92%E7%94%A8%E3%81%84%E3%81%9F%E8%87%AA%E5%8B%95%E3%83%87%E3%83%97%E3%83%AD%E3%82%A4)


