package hello.velogclone.domain.Series.controller;

import hello.velogclone.domain.Series.service.SeriesService;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SeriesController {
    private final SeriesService seriesService;
    private final PostService postService;

    @GetMapping("/api/blogs/{blogId}/series-list")
    public String toSeriesList(Model model, @PathVariable("blogId") Long blogId) {
        model.addAttribute("blogId", blogId);
        return "series/seriesList";
    }

    @GetMapping("/api/blogs/{blogId}/series/{seriesId}")
    public String toSeries(@PathVariable("seriesId") Long seriesId, RedirectAttributes redirectAttributes, Pageable pageable, @PathVariable("blogId") Long blogId
    , Model model) {
        Page<PostResponseDto> posts = postService.findAllBySeriesId(seriesId, pageable);

        if (posts.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "해당 시리즈에는 포스트가 없습니다.");
            return "redirect:/api/blogs/{blogId}/series-list";
        }
        model.addAttribute("blogId", blogId);
        model.addAttribute("seriesId", seriesId);

        return "series/seriesPosts";
    }

}
