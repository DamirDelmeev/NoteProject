package home.project.notebot.handlers;

import home.project.notebot.constants.ButtonName;
import home.project.notebot.constants.State;
import home.project.notebot.entity.Users;
import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

@Component
public class RegistrationMessageHandler {
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;
    @Autowired
    Service service;

    public SendMessage getInfoMessage(Long userId, String text, List<String> LOG_IN) {
        SendMessage sendMessage = new SendMessage(userId.toString(), text);
        sendMessage.setReplyMarkup(replyKeyboardMaker
                .getKeyboard(LOG_IN));
        return sendMessage;
    }

    public SendMessage getLoginAndRegistrationMessage(Users users, State tryInLogin, Long userId, String text) {
        users.setState(tryInLogin.getNameState());
        users.setId(userId);
        service.putUser(users);
        return new SendMessage(userId.toString(), text);
    }

    public SendMessage getRegistrationMessage(Long userId, SendMessage sendMessage, Users users) {
        users.setState(State.TRY_REGISTRATION_LOGIN.getNameState());
        users.setId(userId);
        service.putUser(users);
        return this.getInfoMessage(userId, "Введите логин для регистрации.",
                Collections.singletonList(ButtonName.LOG_IN.getNameButton()));
    }

    public SendMessage getrResultRegistrationAndLogIn(Users users, State online, SendMessage sendMessage, Long userId,
                                                      String text,
                                                      List<String> ADD_NOTE) {
        users.setState(online.getNameState());
        service.putUser(users);
        return this.getInfoMessage(userId, text,
                ADD_NOTE);
    }

    public SendMessage setLoginForInAndShowPassMessage(Long userId, Users users, String userText) {
        SendMessage sendMessage;
        users.setUserInLogin(userText);
        users.setState(State.TRY_IN_PASSWORD.getNameState());
        service.putUser(users);
        sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
        return sendMessage;
    }

    public SendMessage setLoginForRegistrationAndShowPassMessage(Long userId, Users users, String userText) {
        if (userText.equals("Войти")) {
            return getLoginAndRegistrationMessage(users, State.TRY_IN_LOGIN, userId, "Введите логин для входа.");
        } else {
            users.setLogin(userText);
            users.setState(State.TRY_REGISTRATION_PASSWORD.getNameState());
            service.putUser(users);
            SendMessage sendMessage = new SendMessage(userId.toString(), "Введите пароль.");
            return sendMessage;
        }
    }
}
