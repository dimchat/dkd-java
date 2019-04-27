package chat.dim.dkd;

import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.FileContent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Instant Message
 *
 *      data format: {
 *          //-- envelope
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123,
 *          //-- content
 *          content  : {...}
 *      }
 */
public class InstantMessage extends Message {

    public final Content content;

    public InstantMessageDelegate delegate;

    public InstantMessage(Map<String, Object> dictionary) throws ClassNotFoundException {
        super(dictionary);
        this.content = Content.getInstance(dictionary.get("content"));
    }

    public InstantMessage(String jsonString) throws ClassNotFoundException {
        this(Utils.jsonDecode(jsonString));
    }

    public InstantMessage(Content content, Envelope envelope) {
        super(envelope);
        this.content = content;
        dictionary.put("content", content);
    }

    public InstantMessage(Content content, Object sender, Object receiver, Date time) {
        this(content, new Envelope(sender, receiver, time));
    }

    public InstantMessage(Content content, Object sender, Object receiver) {
        this(content, sender, receiver, new Date());
    }

    @SuppressWarnings("unchecked")
    public static InstantMessage getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof InstantMessage) {
            return (InstantMessage) object;
        } else if (object instanceof Map) {
            return new InstantMessage((Map<String, Object>) object);
        } else if (object instanceof String) {
            return new InstantMessage((String) object);
        } else {
            throw new IllegalArgumentException("unknown message:" + object);
        }
    }

    /**
     *  Encrypt the Instant Message to Secure Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | content  |      | data     |  1. data = encrypt(content, PW)
     *    +----------+      | key/keys |  2. key  = encrypt(PW, receiver.PK)
     *                      +----------+
     */

    public SecureMessage encrypt(Map<String, Object> password) {
        // 1. encrypt 'content' to 'data'
        Map<String, Object> map = encryptContent(password);

        // 2. encrypt password to 'key'
        byte[] key = delegate.encryptKey(this, password, envelope.receiver);
        if (key == null) {
            // NOTICE: reused key
        } else {
            map.put("key", Utils.base64Encode(key));
        }

        // 3. pack message
        try {
            return new SecureMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecureMessage encrypt(Map<String, Object> password, List<Object> members) throws NoSuchFieldException {
        // 1. encrypt 'content' to 'data'
        Map<String, Object> map = encryptContent(password);

        // 2. encrypt password to 'keys'
        Map<Object, String> keys = new HashMap<>();
        byte[] key;
        for (Object member: members) {
            key = delegate.encryptKey(this, password, member);
            if (key != null) {
                keys.put(member, Utils.base64Encode(key));
            }
        }
        map.put("keys", keys);
        // group ID
        String group = content.group;
        if (group == null) {
            throw new NoSuchFieldException("group message error:" + this);
        }
        // NOTICE: this help the receiver knows the group ID when the group message separated to multi-messages
        //         if don't want the others know you are the group members, modify it
        map.put("group", group);

        // 3. pack message
        try {
            return new SecureMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> encryptContent(Map<String, Object> password) {
        Content result = content;

        // 1. check attachment for File/Image/Audio/Video message content
        switch (content.type) {
            case Content.IMAGE:
            case Content.AUDIO:
            case Content.VIDEO:
            case Content.FILE: {
                FileContent file = new FileContent(content);
                String url = delegate.uploadFileData(this, file.data, file.filename, password);
                if (url != null) {
                    file.setUrl(url);
                    file.setData(null);
                    result = file;
                }
                break;
            }
            default: {
                break;
            }
        }

        // 2. encrypt message content
        byte[] data = delegate.encryptContent(this, result, password);
        if (data == null) {
            throw new NullPointerException("failed to encrypt content with key:" + password);
        }

        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", Utils.base64Encode(data));
        return map;
    }
}
