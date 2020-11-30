/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2020 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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

import chat.dim.protocol.Content;
import chat.dim.protocol.Envelope;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;

public final class Factories {

    public static Envelope.Factory envelopeFactory = new Envelope.Factory() {

        @Override
        public Envelope createEnvelope(ID from, ID to, Date when) {
            if (when == null) {
                when = new Date();
            }
            return new MessageEnvelope(from, to, when);
        }

        @Override
        public Envelope createEnvelope(ID from, ID to, long timestamp) {
            if (timestamp == 0) {
                timestamp = (new Date()).getTime() / 1000;
            }
            return new MessageEnvelope(from, to, timestamp);
        }

        @Override
        public Envelope parseEnvelope(Map<String, Object> env) {
            return new MessageEnvelope(env);
        }
    };

    public static Content.Factory contentFactory = new ContentFactory();

    public static InstantMessage.Factory instantMessageFactory = new InstantMessage.Factory() {

        @Override
        public InstantMessage createInstantMessage(Envelope head, Content body) {
            return new PlainMessage(head, body);
        }

        @Override
        public InstantMessage parseInstantMessage(Map<String, Object> msg) {
            return new PlainMessage(msg);
        }
    };

    public static SecureMessage.Factory secureMessageFactory = new SecureMessage.Factory() {

        @Override
        public SecureMessage parseSecureMessage(Map<String, Object> msg) {
            if (msg.containsKey("signature")) {
                return new NetworkMessage(msg);
            }
            return new EncryptedMessage(msg);
        }
    };

    public static ReliableMessage.Factory reliableMessageFactory = new ReliableMessage.Factory() {

        @Override
        public ReliableMessage parseReliableMessage(Map<String, Object> msg) {
            return new NetworkMessage(msg);
        }
    };
}
