package home.bot.notebotserver.dao;

import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    Users findAllByLogin(String text);

    void save(Users users);

    Users findById(Long id);

    Cell[] getAllCellByUserId(Long id);
}