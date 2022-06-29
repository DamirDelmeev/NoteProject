package home.bot.notebotserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id

    @Column(name = "id")
    private long id;
    @Column(name = "login")
    private String login;
    @Column(name = "pass")
    private String password;
    @Column(name = "state")
    private String state;
    @Column(name = "userinlogin")
    private String userInLogin;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Cell> cellList;
}
