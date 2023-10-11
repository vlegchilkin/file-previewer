# Demo app on Swing (File-Previewer)

Cross-platform file browser with an image / text preview capabilities. 
Treats archives (zip) as folders with special icon. 
Walking through nested archives is also supported.   

### Build an executable jar with all libraries packed:
```bash
mvn clean package
java -jar target/filepreviewer-1.0-SNAPSHOT.jar  
```

### Entry point:
```
-Xmx500m
--add-opens java.base/sun.nio.ch=ALL-UNNAMED
--add-opens jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED
--add-opens java.base/java.nio.channels.spi=ALL-UNNAMED

org.vlegchilkin.filepreviewer.Main
```


