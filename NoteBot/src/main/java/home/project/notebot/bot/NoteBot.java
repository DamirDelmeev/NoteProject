package home.project.notebot.bot;

import home.project.notebot.configuration.TelegramConfig;
import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
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
        String chatId = update.getMessage() != null ? update.getMessage().getChatId().toString() :
                update.getEditedMessage().getChatId().toString();
        if (update.getMessage() != null) {
            try {
                return getSendMessage(update);
            } catch (Exception e) {
                return new SendMessage(chatId, "Ошибка");
            }
        }
        return new SendMessage(chatId, "Ошибка edit message.");
    }

    private SendMessage getSendMessage(Update update) {
        Long userId = update.getMessage().getChatId();
        User user = service.getUser(userId);
        String userText = update.getMessage().getText();
        if ((user == null) && userText != null && (!userText.equals("Войти")) & (!userText.equals("Регистрация"))
                || (user != null) && user.getState().equals(State.NOT_REGISTERED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(userId,
                    "Вы хотите войти в аккаунт или зарегистрироваться?",
                    Arrays.asList(ButtonName.LOG_IN.getNameButton(), ButtonName.REGISTRATION.getNameButton()));
        }
        if (userText != null && userText.equals("Войти")) {
            if (user == null) {
                user = new User(userId, null, null, null, null, null);
            }
            return registrationMessageHandler
                    .getLoginAndRegistrationMessage(user, State.TRY_IN_LOGIN, userId,
                            "Введите логин для входа.");
        }
        if (user != null && user.getState().equals(State.TRY_IN_LOGIN.getNameState())) {
            if (userText != null && userText.equals("Регистрация")) {
                return registrationMessageHandler
                        .getLoginAndRegistrationMessage(user, State.TRY_REGISTRATION_LOGIN, userId,
                                "Введите логин для регистрации.");
            }
            if (service.checkLoginFromDB(userText)) {
                return registrationMessageHandler.setLoginForInAndShowPassMessage(userId, user, userText);
            } else {
                return registrationMessageHandler.getInfoMessage(userId,
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
                                        (ButtonName.ADD_NOTE.getNameButton(), ButtonName.FIND_NOTE.getNameButton(),
                                                ButtonName.DELETE_NOTE.getNameButton()));
            } else {
                return registrationMessageHandler
                        .getrResultRegistrationAndLogIn(user, State.TRY_IN_LOGIN, null, userId,
                                "Вы ввели некорректные данные." +
                                        "Введите повторно логин для входа или нажмите кнопку регистрации.",
                                Collections.singletonList(ButtonName.REGISTRATION.getNameButton()));
            }
        }
        if (userText != null && userText.equals("Регистрация")) {
            if (user == null) {
                user = new User(userId, null, null, null, null, null);
            }
            return registrationMessageHandler.getRegistrationMessage(userId, null, user);
        }
        if (user != null && user.getState().equals(State.TRY_REGISTRATION_LOGIN.getNameState())) {
            if (userText != null) {
                return registrationMessageHandler
                        .setLoginForRegistrationAndShowPassMessage(userId, user, userText);
            }
        }
        if (user != null && user.getState().equals(State.TRY_REGISTRATION_PASSWORD.getNameState())) {
            user.setPassword(passwordEncoder.encode(userText));
            return registrationMessageHandler
                    .getrResultRegistrationAndLogIn(user, State.REGISTRATION_FINISHED, null, userId,
                            "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                            Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (user != null && user.getState().equals(State.REGISTRATION_FINISHED.getNameState())) {
            return registrationMessageHandler.getInfoMessage(userId,
                    "Вы успешно прошли регистрацию.Нажмите кнопку войти.",
                    Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
        }
        if (user != null && user.getState().equals(State.ONLINE.getNameState()) &
                userText != null && !userText.equals("Найти запись") & !userText.equals("Удалить запись")) {
            return notesMessageHandler.showInfoAfterChooseAddButtton(userId, user, userText);
        }
        if (user != null && user.getState().equals(State.ADD_TITLE.getNameState())) {
            return notesMessageHandler.showInfoAfterTitle(userId, user, userText);
        }
        if (user != null && user.getState().equals(State.ADD_CONTENT_TEXT.getNameState())) {
            if (userText != null) {
                return notesMessageHandler.showInfoForAddingPhoto(userId, user, userText);
            }
            if (update.getMessage().getPhoto() != null) {
                return notesMessageHandler.addPhotoFromTelegrammWithoutText(this, update, userId, user);
            }
        }
        if (user != null && user.getState().equals(State.ADD_CONTENT_PHOTO.getNameState())) {
            return notesMessageHandler.finishAddContentAndOpenMenu(this, update, userId, user);
        }
        if (user != null && user.getState().equals(State.ONLINE.getNameState())) {
            if (userText != null && userText.equals(ButtonName.FIND_NOTE.getNameButton())) {
                return notesMessageHandler.findNodeAndAddState(user, State.TRY_TO_FIND, userId,
                        "\n\nВведите номер записи которую хотели бы посмотреть.");
            }
            if (userText != null && userText.equals(ButtonName.DELETE_NOTE.getNameButton())) {
                return notesMessageHandler.findNodeAndAddState(user, State.TRY_TO_DELETE, userId,
                        "\n\nВведите номер записи которую хотели бы удалить.");
            }
        }

        if (user != null && user.getState().equals(State.TRY_TO_FIND.getNameState())) {
            return notesMessageHandler.showNoteWithPhotoAfterChoose(this, update, userId, user, userText);
        }
        if (user != null && user.getState().equals(State.TRY_TO_DELETE.getNameState())) {
            return notesMessageHandler.deleteNoteAfterChoose(userId, user, userText);
        }
        return null;
    }
}