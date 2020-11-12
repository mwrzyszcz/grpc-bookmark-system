package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import service.excpetion.ConnectionFailedException;

public class BookmarkService {

  private static final String LISTING = " * ";
  private static final String TITLE = "## ";
  private static final String NEXT_LINE = "\n";
  private static final String SUCCESS_RESPONSE = "OK. Bookmark added.";
  private static final String DESCRIPTION = "description";
  private static final String META = "meta";
  private static final String NAME = "name";
  private static final String CONNECTION_EXCEPTION_MESSAGE = "Cannot connect with given URL";

  static String process(proto.BookmarkRequest bookmarkRequest) throws ConnectionFailedException {

    var document =
        getDocument(bookmarkRequest)
            .orElseThrow(() -> new ConnectionFailedException(CONNECTION_EXCEPTION_MESSAGE));

    var title = document.title();

    var tags = bookmarkRequest.getTags().replace(" ", ", ");

    var description = extractDescription(document);

    Path filePath = Paths.get("target\\bookmarks.md");

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append(TITLE)
        .append(title)
        .append(NEXT_LINE)
        .append(LISTING)
        .append(bookmarkRequest.getUrl())
        .append(NEXT_LINE)
        .append(LISTING)
        .append(tags)
        .append(NEXT_LINE)
        .append(LISTING)
        .append(description);
    try {
      Files.writeString(filePath, stringBuilder.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return SUCCESS_RESPONSE;
  }

  private static String extractDescription(Document document) {
    return document.getElementsByTag(META).stream()
        .filter(element -> DESCRIPTION.equals(element.attr(NAME)))
        .map(element -> element.attr("content"))
        .findFirst()
        .orElse("");
  }

  private static Optional<Document> getDocument(proto.BookmarkRequest bookmarkRequest) {
    Document document;
    try {
      Connection connect = HttpConnection.connect(bookmarkRequest.getUrl());
      connect.timeout(1500);
      if (isWrongResponse(connect)) {
        return Optional.empty();
      }
      document = connect.get();
    } catch (IOException e) {
      return Optional.empty();
    }
    return Optional.ofNullable(document);
  }

  private static boolean isWrongResponse(Connection connect) {
    return connect.response().statusCode() == 404 || connect.response().statusCode() == 500;
  }
}
