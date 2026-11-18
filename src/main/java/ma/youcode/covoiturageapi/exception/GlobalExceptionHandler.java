package ma.youcode.covoiturageapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFoundHandler(NotFoundException notFound , HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, "not found", notFound.getMessage(),request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail invalidRequestHandler(InvalidRequestException invalidRequest , HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST, "invalid request", invalidRequest.getMessage(),request);
    }

    @ExceptionHandler(ConflictException.class)
    public  ProblemDetail conflictHandler(ConflictException conflict, HttpServletRequest request){
        return build(HttpStatus.CONFLICT, "conflict", conflict.getMessage(),request);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public  ProblemDetail forbiddenAction(ForbiddenActionException forbiddenAction, HttpServletRequest request){
        return build(HttpStatus.FORBIDDEN, "forbidden", forbiddenAction.getMessage(),request);
    }

}
