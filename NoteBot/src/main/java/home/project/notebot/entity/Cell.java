package home.project.notebot.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private long id;
    private String title;
    private String text;
    private byte[] view;
    private User user;
}
