package home.project.notebot.services;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.template.RequestRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Service {
    private final ReplyKeyboardMaker replyKeyboardMaker;
    private final RequestRunner requestRunner;

    public Cell getCell(long id) {
        return requestRunner.runnerGetSearchCell(id);
    }

    public User getUser(long id) {
        return requestRunner.runnerGetSearchUser(id);
    }

    public void putUser(User user) {
        requestRunner.runnerPutUser(user);
    }

    public void putCell(Cell cell) {
        requestRunner.runnerPutCell(cell);
    }

    public boolean checkLoginFromDB(String userText) {
        return requestRunner.runnerCheckLogin(userText);
    }

    public boolean checkPasswordFromDB(String userText, User user) {
        return requestRunner.runnercheckPassword(userText, user);
    }
}
