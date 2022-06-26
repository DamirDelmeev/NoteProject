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
        Cell cell = service.getCell(id);
        return cell;
    }

    @GetMapping("user/{id}")
    public User getUser(@PathVariable long id) {
        User user = service.getUser(id);
        return user;
    }

    @PostMapping("cell")
    @ResponseStatus(HttpStatus.OK)
    public void addCell(@RequestBody Cell cell) {
        service.addCell(cell);
    }

    @PostMapping("user")
    @ResponseStatus(HttpStatus.OK)
    public void showEmployee(@RequestBody User user) {//@PathVariable получение id из адреса запроса
        service.addUser(user);
    }

    @GetMapping("login/{text}")
    public User getUser(@PathVariable String text) {
        User user = service.getUser(text);
        return user;
    }

    @GetMapping("password/{text}")
    public User getUserByPass(@PathVariable String text) {
        User user = service.getUserByPass(text);
        return user;
    }
}
