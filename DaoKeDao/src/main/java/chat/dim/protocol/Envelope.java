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
package chat.dim.protocol;

import java.util.Date;
import java.util.Map;

import chat.dim.dkd.Factories;

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
public interface Envelope extends chat.dim.type.Map {

    // message from
    ID getSender();

    static ID getSender(Map<String, Object> env) {
        return ID.parse(env.get("sender"));
    }

    // message to
    ID getReceiver();

    static ID getReceiver(Map<String, Object> env) {
        return ID.parse(env.get("receiver"));
    }

    // message time
    Date getTime();

    static Date getTime(Map<String, Object> env) {
        Object timestamp = env.get("time");
        if (timestamp == null) {
            return null;
        }
        return new Date(((Number) timestamp).longValue() * 1000);
    }

    /*
     *  Group ID
     *  ~~~~~~~~
     *  when a group message was split/trimmed to a single message
     *  the 'receiver' will be changed to a member ID, and
     *  the group ID will be saved as 'group'.
     */
    ID getGroup();
    void setGroup(ID group);

    static ID getGroup(Map<String, Object> env) {
        return ID.parse(env.get("group"));
    }
    static void setGroup(ID group, Map<String, Object> env) {
        if (group == null) {
            env.remove("group");
        } else {
            env.put("group", group.toString());
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
    int getType();
    void setType(int type);

    static int getType(Map<String, Object> env) {
        Object type = env.get("type");
        if (type == null) {
            return 0;
        } else {
            return (int) type;
        }
    }
    static void setType(int type, Map<String, Object> env) {
        env.put("type", type);
    }

    //
    //  Factory methods
    //
    static Envelope create(ID from, ID to, Date when) {
        Factory factory = getFactory();
        assert factory != null : "envelope factory not ready";
        return factory.createEnvelope(from, to, when);
    }
    static Envelope create(ID from, ID to, long timestamp) {
        Factory factory = getFactory();
        assert factory != null : "envelope factory not ready";
        return factory.createEnvelope(from, to, timestamp);
    }
    static Envelope parse(Map<String, Object> env) {
        if (env == null) {
            return null;
        } else if (env instanceof Envelope) {
            return (Envelope) env;
        } else if (env instanceof chat.dim.type.Map) {
            env = ((chat.dim.type.Map) env).getMap();
        }
        Factory factory = getFactory();
        assert factory != null : "envelope factory not ready";
        return factory.parseEnvelope(env);
    }

    static Factory getFactory() {
        return Factories.envelopeFactory;
    }
    static void setFactory(Factory factory) {
        Factories.envelopeFactory = factory;
    }

    /**
     *  Envelope Factory
     *  ~~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create envelope
         *
         * @param from - sender ID
         * @param to   - receiver ID
         * @param when - message time
         * @return Envelope
         */
        Envelope createEnvelope(ID from, ID to, Date when);
        Envelope createEnvelope(ID from, ID to, long timestamp);

        /**
         *  Parse map object to envelope
         *
         * @param env - envelope info
         * @return Envelope
         */
        Envelope parseEnvelope(Map<String, Object> env);
    }
}
