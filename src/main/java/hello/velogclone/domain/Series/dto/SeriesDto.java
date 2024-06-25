package hello.velogclone.domain.Series.dto;

import hello.velogclone.domain.Series.entity.Series;
import lombok.*;

@NoArgsConstructor
@Getter@Setter

public class SeriesDto {
    private Long id;
    private String seriesName;

    public static SeriesDto seriesToDto(Series series) {
        SeriesDto seriesDto = new SeriesDto();
        seriesDto.setId(series.getId());
        seriesDto.setSeriesName(series.getSeriesName());
        return seriesDto;
    }
}
