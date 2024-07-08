package hello.velogclone.domain.Series.dto;

import hello.velogclone.domain.Series.entity.Series;
import hello.velogclone.domain.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SeriesThumbnailDto {
    private Long id;
    private String seriesName;
    private String thumbnailUrl;

    public SeriesThumbnailDto(Long id, String seriesName, List<Post> posts) {
        this.id = id;
        this.seriesName = seriesName;
        this.thumbnailUrl = posts.getFirst().getPostImages().getFirst().getUrl();
    }

    public static SeriesThumbnailDto seriesToThumbnailDto(Series series) {
        SeriesThumbnailDto seriesDto = new SeriesThumbnailDto();
        seriesDto.setId(series.getId());
        seriesDto.setSeriesName(series.getSeriesName());
        seriesDto.setThumbnailUrl(seriesDto.getThumbnailUrl(series.getPosts()));
        return seriesDto;
    }

    private String getThumbnailUrl(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return "/images/posts/default_thumbnail.png";
        }
        Post firstPost = posts.get(0); // 리스트의 첫 번째 요소를 가져옵니다.
        if (firstPost.getPostImages() == null || firstPost.getPostImages().isEmpty()) {
            return "/images/posts/default_thumbnail.png";
        }
        return firstPost.getPostImages().get(0).getUrl(); // 이미지 리스트의 첫 번째 요소를 가져옵니다.
    }
}
