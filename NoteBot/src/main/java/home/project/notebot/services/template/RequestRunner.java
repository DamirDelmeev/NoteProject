package home.project.notebot.services.template;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestRunner {
    @Autowired
    private final RestTemplate restTemplate;


    @Value("${telegram.host-url}")
    private String url;

    public Users runnerGetSearchUser(Long longId) {
        return restTemplate
                .getForObject(url + "user/{id}", Users.class, longId);
    }

    public void runnerPutUser(Users users) {
        restTemplate.postForObject(url + "user", users, String.class);
    }

    public void runnerPutCell(Cell cell) {
        restTemplate.postForObject(url + "cell", cell, String.class);
    }

    public Users runnerCheckLogin(String userText) {
        return restTemplate.getForObject(url + "login/{text}", Users.class, userText);

    }

    public Users runnercheckPassword(String userText) {
        return restTemplate.getForObject(url + "login/{text}", Users.class, userText);
    }

    public Cell runnerGetCellForContent(Long userId) {
        return restTemplate
                .getForObject(url + "cell/content/{id}", Cell.class, userId);
    }

    public Cell[] getListCell(Long userId) {
        return restTemplate.getForObject(url + "cell/{id}/all", Cell[].class, userId);
    }

    public void deleteCell(Integer cellId) {
        restTemplate.delete(url + "cell/{id}", cellId);
    }
}