package hello.velogclone.global.util;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommonUtil {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonUtil() {
        List<org.commonmark.Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create()
        );
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String markdown(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}