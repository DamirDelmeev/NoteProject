package home.project.notebot.bot;

import home.project.notebot.configuration.TelegramConfig;
import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.Users;
import home.project.notebot.handlers.NotesMessageHandler;
import home.project.notebot.handlers.RegistrationMessageHandler;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.Arrays;
import java.util.Collections;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoteBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;
    @Autowired
    Service service;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    RegistrationMessageHandler registrationMessageHandler;

    @Autowired
    NotesMessageHandler notesMessageHandler;

    public NoteBot(SetWebhook setWebhook, TelegramConfig telegramConfig) {
        super(setWebhook);
        this.botPath = telegramConfig.getWebHookPath();
        this.botUsername = telegramConfig.getBotName();
        this.botToken = telegramConfig.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        String chatId = update.getMessage() != null ? update.getMessage().getChatId().toString() :
                update.getEditedMessage().getChatId().toString();
        if (update.getMessage() != null) {
            try {
                return getSendMessage(update);
            } catch (Exception e) {
                return new SendMessage(chatId, "Ошибка " + e.getMessage());
            }
        }
        return new SendMessage(chatId, "Ошибка edit message.");
    }

    private SendMessage getSendMessage(Update update) {
        Long userId = update.getMessage().getChatId();
        Users users = service.getUser(userId);
        String userText = update.getMessage().getText();
        if ((users == null) && userText != null && (!userText.equals("Войти")) & (!userText.equals("Регистрация"))
                || (users != null) && users.getState().equals(State.NOT_REGISTERED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(userId,
                    "Вы хотите войти в аккаунт или зарегистрироваться?",
                    Arrays.asList(ButtonName.LOG_IN.getNameButton(), ButtonName.REGISTRATION.getNameButton()));
        }
        if (userText != null && userText.equals("Войти")) {
            if (users == null) {
                users = new Users(userId, null, null, null, null, null);
            }
            return registrationMessageHandler
                    .getLoginAndRegistrationMessage(users, State.TRY_IN_LOGIN, userId,
                            "Введите логин для входа.");
        }
        if (users != null && users.getState().equals(State.TRY_IN_LOGIN.getNameState())) {
            if (userText != null && userText.equals("Регистрация")) {
                return registrationMessageHandler
                        .getLoginAndRegistrationMessage(users, State.TRY_REGISTRATION_LOGIN, userId,
                                "Введите логин для регистрации.");
            }
            if (service.checkLoginFromDB(userText)) {
                return registrationMessageHandler.setLoginForInAndShowPassMessage(userId, users, userText);
            } else {
                return registrationMessageHandler.getInfoMessage(userId,
                        "Такого логина не существует." +
                                "\nВы можете повторить попытку или зарегистрироваться нажав кнопку регистрация.",
                        Collections.singletonList(ButtonName.REGISTRATION.getNameButton()));
            }
        }
        if (users != null && users.getState().equals(State.TRY_IN_PASSWORD.getNameState())) {
            if (service.checkPasswordFromDB(userText, users)) {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(users, State.ONLINE, null, userId,
                                "Вы успешно вошли в аккаунт.",
                                Arrays.asList
                                        (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                                                ButtonName.DELETE_NOTE.getNameButton()));
            } else {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(users, State.TRY_IN_LOGIN, null, userId,
                                "Вы ввели некорректные данные." +
                                        "Введите повторно логин для входа или нажмите кнопку регистрации.",
                                Collections.singletonList(ButtonName.REGISTRATION.getNameButton()));
            }
        }
        if (userText != null && userText.equals("Регистрация")) {
            if (users == null) {
                users = new Users(userId, null, null, null, null, null);
            }
            return registrationMessageHandler.getRegistrationMessage(userId, null, users);
        }
        if (users != null && users.getState().equals(State.TRY_REGISTRATION_LOGIN.getNameState())) {
            if (userText != null) {
                return registrationMessageHandler
                        .setLoginForRegistrationAndShowPassMessage(userId, users, userText);
            }
        }
        if (users != null && users.getState().equals(State.TRY_REGISTRATION_PASSWORD.getNameState())) {
            users.setPassword(passwordEncoder.encode(userText));
            return registrationMessageHandler
                    .getrResultRegistrationAndLogIn(users, State.REGISTRATION_FINISHED, null, userId,
                            "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                            Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (users != null && users.getState().equals(State.REGISTRATION_FINISHED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(userId,
                    "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                    Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (users != null && users.getState().equals(State.ONLINE.getNameState()) &
                userText != null && !userText.equals("Найти запись") & !userText.equals("Удалить запись")) {
            return notesMessageHandler.showInfoAfterChooseAddButtton(userId, users, userText);
        }
        if (users != null && users.getState().equals(State.ADD_TITLE.getNameState())) {
            return notesMessageHandler.showInfoAfterTitle(userId, users, userText);
        }
        if (users != null && users.getState().equals(State.ADD_CONTENT.getNameState())) {
            if (userText != null) {
                return notesMessageHandler.showInfoForAddingPhoto(userId, users, userText);
            }
            if (update.getMessage().getPhoto() != null) {
                return notesMessageHandler.addPhotoFromTelegrammWithoutText(this, update, userId, users);
            }
        }
        if (users != null && users.getState().equals(State.ADD_CONTENT_PHOTO.getNameState())) {
            return notesMessageHandler.finishAddContentAndOpenMenu(this, update, userId, users);
        }
        if (users != null && users.getState().equals(State.ONLINE.getNameState())) {
            if (userText != null && userText.equals(ButtonName.FIND_NOTE.getNameButton())) {
                return notesMessageHandler.findNodeAndAddState(users, State.TRY_TO_FIND, userId,
                        "\n\nВведите номер записи которую хотели бы посмотреть.");
            }
            if (userText != null && userText.equals(ButtonName.DELETE_NOTE.getNameButton())) {
                return notesMessageHandler.findNodeAndAddState(users, State.TRY_TO_DELETE, userId,
                        "\n\nВведите номер записи которую хотели бы удалить.");
            }
        }

        if (users != null && users.getState().equals(State.TRY_TO_FIND.getNameState())) {
            return notesMessageHandler.showNoteWithPhotoAfterChoose(this, update, userId, users, userText);
        }
        if (users != null && users.getState().equals(State.TRY_TO_DELETE.getNameState())) {
            return notesMessageHandler.deleteNoteAfterChoose(userId, users, userText);
        }
        return null;
    }
}