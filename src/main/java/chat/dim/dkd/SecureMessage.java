package chat.dim.dkd;

import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.FileContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Secure Message
 *      Instant Message encrypted by a symmetric key
 *
 *      data format: {
 *          //-- envelope
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123,
 *          //-- content data & key/keys
 *          data     : "...",  // base64_encode(symmetric)
 *          key      : "...",  // base64_encode(asymmetric)
 *          keys     : {
 *              "ID1": "key1", // base64_encode(asymmetric)
 *          }
 *      }
 */
public class SecureMessage extends Message {

    public final byte[] data;
    public final byte[] key;
    public final Map<Object, String> keys;

    public SecureMessageDelegate delegate;

    @SuppressWarnings("unchecked")
    public SecureMessage(Map<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);

        // encrypted data
        String base64 = (String) dictionary.get("data");
        if (base64 == null) {
            throw new NoSuchFieldException("encrypted data not found:" + dictionary);
        }
        data = Utils.base64Decode(base64);

        // decrypt key
        base64 = (String) dictionary.get("key");
        if (base64 == null) {
            key = null;
        } else {
            key = Utils.base64Decode(base64);
        }

        // keys for group message
        Map<Object, String> map = (Map<Object, String>) dictionary.get("keys");
        if (map == null) {
            keys = null;
        } else {
            keys = map;
        }
    }

    public SecureMessage(byte[] encryptedData, byte[] keyData, Envelope head) {
        super(head);

        // encrypted data
        data = encryptedData;
        dictionary.put("data", Utils.base64Encode(encryptedData));

        // decrypt key
        key = keyData;
        if (key != null) {
            dictionary.put("key", Utils.base64Encode(keyData));
        }

        // keys for group message
        keys = null;
    }

    public SecureMessage(byte[] encryptedData, Map<Object, String> keyMap, Envelope head) {
        super(head);

        // encrypted data
        data = encryptedData;
        dictionary.put("data", Utils.base64Encode(encryptedData));

        // decrypt key
        key = null;

        // keys for group message
        keys = keyMap;
        dictionary.put("keys", keyMap);
    }

    @SuppressWarnings("unchecked")
    public static SecureMessage getInstance(Object object) throws NoSuchFieldException {
        if (object == null) {
            return null;
        } else if (object instanceof SecureMessage) {
            return (SecureMessage) object;
        } else if (object instanceof Map) {
            return new SecureMessage((Map<String, Object>) object);
        } else  {
            throw new IllegalArgumentException("unknown message:" + object);
        }
    }

    /**
     *  Group ID
     *      when a group message was splitted/trimmed to a single message
     *      the 'receiver' will be changed to a member ID, and
     *      the group ID will be saved as 'group'.
     */
    public Object getGroup() {
        return dictionary.get("group");
    }

    public void setGroup(Object ID) {
        dictionary.put("group", ID);
    }

    /**
     *  Split the group message to single person messages
     *
     *  @param members - group members
     *  @return secure/reliable message(s)
     */
    public List<SecureMessage> split(List<Object> members) {
        List<SecureMessage> messages = new ArrayList<>(members.size());

        Map<String, Object> msg = new HashMap<>(dictionary);
        // NOTICE: this help the receiver knows the group ID when the group message separated to multi-messages
        //         if don't want the others know you are the group members, modify it
        msg.put("group", envelope.receiver);

        String base64;
        for (Object member : members) {
            // 1. change receiver to the group member
            msg.put("receiver", member);

            if (keys != null) {
                // 2. get encrypted key from map
                base64 = keys.get(member);
                if (base64 == null) {
                    msg.remove("key");
                } else {
                    msg.put("key", base64);
                }
            }

            // 3. repack message
            try {
                if (msg.containsKey("signature")) {
                    messages.add(new ReliableMessage(msg));
                } else {
                    messages.add(new SecureMessage(msg));
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    /**
     *  Trim the group message for a member
     *
     * @param member - group member ID/string
     * @return SecureMessage
     */
    public SecureMessage trim(Object member) {
        Map<String, Object> msg = new HashMap<>(dictionary);
        // get key from keys
        if (keys != null) {
            String base64 = keys.get(member);
            if (base64 != null) {
                msg.put("key", base64);
            }
            msg.remove("keys");
        }
        // repack
        try {
            if (msg.containsKey("signature")) {
                return new ReliableMessage(msg);
            } else {
                return new SecureMessage(msg);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  Decrypt the Secure Message to Instant Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |  1. PW      = decrypt(key, receiver.SK)
     *    | data     |      | content  |  2. content = decrypt(data, PW)
     *    | key/keys |      +----------+
     *    +----------+
     */

    public InstantMessage decrypt() {
        if (dictionary.containsKey("group")) {
            throw new RuntimeException("group message must be decrypted with member ID");
        }
        Object sender = envelope.sender;
        Object receiver = envelope.receiver;
        return decryptData(key, sender, receiver);
    }

    public InstantMessage decrypt(Object member) {
        Object sender = envelope.sender;
        Object receiver = envelope.receiver;
        // check group
        Object group = dictionary.get("group");
        if (group == null) {
            // if 'group' not exists, the 'receiver' must be a group ID, and
            // it is not equal to the member of course
            if (receiver.equals(member)) {
                throw new IllegalArgumentException("receiver error:" + receiver);
            }
            group = receiver;
        } else {
            // if 'group' exists and the 'receiver' is a group ID too,
            // they must be equal; or the 'receiver' must equal to member
            if (!receiver.equals(group) && !receiver.equals(member)) {
                throw new IllegalArgumentException("receiver error:" + receiver);
            }
            // and the 'group' must not equal to member of course
            if (group.equals(member)) {
                throw new IllegalArgumentException("member error:" + member);
            }
        }
        byte[] key = null;
        if (keys != null) {
            String base64 = keys.get(member);
            if (base64 != null) {
                key = Utils.base64Decode(base64);
            }
        }
        return decryptData(key, sender, group);
    }

    private InstantMessage decryptData(byte[] key, Object sender, Object receiver) {
        // 1. decrypt 'key' to symmetric key
        Map<String, Object> password = delegate.decryptKey(this, key, sender, receiver);
        if (password == null) {
            throw new NullPointerException("failed to decrypt symmetric key:" + this);
        }

        // 2. decrypt 'data' to 'content'
        Content content = delegate.decryptContent(this, data, password);
        if (content == null) {
            throw new NullPointerException("failed to decrypt message data:" + this);
        }

        // 3. check attachment for File/Image/Audio/Video message content
        switch (content.type) {
            case Content.IMAGE:
            case Content.AUDIO:
            case Content.VIDEO:
            case Content.FILE: {
                FileContent file = new FileContent(content);
                file.setPassword(password);
                content = file;
                break;
            }
            default: {
                break;
            }
        }

        // 4. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("key");
        map.remove("data");
        map.put("content", content);
        try {
            return new InstantMessage(map);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  Sign the Secure Message to Reliable Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | data     |      | data     |
     *    | key/keys |      | key/keys |
     *    +----------+      | signature|  1. signature = sign(data, sender.SK)
     *                      +----------+
     */

    public ReliableMessage sign() {
        // 1. sign
        byte[] signature = delegate.signData(this, data, envelope.sender);
        if (signature == null) {
            throw new NullPointerException("failed to sign message:" + this);
        }
        // 2. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.put("signature", Utils.base64Encode(signature));
        try {
            return new ReliableMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
