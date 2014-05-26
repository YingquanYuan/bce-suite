bceclient
=========

This sub-project contains a streaming media client for **Broadcast Encryption Server**.

### How To Build

``` bash
mvn clean package
```


### Run

``` bash
java -d32 -XstartOnFirstThread -cp target/bceclient-jar-with-dependencies.jar bce.client.player.BCEClient
```
