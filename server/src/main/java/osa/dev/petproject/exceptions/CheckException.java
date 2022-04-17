package osa.dev.petproject.exceptions;

import org.springframework.http.HttpStatus;

public class CheckException extends Exception {

    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }

    public CheckException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
