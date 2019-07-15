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
 *          //-- content data and key/keys
 *          data     : "...",  // base64_encode(symmetric)
 *          key      : "...",  // base64_encode(asymmetric)
 *          keys     : {
 *              "ID1": "key1", // base64_encode(asymmetric)
 *          }
 *      }
 */
public class SecureMessage extends Message {

    public SecureMessageDelegate delegate;

    public SecureMessage(Map<String, Object> dictionary) {
        super(dictionary);
    }

    protected byte[] getData() {
        Object base64 = dictionary.get("data");
        if (base64 == null) {
            throw new NullPointerException("encrypted data not found: " + dictionary);
        }
        return delegate.decodeContentData(base64, this);
    }

    private byte[] getKey() {
        Object base64 = dictionary.get("key");
        return base64 == null ? null : delegate.decodeKeyData(base64, this);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> getKeys() {
        return (Map<Object, Object>) dictionary.get("keys");
    }

    @SuppressWarnings("unchecked")
    public static SecureMessage getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof SecureMessage) {
            return (SecureMessage) object;
        } else if (object instanceof Map) {
            return new SecureMessage((Map<String, Object>) object);
        } else  {
            throw new IllegalArgumentException("unknown message: " + object);
        }
    }

    /**
     *  Split the group message to single person messages
     *
     *  @param members - group members
     *  @return secure/reliable message(s)
     */
    public List<SecureMessage> split(List members) {
        List<SecureMessage> messages = new ArrayList<>(members.size());

        Map<String, Object> msg = new HashMap<>(dictionary);
        // NOTICE: this help the receiver knows the group ID when the group message separated to multi-messages
        //         if don't want the others know you are the group members, modify it
        msg.put("group", envelope.receiver);

        Map<Object, Object> keys = getKeys();
        Object base64;
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
            if (msg.containsKey("signature")) {
                messages.add(new ReliableMessage(msg));
            } else {
                messages.add(new SecureMessage(msg));
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
        Map<Object, Object> keys = getKeys();
        if (keys != null) {
            Object base64 = keys.get(member);
            if (base64 != null) {
                msg.put("key", base64);
            }
            msg.remove("keys");
        }
        // repack
        if (msg.containsKey("signature")) {
            return new ReliableMessage(msg);
        } else {
            return new SecureMessage(msg);
        }
    }

    /*
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

    /**
     *  Decrypt message, replace encrypted 'data' with 'content' field
     *
     * @return InstantMessage object
     */
    public InstantMessage decrypt() {
        Object sender = envelope.sender;
        Object receiver = envelope.receiver;
        assert getGroup() == null;
        return decryptData(getKey(), sender, receiver);
    }

    /**
     *  Decrypt group message, replace encrypted 'data' with 'content' field
     *
     * @param member - receiver (as group member) ID
     * @return InstantMessage object
     */
    public InstantMessage decrypt(Object member) {
        Object sender = envelope.sender;
        Object receiver = envelope.receiver;
        // check group
        Object group = getGroup();
        if (group == null) {
            // if 'group' not exists, the 'receiver' must be a group ID, and
            // it is not equal to the member of course
            if (receiver.equals(member)) {
                throw new IllegalArgumentException("receiver error: " + receiver);
            }
            group = receiver;
        } else {
            // if 'group' exists and the 'receiver' is a group ID too,
            // they must be equal; or the 'receiver' must equal to member
            if (!receiver.equals(group) && !receiver.equals(member)) {
                throw new IllegalArgumentException("receiver error: " + receiver);
            }
            // and the 'group' must not equal to member of course
            if (group.equals(member)) {
                throw new IllegalArgumentException("member error: " + member);
            }
        }
        byte[] key = getKey();
        Map<Object, Object> keys = getKeys();
        if (keys != null) {
            Object base64 = keys.get(member);
            if (base64 != null) {
                key = delegate.decodeKeyData(base64, this);
            }
        }
        return decryptData(key, sender, group);
    }

    private InstantMessage decryptData(byte[] key, Object sender, Object receiver) {
        // 1. decrypt 'key' to symmetric key
        Map<String, Object> password = delegate.decryptKey(key, sender, receiver, this);

        // 2. decrypt 'data' to 'content'
        //    (remember to save password for decrypted File/Image/Audio/Video data)
        Content content = delegate.decryptContent(getData(), password, this);
        if (content == null) {
            throw new NullPointerException("failed to decrypt message data: " + this);
        }

        // 3. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("key");
        map.remove("data");
        map.put("content", content);
        return new InstantMessage(map);
    }

    /*
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

    /**
     *  Sign message.data, add 'signature' field
     *
     * @return ReliableMessage object
     */
    public ReliableMessage sign() {
        // 1. sign
        byte[] signature = delegate.signData(getData(), envelope.sender, this);
        if (signature == null) {
            throw new NullPointerException("failed to sign message: " + this);
        }
        // 2. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.put("signature", delegate.encodeSignature(signature, this));
        return new ReliableMessage(map);
    }
}
