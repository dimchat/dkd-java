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
        this.envelope = new Envelope(env);
    }

    public Message(Envelope envelope) {
        super();
        this.envelope = envelope;
        // copy values from envelope
        dictionary.put("sender", envelope.sender);
        dictionary.put("receiver", envelope.receiver);
        dictionary.put("time", envelope.time);
    }

    public Message(Object sender, Object receiver, Date time) {
        this(new Envelope(sender, receiver, time));
    }

    public Message(Object sender, Object receiver) {
        this(sender, receiver, new Date());
    }
}
