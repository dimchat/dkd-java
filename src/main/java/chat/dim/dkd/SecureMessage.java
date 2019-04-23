package chat.dim.dkd;

import chat.dim.dkd.content.Content;
import chat.dim.dkd.content.FileContent;

import java.util.Date;
import java.util.HashMap;

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
    public final HashMap<String, String> keys;

    public ISecureMessageDelegate delegate;

    public SecureMessage(SecureMessage message) {
        super(message);
        // encrypted data
        this.data = message.data;
        // decrypt key
        this.key = message.key;
        // keys for group message
        this.keys = message.keys;
    }

    public SecureMessage(HashMap<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        // encrypted data
        Object data = dictionary.get("data");
        if (data == null) {
            throw new NoSuchFieldException("encrypted data not found:" + dictionary);
        }
        this.data = Utils.base64Decode((String) data);
        // decrypt key
        Object key = dictionary.get("key");
        if (key == null) {
            this.key = null;
        } else {
            this.key = Utils.base64Decode((String) key);
        }
        // keys for group message
        Object keys = dictionary.get("keys");
        if (keys == null) {
            this.keys = null;
        } else {
            this.keys = (HashMap<String, String>) keys;
        }
    }

    public SecureMessage(byte[] data, byte[] key, Envelope envelope) {
        super(envelope);
        // encrypted data
        this.data = data;
        this.dictionary.put("data", Utils.base64Encode(data));
        // decrypt key
        this.key = key;
        if (key != null) {
            this.dictionary.put("key", Utils.base64Encode(key));
        }
        // keys for group message
        this.keys = null;
    }

    public SecureMessage(byte[] data, HashMap<String, String> keys, Envelope envelope) {
        super(envelope);
        // encrypted data
        this.data = data;
        this.dictionary.put("data", Utils.base64Encode(data));
        // decrypt key
        this.key = null;
        // keys for group message
        this.keys = keys;
        this.dictionary.put("keys", keys);
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
        String sender = envelope.sender;
        String receiver = envelope.receiver;
        return decryptData(key, sender, receiver);
    }

    public InstantMessage decrypt(String member) {
        String sender = envelope.sender;
        String receiver = envelope.receiver;
        // check group
        String group = (String) dictionary.get("group");
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
        return decryptData(key, sender, receiver);
    }

    private InstantMessage decryptData(byte[] key, String sender, String receiver) {
        // 1. decrypt 'key' to symmetric key
        HashMap<String, Object> password = delegate.decryptKey(this, key, sender, receiver);
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
                FileContent file = new FileContent(content.toDictionary());
                file.setPassword(password);
                content = file;
                break;
            }
            default: {
                break;
            }
        }

        // 4. pack message
        HashMap<String, Object> map = new HashMap<>(dictionary);
        map.remove("key");
        map.remove("data");
        map.put("content", content.toDictionary());
        try {
            return new InstantMessage(map);
        } catch (NoSuchFieldException e) {
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
        HashMap<String, Object> map = new HashMap<>(dictionary);
        map.put("signature", Utils.base64Encode(signature));
        try {
            return new ReliableMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
