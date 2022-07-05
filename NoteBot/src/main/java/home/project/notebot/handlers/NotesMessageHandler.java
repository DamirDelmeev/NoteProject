package home.project.notebot.handlers;

import home.project.notebot.bot.NoteBot;
import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotesMessageHandler {
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;
    @Autowired
    Service service;


    public SendMessage getrResultRegistrationAndLogIn(User user, State online, SendMessage sendMessage, Long userId,
                                                      String text,
                                                      List<String> ADD_NOTE) {
        user.setState(online.getNameState());
        service.putUser(user);
        return this.getInfoMessage(userId, text,
                ADD_NOTE);
    }

    public SendMessage getInfoMessage(Long userId, String text, List<String> LOG_IN) {
        SendMessage sendMessage = new SendMessage(userId.toString(), text);
        sendMessage.setReplyMarkup(replyKeyboardMaker
                .getKeyboard(LOG_IN));
        return sendMessage;
    }

    public SendMessage showInfoAfterChooseAddButtton(Long userId, User user, String userText) {
        if (userText.equals(ButtonName.ADD_NOTE.getNameButton())) {
            user.setState(State.ADD_TITLE.getNameState());
            service.putUser(user);
            return new SendMessage(userId.toString(), "Введите тему вашей заметки.");
        } else {
            return this
                    .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                            "Используйте кнопки ниже.",
                            Arrays.asList
                                    (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                                            ButtonName.DELETE_NOTE.getNameButton()));
        }
    }

    public SendMessage deleteNoteAfterChoose(Long userId, User user, String userText) {
        Integer userChoose = null;
        try {
            userChoose = Integer.valueOf(userText);
            List<Cell> cells = service.getListCell(userId);
            service.deleteCell(cells.get(userChoose).getId());
            SendMessage sendMessage = new SendMessage
                    (userId.toString(), "Вы успешно удалили запись");
            sendMessage.setReplyMarkup(replyKeyboardMaker.getKeyboard(Arrays.asList
                    (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                            ButtonName.DELETE_NOTE.getNameButton())));
            user.setState(State.ONLINE.getNameState());
            service.putUser(user);
            return sendMessage;
        } catch (RuntimeException r) {
            return new SendMessage
                    (userId.toString(), "Введите номер записи которую хотели бы удалить из списка.");
        }
    }

    public SendMessage findNodeAndAddState(User user, State tryToFind, Long userId, String x) {
        user.setState(tryToFind.getNameState());
        service.putUser(user);
        List<Cell> cells = service.getListCell(userId);
        if (cells.size() == 0) {
            user.setState(State.ONLINE.getNameState());
            service.putUser(user);
            SendMessage sendMessage = new SendMessage
                    (userId.toString(), "Cписок пуст.");
            sendMessage.setReplyMarkup(replyKeyboardMaker.getKeyboard(Arrays.asList
                    (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                            ButtonName.DELETE_NOTE.getNameButton())));
            return sendMessage;
        }
        String cellsToString =
                cells.stream()
                        .map(cell -> cells.indexOf(cell) + " : " + cell.getTitle())
                        .collect(Collectors.joining("\n"));
        return new SendMessage
                (userId.toString(), "Список:\n"+cellsToString + x);
    }

    public SendMessage showInfoForAddingPhoto(Long userId, User user, String userText) {
        Cell cell = service.getCellForContent(userId);
        cell.setText(userText);
        service.putCell(cell);
        return this
                .getrResultRegistrationAndLogIn(user, State.ADD_CONTENT_PHOTO, null, userId,
                        "Если хотите добавить картинку приложите картинку к сообщению или нажмите прололжить.",
                        Collections.singletonList(ButtonName.CONTINUE.getNameButton()));
    }

    public SendMessage showInfoAfterTitle(Long userId, User user, String userText) {
        new User();
        Cell cell = Cell.builder()
                .title(userText)
                .user(User.builder()
                        .id(userId)
                        .build())
                .build();
        service.putCell(cell);
        user.setState(State.ADD_CONTENT_TEXT.getNameState());
        service.putUser(user);
        return new SendMessage(userId.toString(), "Введите текст для вашей заметки.");
    }

    public SendMessage addPhotoFromTelegrammWithoutText(NoteBot noteBot, Update update, Long userId, User user) {
       addPhotoFromTelegramm(noteBot,update, userId);
        Cell cell = service.getCellForContent(userId);
        if (update.getMessage() != null) {
            cell.setText(update.getMessage().getCaption());
            service.putCell(cell);
        }
        return this
                .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                        "Вы успешно внесли заметку.",
                        Arrays.asList
                                (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                                        ButtonName.DELETE_NOTE.getNameButton()));
    }

    public SendMessage showNoteWithPhotoAfterChoose(NoteBot noteBot, Update update, Long userId, User user, String userText) {
        Integer userChoose;
        try {
            userChoose = Integer.valueOf(userText);
            List<Cell> cells = service.getListCell(userId);
            Cell cell = cells.get(userChoose);
            if (cell.getView() != null) {
                sendPhotoToTelegramm(noteBot,update, cell.getView());
            }
            SendMessage sendMessage = new SendMessage
                    (userId.toString(), cell.getTitle() + "\n\nТекст:\n" + cell.getText());
            sendMessage.setReplyMarkup(replyKeyboardMaker.getKeyboard(Arrays.asList
                    (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                            ButtonName.DELETE_NOTE.getNameButton())));
            user.setState(State.ONLINE.getNameState());
            service.putUser(user);
            return sendMessage;
        } catch (RuntimeException r) {
            return new SendMessage
                    (userId.toString(), "Такого номера в списке нет.Введите номер из списка выше.");
        }
    }

    public SendMessage finishAddContentAndOpenMenu(NoteBot noteBot, Update update, Long userId, User user) {
        if (update.getMessage().getPhoto() != null) {
            addPhotoFromTelegramm(noteBot,update, userId);
        }
        return this
                .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                        "Вы успешно внесли заметку.",
                        Arrays.asList
                                (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                                        ButtonName.DELETE_NOTE.getNameButton()));
    }
    private void sendPhotoToTelegramm(NoteBot noteBot,Update update, byte[] img) {
        try {
            noteBot.execute(service.sendPhoto(update.getMessage().getFrom().getId(), img));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка: невозможно отправить файл.");
        }
    }

    private void addPhotoFromTelegramm(NoteBot noteBot,Update update, Long userId) {
        byte[] imageInByte;
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.get(photos.size() - 1);
        String fileId = photo.getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        String filePath;
        try {
            filePath = noteBot.execute(getFile).getFilePath();
            File file = noteBot.downloadFile(filePath, new File("NoteBot/src/main/resources/img.png"));
            BufferedImage originalImage = ImageIO.read(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
        Cell cell = service.getCellForContent(userId);
        cell.setView(imageInByte);
        service.putCell(cell);
    }
}
