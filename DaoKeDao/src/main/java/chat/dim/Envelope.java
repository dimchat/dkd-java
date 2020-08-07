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
import java.util.Map;

import chat.dim.protocol.ContentType;
import chat.dim.type.Dictionary;

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
public final class Envelope<ID> extends Dictionary<String, Object> {

    public final ID sender;
    public final ID receiver;
    public final Date time;

    Envelope(Map<String, Object> dictionary) {
        super(dictionary);
        //noinspection unchecked
        sender   = (ID) parser.getID(dictionary.get("sender"));
        //noinspection unchecked
        receiver = (ID) parser.getID(dictionary.get("receiver"));
        Object timestamp = dictionary.get("time");
        if (timestamp == null) {
            time = null;
        } else {
            time = new Date(((Number) timestamp).longValue() * 1000);
        }
    }

    Envelope(ID from, ID to) {
        this(from, to, new Date());
    }

    Envelope(ID from, ID to, Date when) {
        super();
        sender   = from;
        receiver = to;
        time     = when;
        put("sender", from);
        put("receiver", to);
        if (when != null) {
            put("time", when.getTime() / 1000);
        }
    }

    Envelope(ID from, ID to, long timestamp) {
        super();
        sender   = from;
        receiver = to;
        time     = new Date(timestamp * 1000);
        put("sender", from);
        put("receiver", to);
        put("time", timestamp);
    }

    public static Envelope getInstance(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Envelope) {
            // return Envelope object directly
            return (Envelope) object;
        }
        //noinspection unchecked
        return new Envelope((Map<String, Object>) object);
    }

    /*
     *  Group ID
     *  ~~~~~~~~
     *  when a group message was split/trimmed to a single message
     *  the 'receiver' will be changed to a member ID, and
     *  the group ID will be saved as 'group'.
     */
    public ID getGroup() {
        //noinspection unchecked
        return (ID) parser.getID(get("group"));
    }

    public void setGroup(ID group) {
        if (group == null) {
            remove("group");
        } else {
            put("group", group);
        }
    }

    /*
     *  Message Type
     *  ~~~~~~~~~~~~
     *  because the message content will be encrypted, so
     *  the intermediate nodes(station) cannot recognize what kind of it.
     *  we pick out the content type and set it in envelope
     *  to let the station do its job.
     */
    public int getType() {
        Object type = get("type");
        if (type == null) {
            return 0;
        } else {
            return (int) type;
        }
    }

    public void setType(ContentType type) {
        setType(type.value);
    }
    public void setType(int type) {
        put("type", type);
    }

    Map<String, Object> getDictionary() {
        return dictionary;
    }

    //
    //  ID parser
    //

    public interface Parser<ID> {

        ID getID(Object identifier);
    }

    public static Parser parser = new Parser() {

        @Override
        public Object getID(Object identifier) {
            // TODO: convert String to ID
            return identifier;
        }
    };
}
