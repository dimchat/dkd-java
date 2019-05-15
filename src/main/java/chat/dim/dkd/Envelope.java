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
import java.util.Map;

/**
 *  Envelope for message
 *
 *      data format: {
 *          sender   : "moki@xxx",
 *          receiver : "hulk@yyy",
 *          time     : 123
 *      }
 */
public final class Envelope extends Dictionary {

    public final Object sender;
    public final Object receiver;
    public final Date time;

    public Envelope(Map<String, Object> dictionary) {
        super(dictionary);
        sender   = dictionary.get("sender");
        receiver = dictionary.get("receiver");
        time     = getTime(dictionary);
    }

    public Envelope(Object from, Object to, Date when) {
        super();
        sender   = from;
        receiver = to;
        time     = when;
        dictionary.put("sender", from);
        dictionary.put("receiver", to);
        dictionary.put("time", getTimestamp(when));
    }

    public Envelope(Object from, Object to) {
        this(from, to, new Date());
    }

    private static Date getTime(Map<String, Object> map) {
        long timestamp = 0;
        Object time = map.get("time");
        if (time == null) {
            // timestamp = 0;
            return new Date();
        } else {
            timestamp = (long) map.get("time");
        }
        return new Date(timestamp * 1000);
    }

    private static long getTimestamp(Date time) {
        return time.getTime() / 1000;
    }

    @SuppressWarnings("unchecked")
    public static Envelope getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Envelope) {
            return (Envelope) object;
        } else if (object instanceof Map) {
            return new Envelope((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }
}
