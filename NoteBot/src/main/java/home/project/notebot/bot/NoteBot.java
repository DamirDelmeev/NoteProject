package home.project.notebot.bot;

import home.project.notebot.configuration.TelegramConfig;
import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.Cell;
import home.project.notebot.entity.User;
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
        Long userId = update.getMessage().getChatId();
        User user = service.getUser(userId);
        String userText = update.getMessage().getText();
        if ((user == null) & (!userText.equals("Войти")) & (!userText.equals("Регистрация"))
                || (user != null) && user.getState().equals(State.NOT_REGISTERED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(null, userId,
                    "Вы хотите войти в аккаунт или зарегистрироваться?",
                    Arrays.asList(ButtonName.LOG_IN.getNameButton(), ButtonName.REGISTRATION.getNameButton()));
        }
        if (userText != null && userText.equals("Войти")) {
            if (user == null) {
                user = new User(userId, null, null, null, null, null);
            }
            return registrationMessageHandler
                    .getLoginAndRegistrationMessage(user, State.TRY_IN_LOGIN, userId, null,
                            "Введите логин для входа.");
        }

        if (user != null && user.getState().equals(State.TRY_IN_LOGIN.getNameState())) {
            if (userText.equals("Регистрация")) {
                return registrationMessageHandler
                        .getLoginAndRegistrationMessage(user, State.TRY_REGISTRATION_LOGIN, userId, null,
                                "Введите логин для регистрации.");
            }
            if (service.checkLoginFromDB(userText)) {
                return registrationMessageHandler.setLoginForInAndShowPassMessage(userId, user, userText);
            } else {
                return registrationMessageHandler.getInfoMessage(null, userId,
                        "Такого логина не существует." +
                                "\nВы можете повторить попытку или зарегистрироваться нажав кнопку регистрация.",
                        Collections.singletonList(ButtonName.REGISTRATION.getNameButton()));
            }
        }

        if (user != null && user.getState().equals(State.TRY_IN_PASSWORD.getNameState())) {
            if (service.checkPasswordFromDB(userText, user)) {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                                "Вы успешно вошли в аккаунт.",
                                Arrays.asList
                                        (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton()));
            } else {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(user, State.TRY_IN_LOGIN, null, userId,
                                "Вы ввели некорректные данные.Введите логин или нажмите кнопку регистрации.",
                                Collections.singletonList(ButtonName.REGISTRATION.getNameButton()));
            }
        }

        if (userText != null && userText.equals("Регистрация")) {
            if (user == null) {
                user = new User(userId, null, null, null, null, null);
            }
            return registrationMessageHandler.getRegistrationMessage(userId, null, user);
        }

        if (user.getState().equals(State.TRY_REGISTRATION_LOGIN.getNameState())) {
            return registrationMessageHandler
                    .setLoginForRegistrationAndShowPassMessage(userId, null, user, userText);
        }

        if (user.getState().equals(State.TRY_REGISTRATION_PASSWORD.getNameState())) {

            user.setPassword(passwordEncoder.encode(userText));
            return registrationMessageHandler
                    .getrResultRegistrationAndLogIn(user, State.REGISTRATION_FINISHED, null, userId,
                            "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                            Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (user.getState().equals(State.REGISTRATION_FINISHED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(null, userId,
                    "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                    Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (user.getState().equals(State.ONLINE.getNameState())) {
            if (userText.equals(ButtonName.ADD_NOTE.getNameButton())) {
                user.setState(State.ADD_TITLE.getNameState());
                service.putUser(user);
                return new SendMessage(userId.toString(), "Введите тему вашей заметки.");
            } else {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                                "Вы успешно вошли в аккаунт.",
                                Arrays.asList
                                        (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton()));
            }
        }
        if (user.getState().equals(State.ADD_TITLE.getNameState())) {

            Cell cell = Cell.builder()
                    .title(userText)
                    .user(new User().builder()
                            .id(userId)
                            .build())
                    .build();
            service.putCell(cell);
            user.setState(State.ADD_CONTENT.getNameState());
            service.putUser(user);
            return new SendMessage(userId.toString(), "Введите текст или приложите картинку для вашей заметки.");

        }
        if (user.getState().equals(State.ADD_CONTENT.getNameState())) {
            if (userText != null) {
                Cell cell = service.getCellForContent(userId);
                cell.setText(userText);
                service.putCell(cell);
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(user, State.ONLINE, null, userId,
                                "Вы успешно внесли заметку.",
                                Arrays.asList
                                        (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton()));
            } else {
        //realize SendPhoto

            }
        }

        return null;
    }
}
