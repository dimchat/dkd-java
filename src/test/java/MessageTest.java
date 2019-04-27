import chat.dim.dkd.Utils;
import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.TextContent;

import java.util.ArrayList;
import java.util.List;
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

        Map map = text;
        log("dictionary:" + map);
        Content content = Content.getInstance(map);
        log("content:" + content);
        assertEquals(text.type, content.type);
    }

    @Test
    public void testJSON() throws ClassNotFoundException {
        String json = "{\"sn\":1952110619,\"text\":\"Hey guy!\",\"type\":1}";
        Map<String, Object> dictionary = Utils.jsonDecode(json);
        Content content = Content.getInstance(dictionary);
        log("content:" + content);

        String string1 = Utils.jsonEncode(content);
        log("json string1:" + string1);

        String string2 = Utils.jsonEncode(content);
        log("json string2:" + string2);
        assertEquals(string1, string2);

        List<Content> array = new ArrayList<>();
        array.add(content);
        array.add(new Content(content));
        String string3 = Utils.jsonEncode(array);
        log("json string3:" + string3);
    }
}
