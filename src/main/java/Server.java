import io.grpc.stub.*;
import com.samples.grpc.*;

public class Server {

    public static void main(String[] args) {

        System.out.println("Server...");
    }

    public class EchoService extends com.samples.grpc.EchoServiceGrpc.EchoServiceImplBase {

        public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {

            // Not implemented:
            ServerCalls.asyncUnimplementedUnaryCall(EchoServiceGrpc.getEchoMethod(), responseObserver);
        }
    }
}
