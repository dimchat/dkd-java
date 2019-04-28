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
        sender   = dictionary.get("sender");
        receiver = dictionary.get("receiver");
        time     = getTime(dictionary);
    }

    public Envelope(Object from, Object to, Date when) {
        super();
        sender   = from;
        receiver = to;
        time     = when;
        dictionary.put("sender", from);
        dictionary.put("receiver", to);
        dictionary.put("time", getTimestamp(when));
    }

    public Envelope(Object from, Object to) {
        this(from, to, new Date());
    }

    private static Date getTime(Map<String, Object> map) {
        long timestamp = 0;
        Object time = map.get("time");
        if (time == null) {
            // timestamp = 0;
            return new Date();
        } else {
            timestamp = (long) map.get("time");
        }
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
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }
}
