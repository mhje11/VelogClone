package hello.velogclone.domain.Series.service;

import hello.velogclone.domain.Series.dto.SeriesCreateDto;
import hello.velogclone.domain.Series.entity.Series;
import hello.velogclone.domain.Series.repository.SeriesRepository;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final BlogRepository blogRepository;

    public List<Series> findAllSeriesByBlogId(Long blogId) {
        return seriesRepository.findAllByBlogId(blogId);
    }

    public void createSeries(SeriesCreateDto seriesCreateDto, Long blogId) {
        Series series = new Series();
        Blog blog = blogRepository.findById(blogId).get();
        series.setSeriesName(seriesCreateDto.getSeriesName());
        series.setBlog(blog);
        seriesRepository.save(series);
    }

    public void deleteSeries(Long seriesId) {
        Series series = seriesRepository.findById(seriesId).get();
        seriesRepository.delete(series);
    }
}
