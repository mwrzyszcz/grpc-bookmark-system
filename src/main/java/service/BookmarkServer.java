package service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import proto.BookmarkRequest;
import proto.BookmarkResponse;
import service.excpetion.ConnectionFailedException;

public class BookmarkServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server =
        ServerBuilder.forPort(8080)
            .addService(new BookmarkServiceImpl())
            .addService(ProtoReflectionService.newInstance())
            .build();

    System.out.println("Starting server...");
    server.start();
    System.out.println("Server started!");

    server.awaitTermination();
  }

  public static class BookmarkServiceImpl
      extends proto.BookmarkServiceGrpc.BookmarkServiceImplBase {
    public void addBookmark(
        proto.BookmarkRequest request, StreamObserver<proto.BookmarkResponse> responseObserver) {

      var bookmarkRequest =
          BookmarkRequest.newBuilder().setUrl(request.getUrl()).setTags(request.getTags()).build();

      var response = BookmarkResponse.newBuilder();
      try {
        var responseStatus = BookmarkService.process(bookmarkRequest);
        response.setStatus(responseStatus);
      } catch (ConnectionFailedException ex) {
        response.setStatus(ex.getMessage());
      }

      responseObserver.onNext(response.build());
      responseObserver.onCompleted();
    }
  }
}
