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
org.vlegchilkin.filepreviewer.Main
```
