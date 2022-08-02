package home.bot.notebotserver.service;


import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.Users;

public interface Service {


    Cell getCell(Integer id);

    Users getUser(Long id);

    void addUser(Users users);

    void addCell(Cell cell);

    Cell getCellForContent(Long userId);

    Users getUser(String text);

    Cell[] getAllCellByUserId(Long id);

    void deleteCell(Integer cellId);
}
