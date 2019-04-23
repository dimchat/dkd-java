package chat.dim.dkd;

import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.FileContent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public IInstantMessageDelegate delegate;

    public InstantMessage(InstantMessage message) {
        super(message);
        // content
        this.content = message.content;
    }

    public InstantMessage(HashMap<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        // content
        Object content = dictionary.get("content");
        if (content == null) {
            throw new NoSuchFieldException("message content not found:" + dictionary);
        }
        this.content = new Content((HashMap<String, Object>) content);
    }

    public InstantMessage(Content content, Envelope envelope) {
        super(envelope);
        // content
        this.content = content;
        this.dictionary.put("content", content.toDictionary());
    }

    public InstantMessage(Content content, String sender, String receiver, Date time) {
        this(content, new Envelope(sender, receiver, time));
    }

    public InstantMessage(Content content, String sender, String receiver) {
        this(content, sender, receiver, new Date());
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

    public SecureMessage encrypt(HashMap<String, Object> password) {
        // 1. encrypt 'content' to 'data'
        HashMap<String, Object> map = encryptContent(password);

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

    public SecureMessage encrypt(HashMap<String, Object> password, List<String> members) throws NoSuchFieldException {
        // 1. encrypt 'content' to 'data'
        HashMap<String, Object> map = encryptContent(password);

        // 2. encrypt password to 'keys'
        HashMap<String, String> keys = new HashMap<>();
        byte[] key;
        for (String ID: members) {
            key = delegate.encryptKey(this, password, ID);
            if (key == null) {
                // NOTICE: reused key
            } else {
                keys.put(ID, Utils.base64Encode(key));
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

    private HashMap<String, Object> encryptContent(HashMap<String, Object> password) {
        Content result = content;

        // 1. check attachment for File/Image/Audio/Video message content
        switch (content.type) {
            case Content.IMAGE:
            case Content.AUDIO:
            case Content.VIDEO:
            case Content.FILE: {
                FileContent file = new FileContent(content.toDictionary());
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

        HashMap<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", Utils.base64Encode(data));
        return map;
    }
}
