package home.project.notebot.constants;

import lombok.Getter;

@Getter
public enum State {
    NOT_REGISTERED("NOT_REGISTERED"),
    REGISTERED("REGISTERED"),
    ADDED("ADDED"),
    REMOVABLE("REMOVABLE"),
    SEARCHING("SEARCHING"),
    TRY_IN_LOGIN("TRY_IN_LOGIN"),
    TRY_IN_PASSWORD("TRY_IN_PASSWORD"),
    TRY_REGISTRATION_LOGIN(" TRY_REGISTRATION_LOGIN"),
    TRY_REGISTRATION_PASSWORD("TRY_REGISTRATION_PASSWORD"),
    REGISTRATION_FINISHED("REGISTRATION_FINISHED"),
    ONLINE("ONLINE");


    String nameState;

    State(String nameState) {
        this.nameState = nameState;
    }
}
