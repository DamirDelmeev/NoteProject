package home.project.notebot.constants;

import lombok.Getter;

@Getter
public enum ButtonName {
    LOG_IN("Войти"),
    REGISTRATION("Регистрация"),
    ADD_NOTE("Добавить запись"),
    FIND_NOTE("Найти запись");

    String nameButton;

    ButtonName(String nameButton) {
        this.nameButton = nameButton;
    }
}
