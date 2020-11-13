package service;

import io.grpc.stub.StreamObserver;
import proto.BookmarkControllerGrpc;
import proto.BookmarkResponse;
import service.excpetion.ConnectionFailedException;

public class BookmarkController extends BookmarkControllerGrpc.BookmarkControllerImplBase {

  private final BookmarkService bookmarkService;

  public BookmarkController(BookmarkService bookmarkService) {
    this.bookmarkService = bookmarkService;
  }

  public void addBookmark(
      proto.BookmarkRequest request, StreamObserver<BookmarkResponse> responseObserver) {

    var response = BookmarkResponse.newBuilder();
    try {
      var responseStatus = bookmarkService.process(request);
      response.setStatus(responseStatus);
    } catch (ConnectionFailedException ex) {
      response.setStatus(ex.getMessage());
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
