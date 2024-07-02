package hello.velogclone.domain.postimage.entity;

import hello.velogclone.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "post_image")
@NoArgsConstructor
@Getter
@Setter
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String type;

    private String url;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostImage(String type, String url, Post post) {
        this.uuid = UUID.randomUUID().toString();
        this.type = type;
        this.url = url;
        this.post = post;
    }
}
