package home.bot.notebotserver.dao;

import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    User findAllByLogin(String text);

    void save(User user);

    User findById(long id);

    Cell[] getAllCellByUserId(int id);
}