package home.bot.notebotserver.service;


import home.bot.notebotserver.dao.CellRepository;
import home.bot.notebotserver.dao.UserRepository;
import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.Users;
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
    public Cell getCell(Integer id) {
        Cell cell = null;
        Optional<Cell> cellOptional = cellRepository.findById(Math.toIntExact(id));
        if (cellOptional.isPresent()) {
            cell = cellOptional.get();
        }
        return cell;
    }

    @Override
    public Users getUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void addUser(Users users) {
        userRepository.save(users);
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
    public Users getUser(String text) {
        return userRepository.findAllByLogin(text);
    }

    @Override
    public Cell[] getAllCellByUserId(Long id) {
        return userRepository.getAllCellByUserId(id);
    }

    @Override
    public void deleteCell(Integer cellId) {
        Cell cell = getCell(cellId);
        cellRepository.delete(cell);
    }
}
