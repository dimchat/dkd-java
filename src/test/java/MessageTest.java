import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.TextContent;

import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;

public class MessageTest extends TestCase {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    @Test
    public void testTextContent() throws ClassNotFoundException {
        TextContent text = new TextContent("Hello world!");
        log("text:" + text);
        assertEquals(text.type, Content.TEXT);

        Map map = text.toDictionary();
        log("dictionary:" + map);
        Content content = Content.getInstance(map);
        log("content:" + content);
        assertEquals(text.type, content.type);
    }
}
