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
public class Envelope extends Dictionary {

    public final Object sender;
    public final Object receiver;
    public final Date time;

    public Envelope(Map<String, Object> dictionary) {
        super(dictionary);
        this.sender   = (String) dictionary.get("sender");
        this.receiver = (String) dictionary.get("receiver");
        this.time     = getTime(dictionary);
    }

    public Envelope(String jsonString) {
        this(Utils.jsonDecode(jsonString));
    }

    public Envelope(Object sender, Object receiver, Date time) {
        super();
        this.sender   = sender;
        this.receiver = receiver;
        this.time     = time;
        dictionary.put("sender", sender);
        dictionary.put("receiver", receiver);
        dictionary.put("time", getTimestamp(time));
    }

    public Envelope(Object sender, Object receiver) {
        this(sender, receiver, new Date());
    }

    private static Date getTime(Map<String, Object> map) {
        long timestamp = (long) map.get("time");
        return new Date(timestamp * 1000);
    }

    private static long getTimestamp(Date time) {
        return time.getTime() / 1000;
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
}
