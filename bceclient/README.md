bceclient
=========

This sub-project contains a streaming media client for **Broadcast Encryption Server**.

### How To Build

``` bash
mvn clean package (install)
```


### Run

* Linux (**MUST** be 32-bit Java)
``` bash
java -d32 -cp target/bceclient-jar-with-dependencies.jar bce.client.player.BCEClient
```

* Mac OS X
``` bash
java -d32 -XstartOnFirstThread -cp target/bceclient-jar-with-dependencies.jar bce.client.player.BCEClient
```
