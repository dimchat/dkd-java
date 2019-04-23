package chat.dim.dkd.content;

import chat.dim.dkd.ReliableMessage;

import java.util.HashMap;

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

    public ForwardContent(HashMap<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        // top-secret message to forward
        Object forward = dictionary.get("forward");
        if (forward == null) {
            throw new NoSuchFieldException("forward message not found:" + dictionary);
        }
        this.forwardMessage = new ReliableMessage((HashMap<String, Object>) forward);
    }

    public ForwardContent(ReliableMessage message) {
        super(FORWARD);
        this.forwardMessage = message;
        this.dictionary.put("forward", message.toDictionary());
    }
}
