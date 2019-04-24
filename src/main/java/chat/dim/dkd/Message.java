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
public class Message {

    protected final Map<String, Object> dictionary;

    public final Envelope envelope;

    public Message(Message message) {
        super();
        this.dictionary = message.dictionary;
        this.envelope   = message.envelope;
    }

    public Message(Map<String, Object> dictionary) {
        super();
        this.dictionary = dictionary;

        Map<String, Object> env = new HashMap<>();
        env.put("sender", dictionary.get("sender"));
        env.put("receiver", dictionary.get("receiver"));
        env.put("time", dictionary.get("time"));
        this.envelope = new Envelope(env);
    }

    public Message(Envelope envelope) {
        super();
        this.dictionary = new HashMap<>(envelope.toDictionary());
        this.envelope = envelope;
    }

    public Message(String sender, String receiver, Date time) {
        this(new Envelope(sender, receiver, time));
    }

    public Message(String sender, String receiver) {
        this(sender, receiver, new Date());
    }

    public String toString() {
        return dictionary.toString();
    }

    public Map<String, Object> toDictionary() {
        return dictionary;
    }
}
