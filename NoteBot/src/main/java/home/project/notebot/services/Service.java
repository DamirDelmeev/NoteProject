package home.project.notebot.services;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
import home.project.notebot.services.template.RequestRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Service {

    @Autowired
    private final RequestRunner requestRunner;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

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
        User userForCheck = requestRunner.runnerCheckLogin(userText);
        if (userForCheck != null && userForCheck.getLogin().equals(userText)) {
            return true;
        } else return false;
    }

    public boolean checkPasswordFromDB(String userText, User user) {
        User userForCheck = requestRunner.runnercheckPassword(userText, user);
        if (userForCheck != null && passwordEncoder.matches(userText, userForCheck.getPassword())
                && userForCheck.getLogin().equals(user.getUserInLogin())) {
            return true;
        } else {
            return false;
        }

    }

    public Cell getCellForContent(Long userId) {
        return requestRunner.runnerGetCellForContent(userId);
    }
}
