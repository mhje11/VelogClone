package hello.velogclone.domain.post.dto;

import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    private List<String> tags;
    private String series;
    private boolean temporal;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private Long commentsCount;
    private String loginId;
    private String profileImageUrl;
    private Long seriesId;


    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.blogId = post.getBlog().getId();
        this.tags = Arrays.asList(post.getTags().stream().map(tag -> "#" + tag.getName()).collect(Collectors.joining(" ")).split(" "));
        this.series = post.getSeries() != null ? post.getSeries().getSeriesName() : "";
        this.temporal = post.isTemporal();
        this.thumbnailUrl = post.getPostImages().isEmpty() ? "/images/posts/default_thumbnail.png" : post.getPostImages().getFirst().getUrl();
        this.createdAt = post.getCreatedAt();
        this.commentsCount = post.getComments() == null ? 0L : (long) post.getComments().size();
        this.likes = post.getLikes() == null ? 0L : (long) post.getLikes().size();
        this.loginId = post.getUser().getLoginId();
        this.profileImageUrl =  (post.getUser().getProfileImage() != null && post.getUser().getProfileImage().getUrl() != null) ? post.getUser().getProfileImage().getUrl() : "images/profiles/default-profile.png";
        this.seriesId = post.getSeries() != null ? post.getSeries().getId() : null;

    }

    public String getTagsAsString() {
        return String.join(" ", this.tags);
    }
}
