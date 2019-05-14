# grpc-sample
This repository contains a gRPC client-server sample java application (based on maven project). It demonstrates essential basic concepts of grpc:
- defining a protobuf [contract](https://github.com/sargex/grpc-sample/blob/master/src/main/proto/EchoService.proto)
- compiling it to java classes using [maven build](https://github.com/sargex/grpc-sample/blob/master/pom.xml) plugin
- implementing [server](https://github.com/sargex/grpc-sample/blob/master/src/main/java/SampleServer.java) logic by extending generated service stub
- implementing [client](https://github.com/sargex/grpc-sample/blob/master/src/main/java/SampleClient.java) with different calling methods (unary, streaming)

### Troubleshooting

#### Unsafe startup warning
If you run the project you might get the following startup warning:
```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.protobuf.UnsafeUtil (file:/%USERPROFILE%/repository/com/google/protobuf/protobuf-java/3.4.0/protobuf-java-3.4.0.jar) to field java.nio.Buffer.address
WARNING: Please consider reporting this to the maintainers of com.google.protobuf.UnsafeUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```
This is a known JDK and protobuf related issue: https://github.com/protocolbuffers/protobuf/issues/3781
To suppress this warning you need to add the folowing JVM option to command line:  
`--add-opens=java.base/java.nio=ALL-UNNAMED`

It is already included in the IntelliJ IDEA configuration files under `.idea/runConfigurations`.
