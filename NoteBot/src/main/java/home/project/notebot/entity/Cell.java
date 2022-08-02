package home.project.notebot.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private Integer id;
    private String title;
    private String text;
    private byte[] view;
    private Users users;
}
