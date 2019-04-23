package chat.dim.dkd;

import java.util.Date;
import java.util.HashMap;

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

    public IReliableMessageDelegate delegate;

    // Extends for the first message package of 'Handshake' protocol
    public HashMap<String, Object> meta;

    public ReliableMessage(ReliableMessage message) {
        super(message);
        // signature for encrypted data
        this.signature = message.signature;
        // meta
        this.meta = message.meta;
    }

    public ReliableMessage(HashMap<String, Object> dictionary) throws NoSuchFieldException {
        super(dictionary);
        // signature for encrypted data
        Object signature = dictionary.get("signature");
        if (signature == null) {
            throw new NoSuchFieldException("signature not found:" + dictionary);
        }
        this.signature = Utils.base64Decode((String) signature);
        // meta
        this.meta = (HashMap<String, Object>) dictionary.get("meta");
    }

    public ReliableMessage(byte[] signature, byte[] data, byte[] key, Envelope envelope) {
        super(data, key, envelope);
        this.signature = signature;
    }

    public ReliableMessage(byte[] signature, byte[] data, HashMap<String, String> keys, Envelope envelope) {
        super(data, keys, envelope);
        this.signature = signature;
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
        HashMap<String, Object> map = new HashMap<>(dictionary);
        map.remove("signature");
        try {
            return new SecureMessage(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
