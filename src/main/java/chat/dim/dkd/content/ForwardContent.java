package chat.dim.dkd.content;

import chat.dim.dkd.ReliableMessage;

import java.util.Map;

/**
 *  Top-Secret message: {
 *      type : 0xFF,
 *      sn   : 456,
 *
 *      forward : {...}  // reliable (secure + certified) message
 *  }
 */
public class ForwardContent extends Content {

    public final ReliableMessage forwardMessage;

    public ForwardContent(ForwardContent content) {
        super(content);
        this.forwardMessage = content.forwardMessage;
    }

    public ForwardContent(Map<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        this.forwardMessage = ReliableMessage.getInstance(dictionary.get("forward"));
    }

    public ForwardContent(ReliableMessage message) {
        super(FORWARD);
        this.forwardMessage = message;
        this.dictionary.put("forward", message);
    }
}
