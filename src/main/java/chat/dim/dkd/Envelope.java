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
package chat.dim.dkd;

import java.util.Date;
import java.util.Map;

/**
 *  Envelope for message
 *  ~~~~~~~~~~~~~~~~~~~~
 *  This class is used to create a message envelope
 *  which contains 'sender', 'receiver' and 'time'
 *
 *  data format: {
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123
 *  }
 */
public final class Envelope extends Dictionary {

    public final Object sender;
    public final Object receiver;
    public final Date time;

    Envelope(Map<String, Object> dictionary) {
        super(dictionary);
        sender   = dictionary.get("sender");
        receiver = dictionary.get("receiver");
        Object timestamp = dictionary.get("time");
        if (timestamp == null) {
            time = null;
        } else {
            time = new Date(((Number) timestamp).longValue() * 1000);
        }
    }

    Envelope(Object from, Object to) {
        this(from, to, new Date());
    }

    Envelope(Object from, Object to, Date when) {
        super();
        sender   = from;
        receiver = to;
        time     = when;
        dictionary.put("sender", from);
        dictionary.put("receiver", to);
        if (when != null) {
            dictionary.put("time", when.getTime() / 1000);
        }
    }

    Envelope(Object from, Object to, long timestamp) {
        super();
        sender   = from;
        receiver = to;
        time     = new Date(timestamp * 1000);
        dictionary.put("sender", from);
        dictionary.put("receiver", to);
        dictionary.put("time", timestamp);
    }

    @SuppressWarnings("unchecked")
    public static Envelope getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Envelope) {
            // return Envelope object directly
            return (Envelope) object;
        } else if (object instanceof Map) {
            return new Envelope((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown envelope: " + object);
        }
    }
}
