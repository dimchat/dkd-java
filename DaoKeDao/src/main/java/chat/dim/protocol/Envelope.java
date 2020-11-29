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
public interface Envelope extends Map<String, Object> {

    Map<String, Object> getMap();
    Map<String, Object> copyMap();

    // message from
    ID getSender();

    // message to
    ID getReceiver();

    // message time
    Date getTime();

    /*
     *  Group ID
     *  ~~~~~~~~
     *  when a group message was split/trimmed to a single message
     *  the 'receiver' will be changed to a member ID, and
     *  the group ID will be saved as 'group'.
     */
    ID getGroup();
    void setGroup(ID group);

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

    //
    //  Factory methods
    //
    static Envelope create(ID from, ID to, Date when) {
        return Factories.envelopeFactory.createEnvelope(from, to, when);
    }
    static Envelope create(ID from, ID to, long timestamp) {
        return Factories.envelopeFactory.createEnvelope(from, to, timestamp);
    }
    static Envelope parse(Map<String, Object> env) {
        if (env == null) {
            return null;
        } else if (env instanceof Envelope) {
            return (Envelope) env;
        }
        return Factories.envelopeFactory.parseEnvelope(env);
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
