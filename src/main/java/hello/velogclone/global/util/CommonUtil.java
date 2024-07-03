package hello.velogclone.global.util;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    public CommonUtil() {
        parser = Parser.builder().build();
        htmlRenderer = HtmlRenderer.builder().build();
    }

    public String markdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return htmlRenderer.render(document);
    }

    public String htmlToMarkdown(String html) {
        return FlexmarkHtmlConverter.builder().build().convert(html);
    }
}
