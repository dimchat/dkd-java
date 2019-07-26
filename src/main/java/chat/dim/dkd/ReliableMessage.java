/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
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
 *          //-- content data and key/keys
 *          data     : "...",  // base64_encode(symmetric)
 *          key      : "...",  // base64_encode(asymmetric)
 *          keys     : {
 *              "ID1": "key1", // base64_encode(asymmetric)
 *          },
 *          //-- signature
 *          signature: "..."   // base64_encode()
 *      }
 */
public final class ReliableMessage extends SecureMessage {

    private byte[] signature = null;

    @SuppressWarnings("unchecked")
    ReliableMessage(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public byte[] getSignature() {
        if (signature == null) {
            Object base64 = dictionary.get("signature");
            assert base64 != null;
            signature = getDelegate().decodeSignature(base64, this);
        }
        return signature;
    }

    private ReliableMessageDelegate getDelegate() {
        return (ReliableMessageDelegate) delegate;
    }

    @SuppressWarnings("unchecked")
    public static ReliableMessage getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof ReliableMessage) {
            return (ReliableMessage) object;
        } else if (object instanceof Map) {
            return new ReliableMessage((Map<String, Object>) object);
        } else  {
            throw new IllegalArgumentException("unknown message: " + object);
        }
    }

    /**
     *  Sender's Meta
     *      Extends for the first message package of 'Handshake' protocol.
     *
     * @param meta - Meta object or dictionary
     */
    public void setMeta(Map<String, Object> meta) {
        dictionary.put("meta", meta);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMeta() {
        return (Map<String, Object>) dictionary.get("meta");
    }

    /*
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

    /**
     *  Verify 'data' and 'signature' field with sender's public key
     *
     * @return SecureMessage object
     */
    public SecureMessage verify() {
        // 1. verify
        boolean OK = getDelegate().verifyDataSignature(getData(), getSignature(), envelope.sender, this);
        if (!OK) {
            //throw new RuntimeException("message signature not match: " + this);
            return null;
        }
        // 2. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("signature");
        return new SecureMessage(map);
    }
}
