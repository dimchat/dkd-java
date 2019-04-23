package chat.dim.dkd.content;

import java.util.HashMap;

/**
 *  Text message: {
 *      type : 0x01,
 *      sn   : 123,
 *
 *      text : "..."
 *  }
 */
public class TextContent extends Content {

    public String text;

    public TextContent(TextContent content) {
        super(content);
        this.text = content.text;
    }

    public TextContent(HashMap<String, Object> dictionary) {
        super(dictionary);
        this.text = (String) dictionary.get("text");
    }

    public TextContent(String text) {
        super(TEXT);
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        this.dictionary.put("text", text);
    }
}
