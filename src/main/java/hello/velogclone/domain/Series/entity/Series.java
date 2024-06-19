package hello.velogclone.domain.Series.entity;

import hello.velogclone.domain.post.entity.Post;
import jakarta.persistence.*;

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_name")
    private String seriesName;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
