package home.project.notebot.bot;

import home.project.notebot.configuration.TelegramConfig;
import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.User;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.Arrays;

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

    public NoteBot(SetWebhook setWebhook, TelegramConfig telegramConfig) {
        super(setWebhook);
        this.botPath = telegramConfig.getWebHookPath();
        this.botUsername = telegramConfig.getBotName();
        this.botToken = telegramConfig.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Long userId = update.getMessage().getChatId();
        SendMessage sendMessage = null;
        User user = service.getUser(userId);
        String userText = update.getMessage().getText();
        if ((user == null) & (!userText.equals("Войти")) & (!userText.equals("Регистрация")) || (user != null) && user.getState().equals(State.NOT_REGISTERED.getNameState())) {
            sendMessage = new SendMessage(userId.toString(), "Вы хотите войти в аккаунт или зарегистрироваться?");
            sendMessage.setReplyMarkup(replyKeyboardMaker
                    .getKeyboard(Arrays.asList(ButtonName.LOG_IN.getNameButton(),
                            ButtonName.REGISTRATION.getNameButton())));
            return sendMessage;
        }
        if (userText.equals("Войти")) {
            user.setState(State.TRY_IN_LOGIN.getNameState());
            user.setId(userId);
            service.putUser(user);
            sendMessage = new SendMessage(userId.toString(), "Введите логин для входа.");
            return sendMessage;
        }

        if (user != null && user.getState().equals(State.TRY_IN_LOGIN.getNameState())) {
            if (userText.equals("Регистрация")) {
                user.setState(State.TRY_REGISTRATION_LOGIN.getNameState());
                user.setId(userId);
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Введите логин для регистрации.");
                return sendMessage;
            }
            if (service.checkLoginFromDB(userText)) {
                user.setUserInLogin(userText);
                user.setState(State.TRY_IN_PASSWORD.getNameState());
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
                return sendMessage;
            } else {
                sendMessage = new SendMessage(userId.toString(), "Такого логина не существует." +
                        "\nВы можете повторить попытку или зарегистрироваться нажав кнопку регистрация.");
                sendMessage.setReplyMarkup(replyKeyboardMaker
                        .getKeyboard(Arrays.asList(ButtonName.REGISTRATION.getNameButton())));
                return sendMessage;
            }
        }

        if (user != null && user.getState().equals(State.TRY_IN_PASSWORD.getNameState())) {
            if (service.checkPasswordFromDB(userText, user)) {
                //*
                user.setState(State.ONLINE.getNameState());
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Вы успешно вошли в аккаунт.");
                sendMessage.setReplyMarkup(replyKeyboardMaker
                        .getKeyboard(Arrays.asList(ButtonName.ADD_NOTE.getNameButton(),
                                ButtonName.FIND_NOTE.getNameButton())));
                return sendMessage;
            } else {
                user.setState(State.TRY_IN_LOGIN.getNameState());
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Вы ввели некорректные данные.Введите логин или нажмите " +
                        "кнопку регистрации.");
                sendMessage.setReplyMarkup(replyKeyboardMaker
                        .getKeyboard(Arrays.asList(ButtonName.REGISTRATION.getNameButton())));
                return sendMessage;
            }
        }

        if (userText.equals("Регистрация")) {
            user.setState(State.TRY_REGISTRATION_LOGIN.getNameState());
            user.setId(userId);
            service.putUser(user);
            sendMessage = new SendMessage(userId.toString(), "Введите логин для регистрации.");
            sendMessage.setReplyMarkup(replyKeyboardMaker
                    .getKeyboard(Arrays.asList(ButtonName.LOG_IN.getNameButton())));
            return sendMessage;
        }

        if (user != null && user.getState().equals(State.TRY_REGISTRATION_LOGIN.getNameState())) {
            if (userText.equals("Войти")) {
                user.setState(State.TRY_IN_LOGIN.getNameState());
                user.setId(userId);
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Введите логин для входа.");
                return sendMessage;
            } else {
                user.setLogin(userText);
                user.setState(State.TRY_REGISTRATION_PASSWORD.getNameState());
                service.putUser(user);
                sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
                return sendMessage;
            }
        }

        if (user != null && user.getState().equals(State.TRY_REGISTRATION_PASSWORD.getNameState())) {
            user.setPassword(userText);
            user.setState(State.REGISTRATION_FINISHED.getNameState());
            service.putUser(user);
            sendMessage = new SendMessage(userId.toString(), "Вы успешно прошли регистрацию.Нажмите кнопку войти.");
            sendMessage.setReplyMarkup(replyKeyboardMaker
                    .getKeyboard(Arrays.asList(ButtonName.LOG_IN.getNameButton())));
            return sendMessage;
        }
        if (user != null && user.getState().equals(State.REGISTRATION_FINISHED.getNameState())) {
            sendMessage = new SendMessage(userId.toString(), "Вы успешно прошли регистрацию.Нажмите кнопку войти.");
            sendMessage.setReplyMarkup(replyKeyboardMaker
                    .getKeyboard(Arrays.asList(ButtonName.LOG_IN.getNameButton())));
            return sendMessage;
        }
        return sendMessage;
    }
}
