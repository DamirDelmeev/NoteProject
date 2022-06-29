package home.project.notebot.handlers;

import home.project.notebot.keyboard.ReplyKeyboardMaker;
import home.project.notebot.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotesMessageHandler {
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;
    @Autowired
    Service service;

}
