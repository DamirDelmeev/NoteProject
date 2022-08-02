package home.bot.notebotserver.dao;

import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.Users;
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
    public Users findAllByLogin(String text) {
        Query query = entityManager.createQuery("from Users where login=:paramName");
        query.setParameter("paramName", text);
        List<Users> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @Override
    @Transactional
    public void save(Users users) {
        Users merge = entityManager.merge(users);
        users.setId(merge.getId());
    }

    @Override
    public Users findById(Long id) {
        return entityManager.find(Users.class, id);
    }

    @Override
    public Cell[] getAllCellByUserId(Long id) {
        Query query = entityManager.createQuery("from Cell where users=:userId");
        Users users = new Users();
        users.setId(id);
        query.setParameter("userId", users);
        List<Cell> listCell = query.getResultList();
        return listCell.size() == 0 ? new Cell[0] : listCell.toArray(new Cell[0]);
    }
}
