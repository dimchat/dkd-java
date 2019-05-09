import chat.dim.dkd.Content;

import java.util.Map;

/**
 *  Text message: {
 *      type : 0x01,
 *      sn   : 123,
 *
 *      text : "..."
 *  }
 */
public class TextContent extends Content {

    public static final int TEXT    = 0x01; // 0000 0001

    private String text;

    public TextContent(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public TextContent(String message) {
        super(TEXT);
        setText(message);
    }

    //-------- setter/getter --------

    public void setText(String message) {
        text = message;
        dictionary.put("text", message);
    }

    public String getText() {
        return text;
    }

//    static {
//        Content.register(TEXT, TextContent.class);
//    }
}