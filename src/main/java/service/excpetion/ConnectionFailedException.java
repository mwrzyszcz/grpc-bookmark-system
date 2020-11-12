package service.excpetion;

public class ConnectionFailedException extends RuntimeException {

  public ConnectionFailedException(String message) {
    super(message);
  }
}
