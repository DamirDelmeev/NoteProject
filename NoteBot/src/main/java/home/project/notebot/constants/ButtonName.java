package home.project.notebot.constants;

import lombok.Getter;

@Getter
public enum ButtonName {
    LOG_IN("Войти"),
    REGISTRATION("Регистрация"),
    CONTINUE("Продолжить"),
    ADD_NOTE("Добавить запись"),
    FIND_NOTE("Найти запись"),
    DELETE_NOTE("Удалить запись");

    final String nameButton;

    ButtonName(String nameButton) {
        this.nameButton = nameButton;
    }
}
