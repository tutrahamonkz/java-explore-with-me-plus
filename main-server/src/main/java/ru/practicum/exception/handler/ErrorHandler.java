package ru.practicum.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.DataAlreadyInUseException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseEntity<ErrorResponse> response = getResponseEntity(status, "Объект не найден",
                e.getMessage(), nowTime);
        logging(e, status, nowTime);

        return response;
    }

    // Перехватывает исключение из базы данных, при неверных параметрах в запросах
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;
        ResponseEntity<ErrorResponse> response = getResponseEntity(status, "Нарушение целостности данных",
                e.getMessage(), nowTime);
        logging(e, status, nowTime);

        return response;
    }

    // Перехватывает исключения при валидации с помощью jakarta.validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseEntity<ErrorResponse> response = getResponseEntity(status, "Запрос составлен некорректно",
                e.getMessage(), nowTime);
        logging(e, status, nowTime);

        return response;
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataAlreadyInUseException e){
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.BAD_REQUEST; //возможно статус должен быть другим
        ResponseEntity<ErrorResponse> response = getResponseEntity(status,
                "Исключение, связанное с нарушением целостности данных", e.getMessage(), nowTime);
        logging(e, status, nowTime);

        return response;
    }

    // Метод для составления ответа при возникновении исключения
    private ResponseEntity<ErrorResponse> getResponseEntity(HttpStatus status, String reason, String message,
                                                            String timestamp) {
        return new ResponseEntity<>(new ErrorResponse(status.name(), reason, message, timestamp), status);
    }

    // Метод для логгирования исключений
    private void logging(Exception e, HttpStatus status, String timestamp) {
        log.error("{} - Status: {}, Description: {}, Timestamp: {}", e.getClass(), status,
                e.getMessage(), timestamp);
    }
}