package home.bot.notebotserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id")
    Long id;
    @Column(name = "login")
    String login;
    @Column(name = "pass")
    String password;
    @Column(name = "state")
    String state;
    @Column(name = "userinlogin")
    String userInLogin;
}
