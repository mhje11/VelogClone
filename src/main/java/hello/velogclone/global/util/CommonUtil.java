package hello.velogclone.global.util;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommonUtil {
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    public CommonUtil() {
        List extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create()
        );
        parser = Parser.builder().extensions(extensions).build();
        htmlRenderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String markdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return htmlRenderer.render(document);
    }

    public String htmlToMarkdown(String html) {
        return FlexmarkHtmlConverter.builder().build().convert(html);
    }
}
