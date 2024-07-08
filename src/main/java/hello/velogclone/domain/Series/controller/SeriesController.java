package hello.velogclone.domain.Series.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SeriesController {

    @GetMapping("/api/blogs/{blogId}/series-list")
    public String toSeries(Model model, @PathVariable("blogId") Long blogId) {
        model.addAttribute("blogId", blogId);
        return "series/seriesList";
    }

}
