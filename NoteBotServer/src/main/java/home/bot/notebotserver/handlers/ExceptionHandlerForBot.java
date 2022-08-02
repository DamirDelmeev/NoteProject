package home.bot.notebotserver.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerForBot {
    @ExceptionHandler
    public void handleException(Exception exception) {
        log.debug("log message: {}", exception.getMessage());
    }
}
