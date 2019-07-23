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
public final class InstantMessage extends Message {

    public final Content content;

    public InstantMessageDelegate delegate;

    public InstantMessage(Map<String, Object> dictionary) {
        super(dictionary);
        content = Content.getInstance(dictionary.get("content"));
    }

    public InstantMessage(Content body, Envelope head) {
        super(head);
        content = body;
        dictionary.put("content", body);
    }

    public InstantMessage(Content body, Object from, Object to) {
        super(from, to);
        content = body;
        dictionary.put("content", body);
    }

    public InstantMessage(Content body, Object from, Object to, Date when) {
        super(from, to, when);
        content = body;
        dictionary.put("content", body);
    }

    public InstantMessage(Content body, Object from, Object to, long timestamp) {
        super(from, to, timestamp);
        content = body;
        dictionary.put("content", body);
    }

    public Object getGroup() {
        return content.getGroup();
    }

    public void setGroup(Object ID) {
        content.setGroup(ID);
    }

    @SuppressWarnings("unchecked")
    public static InstantMessage getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof InstantMessage) {
            return (InstantMessage) object;
        } else if (object instanceof Map) {
            return new InstantMessage((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown message: " + object);
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
        byte[] key = delegate.encryptKey(password, envelope.receiver, this);
        Object base64 = delegate.encodeKeyData(key, this);
        if (base64 != null) {
            map.put("key", base64);
        }

        // 3. pack message
        return new SecureMessage(map);
    }

    /**
     *  Encrypt group message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @param members - group members
     * @return SecureMessage object
     * @throws NoSuchFieldException when 'group' field not found
     */
    public SecureMessage encrypt(Map<String, Object> password, List members) throws NoSuchFieldException {
        // 1. encrypt 'content' to 'data'
        Map<String, Object> map = encryptContent(password);

        // 2. encrypt password to 'keys'
        Map<Object, Object> keys = new HashMap<>();
        byte[] key;
        Object base64;
        for (Object member: members) {
            key = delegate.encryptKey(password, member, this);
            base64 = delegate.encodeKeyData(key, this);
            if (base64 != null) {
                keys.put(member, base64);
            }
        }
        if (keys.size() > 0) {
            map.put("keys", keys);
        }
        // group ID
        Object group = getGroup();
        if (group == null) {
            throw new NoSuchFieldException("group message error: " + this);
        }
        // NOTICE: this help the receiver knows the group ID when the group message separated to multi-messages
        //         if don't want the others know you are the group members, modify it
        map.put("group", group);

        // 3. pack message
        return new SecureMessage(map);
    }

    private Map<String, Object> encryptContent(Map<String, Object> password) {
        // 1. check attachment for File/Image/Audio/Video message content
        //    (do it in 'core' module)

        // 2. encrypt message content
        byte[] data = delegate.encryptContent(content, password, this);
        if (data == null) {
            throw new NullPointerException("failed to encrypt content with key: " + password);
        }

        // 3. replace 'content' with encrypted 'data'
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", delegate.encodeContentData(data, this));
        return map;
    }
}
