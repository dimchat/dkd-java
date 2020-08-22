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

import java.util.Arrays;
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
public class InstantMessage<ID, KEY> extends Message<ID> {

    private Content<ID> content;

    InstantMessage(Map<String, Object> dictionary) {
        super(dictionary);
        // lazy
        content = null;
    }

    public InstantMessage(Content<ID> body, Envelope<ID> head) {
        super(head);
        put("content", body);
        content = body;
    }

    public InstantMessage(Content<ID> body, ID from, ID to) {
        this(body, new Envelope<>(from, to));
    }

    public InstantMessage(Content<ID> body, ID from, ID to, Date when) {
        this(body, new Envelope<>(from, to, when));
    }

    public InstantMessage(Content<ID> body, ID from, ID to, long timestamp) {
        this(body, new Envelope<>(from, to, timestamp));
    }

    @Override
    public InstantMessageDelegate<ID, KEY> getDelegate() {
        return (InstantMessageDelegate<ID, KEY>) super.getDelegate();
    }

    @Override
    public void setDelegate(MessageDelegate<ID> delegate) {
        super.setDelegate(delegate);
        // set delegate for message content
        getContent().setDelegate(delegate);
    }

    @SuppressWarnings("unchecked")
    public Content<ID> getContent() {
        if (content == null) {
            Object info = get("content");
            if (info instanceof Map) {
                content = getDelegate().getContent((Map<String, Object>) info);
            }
        }
        return content;
    }

    public static InstantMessage getInstance(Map<String, Object> dictionary) {
        if (dictionary == null) {
            return null;
        }
        if (dictionary instanceof InstantMessage) {
            // return InstantMessage object directly
            return (InstantMessage) dictionary;
        }
        return new InstantMessage<>(dictionary);
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
    public SecureMessage<ID, KEY> encrypt(KEY password) {
        // 0. check attachment for File/Image/Audio/Video message content
        //    (do it in 'core' module)

        // 1. encrypt 'message.content' to 'message.data'
        Map<String, Object> map = prepareData(password);

        // 2. encrypt symmetric key(password) to 'message.key'
        InstantMessageDelegate<ID, KEY> delegate = getDelegate();
        // 2.1. serialize symmetric key
        byte[] key = delegate.serializeKey(password, this);
        if (key == null) {
            // A) broadcast message has no key
            // B) reused key
            return new SecureMessage<>(map);
        }

        // 2.2. encrypt symmetric key data
        key = delegate.encryptKey(key, envelope.getReceiver(), this);
        if (key == null) {
            // public key for encryption not found
            // TODO: suspend this message for waiting receiver's meta
            return null;
        }
        // 2.3. encode encrypted key data
        Object base64 = delegate.encodeKey(key, this);
        assert base64 != null : "failed to encode key data: " + Arrays.toString(key);
        // 2.4. insert as 'key'
        map.put("key", base64);

        // 3. pack message
        return new SecureMessage<>(map);
    }

    /**
     *  Encrypt group message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @param members - group members
     * @return SecureMessage object
     */
    public SecureMessage<ID, KEY> encrypt(KEY password, List<ID> members) {
        // 0. check attachment for File/Image/Audio/Video message content
        //    (do it in 'core' module)

        // 1. encrypt 'message.content' to 'message.data'
        Map<String, Object> map = prepareData(password);

        // 2. serialize symmetric key
        InstantMessageDelegate<ID, KEY> delegate = getDelegate();
        // 2.1. serialize symmetric key
        byte[] key = delegate.serializeKey(password, this);
        if (key == null) {
            // A) broadcast message has no key
            // B) reused key
            return new SecureMessage<>(map);
        }

        // encrypt key data to 'message.keys'
        Map<Object, Object> keys = new HashMap<>();
        int count = 0;
        byte[] data;
        Object base64;
        for (ID member: members) {
            // 2.2. encrypt symmetric key data
            data = delegate.encryptKey(key, member, this);
            if (data == null) {
                // public key for encryption not found
                // TODO: suspend this message for waiting receiver's meta
                continue;
            }
            // 2.3. encode encrypted key data
            base64 = delegate.encodeKey(data, this);
            assert base64 != null : "failed to encode key data: " + Arrays.toString(data);
            // 2.4. insert to 'message.keys' with member ID
            keys.put(member, base64);
            ++count;
        }
        if (count > 0) {
            map.put("keys", keys);
        }

        // 3. pack message
        return new SecureMessage<>(map);
    }

    private Map<String, Object> prepareData(KEY password) {
        InstantMessageDelegate<ID, KEY> delegate = getDelegate();
        // 1. serialize message content
        byte[] data = delegate.serializeContent(getContent(), password, this);
        assert data != null : "failed to serialize content: " + content;
        // 2. encrypt content data with password
        data = delegate.encryptContent(data, password, this);
        assert data != null : "failed to encrypt content with key: " + password;
        // 3. encode encrypted data
        Object base64 = delegate.encodeData(data, this);
        assert base64 != null : "failed to encode content data: " + Arrays.toString(data);
        // 4. replace 'content' with encrypted 'data'
        Map<String, Object> map = new HashMap<>(dictionary);
        map.remove("content");
        map.put("data", base64);
        return map;
    }
}
