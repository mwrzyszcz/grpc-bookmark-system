package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import service.excpetion.ConnectionFailedException;

class BookmarkService {

  private static final String LISTING = " * ";
  private static final String TITLE = "## ";
  private static final String NEXT_LINE = "\n";
  private static final String DESCRIPTION = "description";
  private static final String META = "meta";
  private static final String NAME = "name";
  private static final String CONNECTION_EXCEPTION_MESSAGE = "Cannot connect with given URL";
  private static final String SUCCESS_RESPONSE = "OK. Bookmark added.";

  String process(proto.BookmarkRequest bookmarkRequest) throws ConnectionFailedException {

    var document =
        getDocument(bookmarkRequest)
            .orElseThrow(() -> new ConnectionFailedException(CONNECTION_EXCEPTION_MESSAGE));

    var title = document.title();
    var tags = formatTags(bookmarkRequest);
    var description = extractDescription(document);

    var filePath = Paths.get("target\\bookmarks.md");

    var stringBuilder = new StringBuilder();
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

  String renderHTML() {

    var path = Paths.get("target\\bookmarks.md");

    String readString = null;
    try {
      if (Files.notExists(path)) Files.createFile(path);
      readString = Files.readString(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    var parser = Parser.builder().build();
    var htmlRenderer = HtmlRenderer.builder().build();
    return Optional.ofNullable(readString).map(parser::parse).map(htmlRenderer::render).orElse("");
  }

  private String extractDescription(Document document) {
    return document.getElementsByTag(META).stream()
        .filter(element -> DESCRIPTION.equals(element.attr(NAME)))
        .map(element -> element.attr("content"))
        .findFirst()
        .orElse("");
  }

  private Optional<Document> getDocument(proto.BookmarkRequest bookmarkRequest) {
    Document document;
    try {
      var connect = HttpConnection.connect(bookmarkRequest.getUrl());
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

  private String formatTags(proto.BookmarkRequest bookmarkRequest) {
    return bookmarkRequest.getTags().replace(" ", ", ");
  }

  private boolean isWrongResponse(Connection connect) {
    return connect.response().statusCode() == 404 || connect.response().statusCode() == 500;
  }
}
