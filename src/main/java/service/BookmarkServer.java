package service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;

public class BookmarkServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server =
        ServerBuilder.forPort(8080)
            .addService(new BookmarkController(new BookmarkService()))
            .addService(ProtoReflectionService.newInstance())
            .build();

    server.start();
    System.out.println("Server started!");

    server.awaitTermination();
  }
}
