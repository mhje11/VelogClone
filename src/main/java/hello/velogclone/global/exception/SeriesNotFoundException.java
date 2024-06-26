package hello.velogclone.global.exception;

public class SeriesNotFoundException extends RuntimeException{
    public SeriesNotFoundException(String message) {
        super(message);
    }
}
