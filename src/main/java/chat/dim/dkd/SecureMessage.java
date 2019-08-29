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
 *  ~~~~~~~~~~~~~~
 *  Instant Message encrypted by a symmetric key
 *
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- content data and key/keys
 *      data     : "...",  // base64_encode(symmetric)
 *      key      : "...",  // base64_encode(asymmetric)
 *      keys     : {
 *          "ID1": "key1", // base64_encode(asymmetric)
 *      }
 *  }
 */
public class SecureMessage extends Message {

    private byte[] data = null;
    private byte[] key = null;
    private Map<Object, Object> keys = null;

    public SecureMessageDelegate delegate = null;

    SecureMessage(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public Object getGroup() {
        return dictionary.get("group");
    }

    @Override
    public void setGroup(Object ID) {
        dictionary.put("group", ID);
    }

    public byte[] getData() {
        if (data == null) {
            Object base64 = dictionary.get("data");
            data = delegate.decodeData(base64, this);
        }
        return data;
    }

    public byte[] getKey() {
        if (key == null) {
            Object base64 = dictionary.get("key");
            if (base64 == null) {
                // check 'keys'
                Map<Object, Object> keys = getKeys();
                if (keys != null) {
                    base64 = keys.get(envelope.receiver);
                }
            }
            key = delegate.decodeKey(base64, this);
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    public Map<Object, Object> getKeys() {
        if (keys == null) {
            keys = (Map<Object, Object>) dictionary.get("keys");
        }
        return keys;
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
        Object group = getGroup();

        // 1. decrypt 'message.key' to symmetric key
        // 1.1. decode encrypted key data
        byte[] key = getKey();
        Map<String, Object> password;
        // 1.2. decrypt key data
        //      if key is empty, means it should be reused, get it from key cache
        if (group == null) {
            // personal message
            password = delegate.decryptKey(key, sender, receiver, this);
        } else {
            // group message
            password = delegate.decryptKey(key, sender, group, this);
        }

        // 2. decrypt 'message.data' to 'message.content'
        // 2.1. decode encrypted content data
        byte[] data = getData();
        // 2.2. decrypt & deserialize content data
        Content content = delegate.decryptContent(data, password, this);
        // 2.3. check attachment for File/Image/Audio/Video message content
        //      if file data not download yet,
        //          decrypt file data with password;
        //      else,
        //          save password to 'message.content.password'.
        //      (do it in 'core' module)
        if (content == null) {
            //throw new NullPointerException("failed to decrypt message data: " + this);
            return null;
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
        // 1. sign with sender's private key
        byte[] signature = delegate.signData(getData(), envelope.sender, this);
        if (signature == null) {
            throw new NullPointerException("failed to sign message: " + this);
        }
        // 2. pack message
        Map<String, Object> map = new HashMap<>(dictionary);
        map.put("signature", delegate.encodeSignature(signature, this));
        return new ReliableMessage(map);
    }

    /*
     *  Split/Trim group message
     *
     *  for each members, get key from 'keys' and replace 'receiver' to member ID
     */

    /**
     *  Split the group message to single person messages
     *
     *  @param members - group members
     *  @return secure/reliable message(s)
     */
    public List<SecureMessage> split(List members) {

        Map<String, Object> msg = new HashMap<>(dictionary);
        // check 'keys'
        Map<Object, Object> keys = getKeys();
        if (keys == null) {
            keys = new HashMap<>();
        } else {
            msg.remove("keys");
        }
        // check 'signature'
        boolean reliable = msg.containsKey("signature");

        // 1. move the receiver(group ID) to 'group'
        //    this will help the receiver knows the group ID
        //    when the group message separated to multi-messages;
        //    if don't want the others know your membership,
        //    DON'T do this.
        msg.put("group", envelope.receiver);

        List<SecureMessage> messages = new ArrayList<>(members.size());
        Object base64;
        for (Object member : members) {
            // 2. change 'receiver' to each group member
            msg.put("receiver", member);
            // 3. get encrypted key
            base64 = keys.get(member);
            if (base64 == null) {
                msg.remove("key");
            } else {
                msg.put("key", base64);
            }
            // 4. repack message
            if (reliable) {
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
        // check 'keys'
        Map<Object, Object> keys = getKeys();
        if (keys != null) {
            // move key data from 'keys' to 'key'
            Object base64 = keys.get(member);
            if (base64 != null) {
                msg.put("key", base64);
            }
            msg.remove("keys");
        }
        // check 'group'
        Object group = getGroup();
        if (group == null) {
            // if 'group' not exists, the 'receiver' must be a group ID here, and
            // it will not be equal to the member of course,
            // so move 'receiver' to 'group'
            msg.put("group", envelope.receiver);
        }
        msg.put("receiver", member);
        // repack
        if (msg.containsKey("signature")) {
            return new ReliableMessage(msg);
        } else {
            return new SecureMessage(msg);
        }
    }
}
