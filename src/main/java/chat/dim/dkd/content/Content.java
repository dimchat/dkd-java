package chat.dim.dkd.content;

import chat.dim.dkd.Dictionary;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Content extends Dictionary {

    /**
     *  @enum DKDMessageType
     *
     *  @abstract A flag to indicate what kind of message content this is.
     *
     *  @discussion A message is something send from one place to another one,
     *      it can be an instant message, a system command, or something else.
     *
     *      DKDMessageType_Text indicates this is a normal message with plaintext.
     *
     *      DKDMessageType_File indicates this is a file, it may include filename
     *      and file data, but usually the file data will encrypted and upload to
     *      somewhere and here is just a URL to retrieve it.
     *
     *      DKDMessageType_Image indicates this is an image, it may send the image
     *      data directly(encrypt the image data with Base64), but we suggest to
     *      include a URL for this image just like the 'File' message, of course
     *      you can get a thumbnail of this image here.
     *
     *      DKDMessageType_Audio indicates this is a voice message, you can get
     *      a URL to retrieve the voice data just like the 'File' message.
     *
     *      DKDMessageType_Video indicates this is a video file.
     *
     *      DKDMessageType_Page indicates this is a web page.
     *
     *      DKDMessageType_Quote indicates this message has quoted another message
     *      and the message content should be a plaintext.
     *
     *      DKDMessageType_Command indicates this is a command message.
     *
     *      DKDMessageType_Forward indicates here contains a TOP-SECRET message
     *      which needs your help to redirect it to the true receiver.
     *
     *  Bits:
     *      0000 0001 - this message contains plaintext you can read.
     *      0000 0010 - this is a message you can see.
     *      0000 0100 - this is a message you can hear.
     *      0000 1000 - this is a message for the robot, not for human.
     *
     *      0001 0000 - this message's main part is in somewhere else.
     *      0010 0000 - this message contains the 3rd party content.
     *      0100 0000 - (RESERVED)
     *      1000 0000 - this is a message send by the system, not human.
     *
     *      (All above are just some advices to help choosing numbers :P)
     */
    public static final int UNKNOWN = 0x00;
    public static final int TEXT    = 0x01; // 0000 0001

    public static final int FILE    = 0x10; // 0001 0000
    public static final int IMAGE   = 0x12; // 0001 0010
    public static final int AUDIO   = 0x14; // 0001 0100
    public static final int VIDEO   = 0x16; // 0001 0110

    // web page
    public static final int PAGE    = 0x20; // 0010 0000

    // quote a message before and reply it with text
    public static final int QUOTE   = 0x37; // 0011 0111

    public static final int COMMAND = 0x88; // 1000 1000
    public static final int HISTORY = 0x89; // 1000 1001 (Entity history command)

    // top-secret message forward by proxy (Service Provider)
    public static final int FORWARD = 0xFF; // 1111 1111

    //-------- message types end --------

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

    static {
        // Text
        register(TEXT, TextContent.class);
        // File
        register(FILE, FileContent.class);
        // Image
        register(IMAGE, ImageContent.class);
        // Audio
        register(AUDIO, AudioContent.class);
        // Video
        register(VIDEO, VideoContent.class);
        // Page
        register(PAGE, PageContent.class);
        // Quote
        // Command
        register(COMMAND, CommandContent.class);
        // History
        register(HISTORY, HistoryCommand.class);
        // Forward
        register(FORWARD, ForwardContent.class);
        // ...
    }
}
