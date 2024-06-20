package hello.velogclone.domain.Series.entity;

import hello.velogclone.domain.post.entity.Post;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_name")
    private String seriesName;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
