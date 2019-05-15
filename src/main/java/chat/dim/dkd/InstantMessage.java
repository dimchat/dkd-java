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
        if (key != null) {
            map.put("key", Base64.encode(key));
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
                keys.put(member, Base64.encode(key));
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
        return new SecureMessage(map);
    }

    private Map<String, Object> encryptContent(Map<String, Object> password) {
        // 1. encrypt message content
        //    (remember to check attachment for File/Image/Audio/Video message content first)
        byte[] data = delegate.encryptContent(this, content, password);
        if (data == null) {
            throw new NullPointerException("failed to encrypt content with key:" + password);
        }

        // 2. replace 'content' with encrypted 'data'
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", Base64.encode(data));
        return map;
    }
}
