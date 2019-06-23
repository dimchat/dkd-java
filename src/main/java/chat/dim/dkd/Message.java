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
import java.util.Map;

/**
 *  Common Message
 *
 *      data format: {
 *          //-- envelope
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123,
 *          //-- others
 *          ...
 *      }
 */
public abstract class Message extends Dictionary {

    public final Envelope envelope;

    Message(Map<String, Object> dictionary) {
        super(dictionary);
        // build envelope
        Map<String, Object> env = new HashMap<>();
        env.put("sender", dictionary.get("sender"));
        env.put("receiver", dictionary.get("receiver"));
        env.put("time", dictionary.get("time"));
        envelope = new Envelope(env);
    }

    Message(Envelope head) {
        super();
        envelope = head;
        copyEnvelope(head);
    }

    Message(Object from, Object to, Date when) {
        super();
        envelope = new Envelope(from, to, when);
        copyEnvelope(envelope);
    }

    Message(Object from, Object to, long timestamp) {
        super();
        envelope = new Envelope(from, to, timestamp);
        copyEnvelope(envelope);
    }

    private void copyEnvelope(Envelope env) {
        dictionary.put("sender", env.sender);
        dictionary.put("receiver", env.receiver);
        dictionary.put("time", env.get("time"));
    }

    @SuppressWarnings("unchecked")
    public static Message getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Message) {
            // return Message object directly
            return (Message) object;
        } else if (object instanceof Map) {
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
        }
        throw new IllegalArgumentException("unknown message: " + object);
    }
}
