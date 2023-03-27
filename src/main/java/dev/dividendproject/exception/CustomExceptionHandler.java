package dev.dividendproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e){
        ErrorResponse errorResponse = ErrorResponse.builder()
                                    .code(e.getStatusCode())
                                    .message(e.getMessage())
                                    .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));
    }
}

/*
* 로그레벨
* DEBUG
* INFO
* WARN
* ERROR
*
* Log를 어디로 남길지 :
* Console
* File(사이즈 고려해야 함, 일정 크기가 벗어나지 않도록 로테이션 혹은 기록기간 고려)
* 중앙화
* */
