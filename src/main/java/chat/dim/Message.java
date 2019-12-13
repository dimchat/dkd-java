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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/*
 *  Message Transforming
 *  ~~~~~~~~~~~~~~~~~~~~
 *
 *     Instant Message <-> Secure Message <-> Reliable Message
 *     +-------------+     +------------+     +--------------+
 *     |  sender     |     |  sender    |     |  sender      |
 *     |  receiver   |     |  receiver  |     |  receiver    |
 *     |  time       |     |  time      |     |  time        |
 *     |             |     |            |     |              |
 *     |  content    |     |  data      |     |  data        |
 *     +-------------+     |  key/keys  |     |  key/keys    |
 *                         +------------+     |  signature   |
 *                                            +--------------+
 *     Algorithm:
 *         data      = password.encrypt(content)
 *         key       = receiver.public_key.encrypt(password)
 *         signature = sender.private_key.sign(data)
 */

/**
 *  Message with Envelope
 *  ~~~~~~~~~~~~~~~~~~~~~
 *  Base classes for messages
 *  This class is used to create a message
 *  with the envelope fields, such as 'sender', 'receiver', and 'time'
 *
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- body
 *      ...
 *  }
 */
public abstract class Message extends Dictionary {

    public final Envelope envelope;

    private WeakReference<MessageDelegate> delegateRef = null;

    Message(Map<String, Object> dictionary) {
        super(dictionary);
        // build envelope
        Map<String, Object> env = new HashMap<>();
        env.put("sender", dictionary.get("sender"));
        env.put("receiver", dictionary.get("receiver"));
        env.put("time", dictionary.get("time"));
        envelope = new Envelope(env);
    }

    Message(Envelope env) {
        super();
        // copy envelope
        dictionary.put("sender", env.sender);
        dictionary.put("receiver", env.receiver);
        dictionary.put("time", env.get("time")); // copy timestamp
        envelope = env;
    }

    public MessageDelegate getDelegate() {
        if (delegateRef == null) {
            return null;
        }
        return delegateRef.get();
    }

    public void setDelegate(MessageDelegate delegate) {
        delegateRef = new WeakReference<>(delegate);
    }

    /**
     *  Group ID
     *  ~~~~~~~~
     *  when a group message was split/trimmed to a single message
     *  the 'receiver' will be changed to a member ID, and
     *  the group ID will be saved as 'group'.
     *
     * @return group ID/string
     */
    public abstract Object getGroup();

    public abstract void setGroup(Object group);

    @SuppressWarnings("unchecked")
    public static Message getInstance(Object object) {
        if (object == null) {
            return null;
        }
        assert object instanceof Map;
        if (object instanceof Message) {
            // return Message object directly
            return (Message) object;
        }
        Map<String, Object> dictionary = (Map<String, Object>) object;
        // instant message
        Object content = dictionary.get("content");
        if (content != null) {
            return new InstantMessage(dictionary);
        }
        // reliable message
        String signature = (String) dictionary.get("signature");
        if (signature != null) {
            return new ReliableMessage(dictionary);
        }
        // secure message
        String data = (String) dictionary.get("data");
        if (data != null) {
            return new SecureMessage(dictionary);
        }
        throw new IllegalArgumentException("unknown message: " + object);
    }
}
