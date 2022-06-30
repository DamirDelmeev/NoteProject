package home.project.notebot.services;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
import home.project.notebot.services.template.RequestRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public SendPhoto sendPhoto(Long longId,byte[] img) {
        InputStream inputStream = null;
        try {
            String pathIn = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "img.png").getPath();

            Path path = Paths.get(pathIn);


            byte[] bytes = Files.readAllBytes(path);

            inputStream = new ByteArrayInputStream(img);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(inputStream, "picture.png"));
        sendPhoto.setChatId(String.valueOf(longId));
        return sendPhoto;
    }
}
