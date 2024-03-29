package home.project.notebot.services;

import home.project.notebot.entity.Cell;
import home.project.notebot.entity.Users;
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
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Service {

    @Autowired
    private final RequestRunner requestRunner;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Users getUser(long id) {
        return requestRunner.runnerGetSearchUser(id);
    }

    public void putUser(Users users) {
        requestRunner.runnerPutUser(users);
    }

    public void putCell(Cell cell) {
        requestRunner.runnerPutCell(cell);
    }

    public boolean checkLoginFromDB(String userText) {
        Users usersForCheck = requestRunner.runnerCheckLogin(userText);
        return usersForCheck != null && usersForCheck.getLogin().equals(userText);
    }

    public boolean checkPasswordFromDB(String userText, Users users) {
        Users usersForCheck = requestRunner.runnercheckPassword(users.getLogin());
        return usersForCheck != null && passwordEncoder.matches(userText, usersForCheck.getPassword())
                && usersForCheck.getLogin().equals(users.getUserInLogin());
    }

    public Cell getCellForContent(Long userId) {
        return requestRunner.runnerGetCellForContent(userId);
    }

    public SendPhoto sendPhoto(Long longId, byte[] img) {
        InputStream inputStream;
        try {
            String pathIn = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "img.png").getPath();
            Path path = Paths.get(pathIn);
            Files.readAllBytes(path);
            inputStream = new ByteArrayInputStream(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(inputStream, "picture.png"));
        sendPhoto.setChatId(String.valueOf(longId));
        return sendPhoto;
    }

    public List<Cell> getListCell(Long userId) {
        Cell[] listCell = requestRunner.getListCell(userId);
        return Arrays.asList(listCell);
    }

    public void deleteCell(Integer id) {
        requestRunner.deleteCell(id);
    }

}
