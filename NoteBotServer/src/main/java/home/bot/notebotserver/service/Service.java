package home.bot.notebotserver.service;


import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;

public interface Service {


     Cell getCell(int id);

     User getUser(long id);

     void addUser(User user);

     void addCell(Cell cell);

     Cell getCellForContent(Long userId);

    User getUser(String text);

    Cell[] getAllCellByUserId(int id);

    void deleteCell(long cellId);
}
