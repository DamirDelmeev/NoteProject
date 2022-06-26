package home.project.notebot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private int id;
    private String title;
    private String text;
    private byte[] view;
    private String status;
}
