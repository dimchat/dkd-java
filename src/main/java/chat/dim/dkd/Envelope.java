package chat.dim.dkd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  Envelope for message
 *
 *      data format: {
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123
 *      }
 */
public class Envelope {

    private final Map<String, Object> dictionary;

    public final String sender;
    public final String receiver;
    public final Date time;

    public Envelope(Envelope envelope) {
        super();
        this.dictionary = envelope.dictionary;
        this.sender     = envelope.sender;
        this.receiver   = envelope.receiver;
        this.time       = envelope.time;
    }

    public Envelope(Map<String, Object> dictionary) {
        super();
        this.dictionary = dictionary;
        this.sender     = (String) dictionary.get("sender");
        this.receiver   = (String) dictionary.get("receiver");
        this.time       = getDate((Long) dictionary.get("time"));
    }

    public Envelope(String jsonString) {
        this(Utils.jsonDecode(jsonString));
    }

    public Envelope(String sender, String receiver, Date time) {
        super();

        Map<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("time", getTimestamp(time));

        this.dictionary = map;
        this.sender     = sender;
        this.receiver   = receiver;
        this.time       = time;
    }

    public Envelope(String sender, String receiver) {
        this(sender, receiver, new Date());
    }

    @SuppressWarnings("unchecked")
    public static Envelope getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Envelope) {
            return (Envelope) object;
        } else if (object instanceof Map) {
            return new Envelope((Map<String, Object>) object);
        } else if (object instanceof String) {
            return new Envelope((String) object);
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }

    public String toString() {
        return dictionary.toString();
    }

    public Map<String, Object> toDictionary() {
        return dictionary;
    }

    private long getTimestamp(Date time) {
        return time.getTime() / 1000;
    }

    private Date getDate(long timestamp) {
        return new Date(timestamp * 1000);
    }
}
