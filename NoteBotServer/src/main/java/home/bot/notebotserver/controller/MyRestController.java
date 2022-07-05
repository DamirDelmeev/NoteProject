package home.bot.notebotserver.controller;


import home.bot.notebotserver.entity.Cell;
import home.bot.notebotserver.entity.User;
import home.bot.notebotserver.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MyRestController {
    @Autowired
    private Service service;


    @GetMapping("cell/{id}")
    public Cell getCell(@PathVariable int id) {
        return service.getCell(id);
    }

    @GetMapping("cell/{id}/all")
    public Cell[] getAllCellByUserId(@PathVariable int id) {
        return service.getAllCellByUserId(id);
    }

    @GetMapping("user/{id}")
    public User getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @PostMapping("cell")
    @ResponseStatus(HttpStatus.OK)
    public void addCell(@RequestBody Cell cell) {
        service.addCell(cell);
    }

    @PostMapping("user")
    @ResponseStatus(HttpStatus.OK)
    public void addUser(@RequestBody User user) {
        service.addUser(user);
    }

    @GetMapping("login/{text}")
    public User getUser(@PathVariable String text) {
        return service.getUser(text);
    }

    @GetMapping("cell/content/{id}")
    public Cell getCellForContent(@PathVariable long id) {
        return service.getCellForContent(id);
    }

    @DeleteMapping("cell/{id}")
    public void deleteCellById(@PathVariable long id) {
        service.deleteCell(id);
    }
}
