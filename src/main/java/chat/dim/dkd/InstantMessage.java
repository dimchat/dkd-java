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
        content = Content.getInstance(dictionary.get("content"));
    }

    public InstantMessage(Content body, Envelope head) {
        super(head);
        content = body;
        dictionary.put("content", body);
    }

    public InstantMessage(Content body, Object from, Object to, Date when) {
        this(body, new Envelope(from, to, when));
    }

    public InstantMessage(Content body, Object from, Object to) {
        this(body, from, to, new Date());
    }

    @SuppressWarnings("unchecked")
    public static InstantMessage getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof InstantMessage) {
            return (InstantMessage) object;
        } else if (object instanceof Map) {
            return new InstantMessage((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown message:" + object);
        }
    }

    /*
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

    /**
     *  Encrypt message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @return SecureMessage object
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

    /**
     *  Encrypt group message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @param members - group members
     * @return SecureMessage object
     * @throws NoSuchFieldException when 'group' field not found
     */
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
        Object group = content.getGroup();
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
        Content body = content;

        // 1. check attachment for File/Image/Audio/Video message content
        switch (content.type) {
            case Content.IMAGE:
            case Content.AUDIO:
            case Content.VIDEO:
            case Content.FILE: {
                FileContent file = new FileContent(content);
                String url = delegate.uploadFileData(this, file.getData(), file.getFilename(), password);
                if (url != null) {
                    file.setUrl(url);
                    file.setData(null);
                    body = file;
                }
                break;
            }
            default: {
                break;
            }
        }

        // 2. encrypt message content
        byte[] data = delegate.encryptContent(this, body, password);
        if (data == null) {
            throw new NullPointerException("failed to encrypt content with key:" + password);
        }

        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", Utils.base64Encode(data));
        return map;
    }
}
