import com.samples.grpc.*;
import io.grpc.stub.StreamObserver;

public class SampleServer {

    private io.grpc.Server server;

    public static void main(String[] args) throws java.io.IOException, InterruptedException {
        final SampleServer server = new SampleServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws java.io.IOException {
        int port = 50051;                       // <- the port on which the server should run
        this.server = io.grpc.ServerBuilder.forPort(port)
                .addService(new EchoService())  // <- add more services as you need
                .build()
                .start();
        System.out.println("*** Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** Shutting down gRPC server since JVM is shutting down...");
                SampleServer.this.stop();
                System.err.println("*** Server shut down.");
            }
        });
    }

    private void stop() {
        if (this.server != null) {
            this.server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            // Await termination on the main thread since the grpc library uses daemon threads.
            this.server.awaitTermination();
        }
    }

    public class EchoService extends com.samples.grpc.EchoServiceGrpc.EchoServiceImplBase {

        @Override
        public void echo(EchoRequest request, io.grpc.stub.StreamObserver<EchoResponse> responseObserver) {
            String echo = request.getEcho();
            System.out.println("Server: \"" + echo + "\"");

            // Respond with the received echo message:
            EchoResponse response = EchoResponse.newBuilder()
                    .setEcho(echo)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void echoStream(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            String echo = request.getEcho();
            int count = request.getCount();
            System.out.println("Server: \"" + echo + "\": " + count);

            try {
                // Stream with the received echo message specified amount of times:
                for (int i = 0; i < count; i++) {
                    EchoResponse response = EchoResponse.newBuilder()
                            .setEcho(echo)
                            .build();
                    responseObserver.onNext(response);
                    Thread.sleep(1000);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            responseObserver.onCompleted();
        }
    }
}
