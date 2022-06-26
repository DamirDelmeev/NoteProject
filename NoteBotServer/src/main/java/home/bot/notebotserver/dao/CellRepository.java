package home.bot.notebotserver.dao;


import home.bot.notebotserver.entity.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CellRepository extends JpaRepository<Cell, Integer> {
}