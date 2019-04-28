package chat.dim.dkd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  Common Message
 *
 *      data format: {
 *          //-- envelope
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123,
 *          //-- others
 *          ...
 *      }
 */
public class Message extends Dictionary {

    public final Envelope envelope;

    public Message(Map<String, Object> dictionary) {
        super(dictionary);
        // build envelope
        Map<String, Object> env = new HashMap<>();
        env.put("sender", dictionary.get("sender"));
        env.put("receiver", dictionary.get("receiver"));
        env.put("time", dictionary.get("time"));
        envelope = new Envelope(env);
    }

    public Message(Envelope head) {
        super();
        envelope = head;
        // copy values from envelope
        dictionary.put("sender", head.get("sender"));
        dictionary.put("receiver", head.get("receiver"));
        dictionary.put("time", head.get("time"));
    }
}
