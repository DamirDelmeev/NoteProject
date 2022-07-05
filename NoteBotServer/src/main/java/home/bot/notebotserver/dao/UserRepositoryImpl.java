package home.bot.notebotserver.dao;

import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository


public class UserRepositoryImpl implements UserRepository {
    @Autowired
    EntityManager entityManager;

    @Override
    public User findAllByLogin(String text) {
        Query query = entityManager.createQuery("from User where login=:paramName");
        query.setParameter("paramName", text);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        User merge = entityManager.merge(user);
        user.setId(merge.getId());
    }

    @Override
    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public Cell[] getAllCellByUserId(int id) {
        Query query = entityManager.createQuery("from Cell where user=:userId");
        User user = new User();
        user.setId(id);
        query.setParameter("userId", user);
        List<Cell> listCell = query.getResultList();
        return listCell.size() == 0 ? new Cell[0] : listCell.toArray(new Cell[0]);
    }
}
