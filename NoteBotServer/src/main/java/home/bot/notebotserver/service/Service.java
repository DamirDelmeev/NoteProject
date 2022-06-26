package home.bot.notebotserver.service;


import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;

public interface Service {


    public Cell getCell(int id);

    public User getUser(long id);

    public void addUser(User user);

    public void addCell(Cell cell);

    User getUser(String text);

    User getUserByPass(String text);
}
