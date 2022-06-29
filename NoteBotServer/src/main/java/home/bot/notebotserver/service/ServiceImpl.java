package home.bot.notebotserver.service;


import home.bot.notebotserver.dao.CellRepository;
import home.bot.notebotserver.dao.UserRepository;
import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    @Autowired
    CellRepository cellRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Cell getCell(int id) {
        Cell cell = null;
        Optional<Cell> cellOptional = cellRepository.findById(id);
        if (cellOptional.isPresent()) {
            cell = cellOptional.get();
        }
        return cell;
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void addCell(Cell cell) {
        cellRepository.save(cell);
    }

    @Override
    public Cell getCellForContent(Long userId) {
        List<Cell> cellList = getUser(userId).getCellList();
        return cellList.get(cellList.size() - 1);
    }

    @Override
    public User getUser(String text) {
        return userRepository.findAllByLogin(text);
    }

    @Override
    public User getUserByPass(String text) {
        return userRepository.findAllByPassword(text);
    }
}
