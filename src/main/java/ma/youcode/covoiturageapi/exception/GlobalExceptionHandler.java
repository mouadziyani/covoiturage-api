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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException argumentNotValidException, HttpServletRequest request) {
        String message = argumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "validation-error", message, request);
    }

    private ProblemDetail build(HttpStatus status, String type, String message, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
        problem.setType(URI.create("/errors/" + type));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

}
