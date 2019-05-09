package chat.dim.dkd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Content extends Dictionary {

    // message type: text, image, ...
    public final int type;

    // random number to identify message content
    protected final long serialNumber;

    // Group ID/string for group message
    private Object group;

    public Content(Map<String, Object> dictionary) {
        super(dictionary);
        type         = (int) dictionary.get("type");
        serialNumber = Long.valueOf(dictionary.get("sn").toString());
        group        = dictionary.get("group");
    }

    protected Content(int msgType) {
        super();
        type         = msgType;
        serialNumber = randomNumber();
        group        = null;
        dictionary.put("type", type);
        dictionary.put("sn", serialNumber);
    }

    private static long randomNumber() {
        Random random = new Random();
        long sn = random.nextInt();
        if (sn < 0) {
            return sn + 1 + Integer.MAX_VALUE - Integer.MIN_VALUE;
        }
        return sn;
    }

    //-------- setter/getter --------

    public void setGroup(Object groupID) {
        group = groupID;
        dictionary.put("group", groupID);
    }

    public Object getGroup() {
        return group;
    }

    //-------- Runtime --------

    private static Map<Integer, Class> contentClasses = new HashMap<>();

    public static void register(Integer type, Class clazz) {
        // TODO: check whether clazz is subclass of Content
        contentClasses.put(type, clazz);
    }

    @SuppressWarnings("unchecked")
    private static Content createInstance(Map<String, Object> dictionary) throws ClassNotFoundException {
        int type = (int) dictionary.get("type");
        Class clazz = contentClasses.get(type);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown message type:" + type);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (Content) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Content getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof Content) {
            return (Content) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown message content:" + object);
        }
    }
}
