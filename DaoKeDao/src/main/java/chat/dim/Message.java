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

import java.util.Map;

import chat.dim.type.Dictionary;

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
public class Message<ID> extends Dictionary<String, Object> {

    public final Envelope<ID> envelope;

    Message(Map<String, Object> dictionary) {
        super(dictionary);
        envelope = new Envelope<>(dictionary);
    }

    Message(Envelope<ID> env) {
        super(env.getDictionary());
        envelope = env;
    }

    public MessageDelegate<ID> getDelegate() {
        return envelope.getDelegate();
    }

    public void setDelegate(MessageDelegate<ID> delegate) {
        envelope.setDelegate(delegate);
    }

    public static Message getInstance(Object object) {
        if (object == null) {
            return null;
        }
        //noinspection unchecked
        Map<String, Object> dictionary = (Map<String, Object>) object;
        if (dictionary.containsKey("content")) {
            // this should be an instant message
            return InstantMessage.getInstance(dictionary);
        }
        if (dictionary.containsKey("signature")) {
            // this should be a reliable message
            return ReliableMessage.getInstance(dictionary);
        }
        if (dictionary.containsKey("data")) {
            // this should be a secure message
            return SecureMessage.getInstance(dictionary);
        }
        if (object instanceof Message) {
            // return Message object directly
            return (Message) object;
        }
        throw new IllegalArgumentException("unknown message: " + object);
    }
}
