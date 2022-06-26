package home.project.notebot.services.template;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestRunner {
    private final RestTemplate restTemplate;
    @Value("${telegram.host-url}")
    private String url;

    public Cell runnerGetSearchCell(long longId) {
        log.info("log message: {}", "Пользователь нажал поиск");
        return restTemplate
                .getForObject(url + "cell/{id}", Cell.class, longId);
    }

    public User runnerGetSearchUser(long longId) {
        log.info("log message: {}", "Пользователь нажал юзер поиск");
        return restTemplate
                .getForObject(url + "user/{id}", User.class, longId);
    }

    public void runnerPutUser(User user) {
        log.info("log message: {}", "Пользователь нажал add user");
        restTemplate.postForObject(url + "user", user, String.class);
    }

    public void runnerPutCell(Cell cell) {
        log.info("log message: {}", "Пользователь нажал add cell");
        restTemplate.postForObject(url + "cell", cell, String.class);
    }

    public boolean runnerCheckLogin(String userText) {
        User userForCheck = restTemplate.getForObject(url + "login/{text}", User.class, userText);
        if (userForCheck != null && userForCheck.getLogin().equals(userText)) {
            return true;
        } else return false;
    }

    public boolean runnercheckPassword(String userText, User user) {
        User userForCheck = restTemplate.getForObject(url + "password/{text}", User.class, userText);
        if (userForCheck != null && userForCheck.getPassword().equals(userText) && userForCheck.getLogin().equals(user.getUserInLogin())) {
            return true;
        } else return false;
    }
}