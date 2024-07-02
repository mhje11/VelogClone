package hello.velogclone.global.exception;

public class PostImageNotFoundException extends RuntimeException{
    public PostImageNotFoundException (String message) {
        super(message);
    }
}
