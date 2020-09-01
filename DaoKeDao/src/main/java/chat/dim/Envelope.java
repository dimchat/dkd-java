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

    private ID sender;
    private ID receiver;
    private Date time;

    private ID group = null;

    private WeakReference<MessageDelegate<ID>> delegateRef = null;

    Envelope(Map<String, Object> dictionary) {
        super(dictionary);
        // lazy load
        sender   = null;
        receiver = null;
        time     = null;
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

    public MessageDelegate<ID> getDelegate() {
        if (delegateRef == null) {
            return null;
        }
        return delegateRef.get();
    }

    public void setDelegate(MessageDelegate<ID> delegate) {
        delegateRef = new WeakReference<>(delegate);
    }

    public ID getSender() {
        if (sender == null) {
            MessageDelegate<ID> delegate = getDelegate();
            assert delegate != null : "message delegate not set";
            sender = delegate.getID(get("sender"));
        }
        return sender;
    }

    public ID getReceiver() {
        if (receiver == null) {
            MessageDelegate<ID> delegate = getDelegate();
            assert delegate != null : "message delegate not set";
            receiver = delegate.getID(get("receiver"));
        }
        return receiver;
    }

    public Date getTime() {
        if (time == null) {
            Object timestamp = dictionary.get("time");
            if (timestamp != null) {
                time = new Date(((Number) timestamp).longValue() * 1000);
            }
        }
        return time;
    }

    public static Envelope getInstance(Map<String, Object> dictionary) {
        if (dictionary == null) {
            return null;
        }
        if (dictionary instanceof Envelope) {
            // return Envelope object directly
            return (Envelope) dictionary;
        }
        return new Envelope<>(dictionary);
    }

    /*
     *  Group ID
     *  ~~~~~~~~
     *  when a group message was split/trimmed to a single message
     *  the 'receiver' will be changed to a member ID, and
     *  the group ID will be saved as 'group'.
     */
    public ID getGroup() {
        if (group == null) {
            MessageDelegate<ID> delegate = getDelegate();
            assert delegate != null : "message delegate not set";
            group = delegate.getID(get("group"));
        }
        return group;
    }

    public void setGroup(ID group) {
        if (group == null) {
            remove("group");
        } else {
            put("group", group);
        }
        this.group = group;
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
}
