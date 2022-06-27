package home.project.notebot.handlers;

import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.User;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;

@Component
public class RegistrationMessageHandler {
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;
    @Autowired
    Service service;

    public SendMessage getInfoMessage(SendMessage sendMessage, Long userId, String text, List<String> LOG_IN) {
        sendMessage = new SendMessage(userId.toString(), text);
        sendMessage.setReplyMarkup(replyKeyboardMaker
                .getKeyboard(LOG_IN));
        return sendMessage;
    }

    public SendMessage getLoginAndRegistrationMessage(User user, State tryInLogin, Long userId, SendMessage sendMessage, String text) {
        user.setState(tryInLogin.getNameState());
        user.setId(userId);
        service.putUser(user);
        return new SendMessage(userId.toString(), text);
    }


    public SendMessage getRegistrationMessage(Long userId, SendMessage sendMessage, User user) {
        user.setState(State.TRY_REGISTRATION_LOGIN.getNameState());
        user.setId(userId);
        service.putUser(user);
        return this.getInfoMessage(sendMessage, userId, "Введите логин для регистрации.",
                Arrays.asList(ButtonName.LOG_IN.getNameButton()));
    }

    public SendMessage getrResultRegistrationAndLogIn(User user, State online, SendMessage sendMessage, Long userId,
                                                      String text,
                                                      List<String> ADD_NOTE) {
        user.setState(online.getNameState());
        service.putUser(user);
        return this.getInfoMessage(sendMessage, userId, text,
                ADD_NOTE);
    }

    public SendMessage setLoginForInAndShowPassMessage(Long userId, User user, String userText) {
        SendMessage sendMessage;
        user.setUserInLogin(userText);
        user.setState(State.TRY_IN_PASSWORD.getNameState());
        service.putUser(user);
        sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
        return sendMessage;

    }

    public SendMessage setLoginForRegistrationAndShowPassMessage(Long userId, SendMessage sendMessage, User user, String userText) {
        if (userText.equals("Войти")) {
            return getLoginAndRegistrationMessage(user, State.TRY_IN_LOGIN, userId, sendMessage, "Введите логин для входа.");
        } else {
            user.setLogin(userText);
            user.setState(State.TRY_REGISTRATION_PASSWORD.getNameState());
            service.putUser(user);
            sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
            return sendMessage;
        }
    }
}
