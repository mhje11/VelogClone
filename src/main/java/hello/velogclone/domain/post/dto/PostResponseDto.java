package hello.velogclone.domain.post.dto;

import hello.velogclone.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long blogId;
    private Long likes;
    List<String> tags;
    private String series;
    private boolean temporal;


    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.tags = post.getTags().stream().map(tag -> "#" + tag.getName()).collect(Collectors.toList());
        this.series = post.getSeries() != null ? post.getSeries().getSeriesName() : "";
        this.temporal = post.isTemporal();
    }
}
