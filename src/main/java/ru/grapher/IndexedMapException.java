package ru.grapher;

public class IndexedMapException extends Exception {

    public IndexedMapException() {
        super();
    }

    public IndexedMapException(Throwable cause) {
        super(cause);
    }

    public IndexedMapException(String message, Throwable cause) {
        super(message, cause);
    }
}

class IndexedMapAlreadyInitializedException extends IndexedMapException {

    public IndexedMapAlreadyInitializedException() {
        super();
    }

    public IndexedMapAlreadyInitializedException(Throwable cause) {
        super(cause);
    }

    public IndexedMapAlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}

class IndexedMapNotInitializedException extends IndexedMapException {
    public IndexedMapNotInitializedException() {
        super();
    }

    public IndexedMapNotInitializedException(Throwable cause) {
        super(cause);
    }

    public IndexedMapNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}