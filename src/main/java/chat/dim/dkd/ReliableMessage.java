package chat.dim.dkd;

import java.util.HashMap;
import java.util.Map;

/**
 *  Instant Message signed by an asymmetric key
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
 *          },
 *          //-- signature
 *          signature: "..."   // base64_encode()
 *      }
 */
public class ReliableMessage extends SecureMessage {

    public final byte[] signature;

    public ReliableMessageDelegate delegate;

    // Extends for the first message package of 'Handshake' protocol
    public Map<String, Object> meta;

    @SuppressWarnings("unchecked")
    public ReliableMessage(Map<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        // signature for encrypted data
        String signature = (String) dictionary.get("signature");
        if (signature == null) {
            throw new NoSuchFieldException("signature not found:" + dictionary);
        }
        this.signature = Utils.base64Decode(signature);
        // meta
        this.meta = (Map<String, Object>) dictionary.get("meta");
    }

    public ReliableMessage(String jsonString) throws NoSuchFieldException {
        this(Utils.jsonDecode(jsonString));
    }

    public ReliableMessage(byte[] signature, byte[] data, byte[] key, Envelope envelope) {
        super(data, key, envelope);
        this.signature = signature;
    }

    public ReliableMessage(byte[] signature, byte[] data, Map<Object, String> keys, Envelope envelope) {
        super(data, keys, envelope);
        this.signature = signature;
    }

    @SuppressWarnings("unchecked")
    public static ReliableMessage getInstance(Object object) throws NoSuchFieldException {
        if (object == null) {
            return null;
        } else if (object instanceof ReliableMessage) {
            return (ReliableMessage) object;
        } else if (object instanceof Map) {
            return new ReliableMessage((Map<String, Object>) object);
        } else if (object instanceof String) {
            return new ReliableMessage((String) object);
        } else  {
            throw new IllegalArgumentException("unknown message:" + object);
        }
    }

    /**
     *  Verify the Reliable Message to Secure Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | data     |      | data     |  1. verify(data, signature, sender.PK)
     *    | key/keys |      | key/keys |
     *    | signature|      +----------+
     *    +----------+
     */

    public SecureMessage verify() {
        // 1. verify
        boolean OK = delegate.verifyData(this, data, signature, envelope.sender);
        if (!OK) {
            throw new RuntimeException("message signature not match:" + this);
        }
        // 2. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("signature");
        try {
            return new SecureMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
