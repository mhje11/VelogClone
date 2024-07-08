package hello.velogclone.domain.Series.controller;

import hello.velogclone.domain.Series.dto.SeriesDto;
import hello.velogclone.domain.Series.dto.SeriesThumbnailDto;
import hello.velogclone.domain.Series.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class SeriesRestController {
    private final SeriesService seriesService;


    @PostMapping("api/blogs/{blogId}/edit/series/create")
    public ResponseEntity<String> createSeries(@PathVariable("blogId") Long blogId, @RequestBody SeriesDto seriesDto) {
        seriesService.createSeries(seriesDto, blogId);
        return ResponseEntity.ok("시리즈 생성완료");
    }

    @GetMapping("api/blogs/{blogId}/edit/series")
    public ResponseEntity<List<SeriesDto>> findAllSeriesByBlogId(@PathVariable("blogId") Long blogId) {
        List<SeriesDto> series = seriesService.findAllSeriesByBlogId(blogId);
        return ResponseEntity.ok(series);
    }

    @DeleteMapping("api/blogs/{blogId}/edit/series/delete/{seriesId}")
    public ResponseEntity<String> deleteSeries(@PathVariable("seriesId") Long seriesId) {
        seriesService.deleteSeries(seriesId);
        return ResponseEntity.ok("시리즈 삭제");
    }

    @GetMapping("api/blogs/{blogId}/series/list")
    public ResponseEntity<Page<SeriesThumbnailDto>> viewSeriesTab(
            @PathVariable("blogId") Long blogId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SeriesThumbnailDto> series = seriesService.findAllSeriesByBlogIdIncludeImage(blogId, pageable);
        return ResponseEntity.ok(series);
    }


}
