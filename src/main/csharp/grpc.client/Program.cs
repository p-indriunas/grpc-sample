using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Grpc.Samples;
using Grpc.Core;
using Grpc.Net.Client;

namespace grpc.client
{
    public class SampleClient
    {
        static async System.Threading.Tasks.Task Main(string[] args)
        {
            var client = CreateClient();

            EchoResponse echoResponse;
            var callOptions = new CallOptions();
            try
            {
                echoResponse = CallEcho(client, callOptions);
                System.Console.WriteLine(echoResponse.Echo);

                echoResponse = await CallEchoAsync(client, callOptions);
                System.Console.WriteLine(echoResponse.Echo);

                await foreach (var currentEchoResponse in CallEchoStream(client, 5, callOptions))
                {
                    System.Console.WriteLine(currentEchoResponse.Echo);
                }
            }
            catch(Exception ex)
            {
                System.Console.WriteLine(ex.ToString());
            }

            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();
        }

        private static EchoService.EchoServiceClient CreateClient()
        {
            // https://github.com/dotnet/corefx/issues/41701
            AppContext.SetSwitch("System.Net.Http.SocketsHttpHandler.Http2UnencryptedSupport", true);
            var channelOptions = new GrpcChannelOptions()
            {
                Credentials = ChannelCredentials.Insecure
            };

            var channel = GrpcChannel.ForAddress("http://127.0.0.1:50051", channelOptions);
            var client = new EchoService.EchoServiceClient(channel);
           
            return client;
        }

        private static EchoResponse CallEcho(EchoService.EchoServiceClient client, CallOptions options)
        {
            EchoRequest echoRequest = new EchoRequest { Echo = "Hey server!" };
            EchoResponse echoResponse = client.echo(echoRequest, options);
            return echoResponse;
        }

        private static async Task<EchoResponse> CallEchoAsync(EchoService.EchoServiceClient client, CallOptions options)
        {
            EchoRequest echoRequest = new EchoRequest { Echo = "Hey server async!" };
            AsyncUnaryCall <EchoResponse> echoTask = client.echoAsync(echoRequest, options);
            return await echoTask.ResponseAsync;
        }

        private static async IAsyncEnumerable<EchoResponse> CallEchoStream(EchoService.EchoServiceClient client, uint count, CallOptions options)
        {
            EchoRequest echoRequest = new EchoRequest { Echo = "Hey server stream!", Count = count };
            AsyncServerStreamingCall<EchoResponse> echoStream = client.echoStream(echoRequest, options);
            await foreach (EchoResponse reply in echoStream.ResponseStream.ReadAllAsync())
            {
                yield return reply;
            }
            
            /*
            // Classic enumerator:
            while (await echoStream.ResponseStream.MoveNext().ConfigureAwait(false))
            {
                yield return echoStream.ResponseStream.Current;
            }
            */
        }
    }
}
