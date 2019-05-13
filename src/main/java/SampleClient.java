import com.samples.grpc.*;
import io.grpc.Status;

import java.util.concurrent.TimeUnit;

public class SampleClient {

    private final io.grpc.ManagedChannel channel;
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;
    private final EchoServiceGrpc.EchoServiceStub asyncStub;

    public static void main(String[] args) throws Exception {
        SampleClient client = new SampleClient("localhost", 50051);
        try {
            client.echoBlocking(String.format("Hey server!"));
            client.echoAsync(String.format("Hey async server!"), 5);
        } finally {
            client.shutdown();
        }
    }

    public SampleClient(String host, int port) {
        this(io.grpc.ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (SSL/TLS). For this sample disable TLS to avoid certificate config.
                .usePlaintext()
                .build());
    }

    public SampleClient(io.grpc.ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        this.asyncStub = EchoServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void echoBlocking(String message) {
        EchoRequest request = EchoRequest
                .newBuilder()
                .setEcho(message)
                .build();

        EchoResponse response;
        try {
            System.out.println("Client send: " + message + "...");
            response = this.blockingStub.echo(request);
        } catch (io.grpc.StatusRuntimeException e) {
            System.err.println("\tRPC failed: " + e.getStatus());
            return;
        }
        System.out.println("\tClient recv: " + response.getEcho());
    }

    public void echoAsync(String message, int count) {
        EchoRequest request = EchoRequest
                .newBuilder()
                .setEcho(message)
                .setCount(count)
                .build();

        try {
            System.out.println("Client send: " + message + "...");

            this.asyncStub.echoStream(request, new io.grpc.stub.StreamObserver<>() {
                @Override
                public void onNext(EchoResponse response) {
                    System.out.println("\tClient recv: " + response.getEcho());
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("\tRPC failed: " + Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    System.out.println("Client finished.");
                }
            });

        } catch (io.grpc.StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return;
        }
    }
}
