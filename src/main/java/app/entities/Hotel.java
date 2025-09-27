package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@Entity
public class Hotel {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "hotel",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)   // Room er owner
    @ToString.Exclude
    private List<Room> rooms;

}
