package hello.velogclone.domain.post.dto;

import hello.velogclone.domain.Series.entity.Series;
import hello.velogclone.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private Long id;
    private String title;
    private String content;
    private Long blogId;
    private User user;
    private List<String> tags;
    private Series series;
    private boolean temporal;
}
