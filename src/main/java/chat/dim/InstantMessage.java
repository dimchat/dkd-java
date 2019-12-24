/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2019 by Moky <albert.moky@gmail.com>
 *
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
package chat.dim;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Instant Message
 *  ~~~~~~~~~~~~~~~
 *
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- content
 *      content  : {...}
 *  }
 */
public final class InstantMessage extends Message {

    public final Content content;

    InstantMessage(Map<String, Object> dictionary) {
        super(dictionary);
        content = Content.getInstance(dictionary.get("content"));
    }

    public InstantMessage(Content body, Envelope head) {
        super(head);
        dictionary.put("content", body);
        content = body;
    }

    public InstantMessage(Content body, Object from, Object to) {
        this(body, new Envelope(from, to));
    }

    public InstantMessage(Content body, Object from, Object to, Date when) {
        this(body, new Envelope(from, to, when));
    }

    public InstantMessage(Content body, Object from, Object to, long timestamp) {
        this(body, new Envelope(from, to, timestamp));
    }

    @Override
    public InstantMessageDelegate getDelegate() {
        return (InstantMessageDelegate) super.getDelegate();
    }

    @SuppressWarnings("unchecked")
    public static InstantMessage getInstance(Object object) {
        if (object == null) {
            return null;
        }
        assert object instanceof Map;
        if (object instanceof InstantMessage) {
            // return InstantMessage object directly
            return (InstantMessage) object;
        }
        // new InstantMessage(msg)
        return new InstantMessage((Map<String, Object>) object);
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
        // 0. check attachment for File/Image/Audio/Video message content
        //    (do it in 'core' module)

        // 1. encrypt 'message.content' to 'message.data'
        Map<String, Object> map = prepareData(password);

        // 2. encrypt symmetric key(password) to 'message.key'
        // 2.1. serialize & encrypt symmetric key
        byte[] key = getDelegate().encryptKey(password, envelope.receiver, this);
        if (key != null) {
            // 2.2. encode encrypted key data
            Object base64 = getDelegate().encodeKey(key, this);
            assert base64 != null;
            // 2.3. insert as 'key'
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
     */
    public SecureMessage encrypt(Map<String, Object> password, List members) {
        // 0. check attachment for File/Image/Audio/Video message content
        //    (do it in 'core' module)

        // 1. encrypt 'message.content' to 'message.data'
        Map<String, Object> map = prepareData(password);

        // 2. encrypt symmetric key(password) to 'message.keys'
        Map<Object, Object> keys = new HashMap<>();
        byte[] key;
        Object base64;
        for (Object member: members) {
            // 2.1. serialize & encrypt symmetric key
            key = getDelegate().encryptKey(password, member, this);
            if (key != null) {
                // 2.2. encode encrypted key data
                base64 = getDelegate().encodeKey(key, this);
                assert base64 != null;
                // 2.3. insert to 'message.keys' with member ID
                keys.put(member, base64);
            }
        }
        if (keys.size() > 0) {
            map.put("keys", keys);
        }
        // group ID
        Object group = content.getGroup();
        assert group != null;
        // NOTICE: this help the receiver knows the group ID
        //         when the group message separated to multi-messages,
        //         if don't want the others know you are the group members,
        //         remove it.
        map.put("group", group);

        // 3. pack message
        return new SecureMessage(map);
    }

    private Map<String, Object> prepareData(Map<String, Object> password) {
        // encrypt message content with password
        byte[] data = getDelegate().encryptContent(content, password, this);
        assert data != null;
        // encode encrypted data
        Object base64 = getDelegate().encodeData(data, this);
        assert base64 != null;
        // replace 'content' with encrypted 'data'
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", base64);
        return map;
    }
}
