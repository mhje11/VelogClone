package hello.velogclone.global.exception;

public class ProfileImageNotFoundException extends RuntimeException{
    public ProfileImageNotFoundException(String message) {
        super(message);
    }
}
