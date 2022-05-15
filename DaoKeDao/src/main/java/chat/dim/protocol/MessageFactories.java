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
package chat.dim.protocol;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import chat.dim.dkd.EncryptedMessage;
import chat.dim.dkd.MessageEnvelope;
import chat.dim.dkd.NetworkMessage;
import chat.dim.dkd.PlainMessage;

final class MessageFactories {

    static Envelope.Factory envelopeFactory = null;

    static InstantMessage.Factory instantMessageFactory = null;
    static SecureMessage.Factory secureMessageFactory = null;
    static ReliableMessage.Factory reliableMessageFactory = null;

    static {
        registerFactories();
    }

    /**
     *  Register core factories
     */
    private static void registerFactories() {
        // Envelope factory
        Envelope.setFactory(new Envelope.Factory() {

            @Override
            public Envelope createEnvelope(ID from, ID to, Date when) {
                if (when == null) {
                    when = new Date();
                }
                return new MessageEnvelope(from, to, when);
            }

            @Override
            public Envelope parseEnvelope(Map<String, Object> env) {
                if (env.get("sender") == null) {
                    // env.sender should not empty
                    return null;
                }
                return new MessageEnvelope(env);
            }
        });

        // Instant message factory
        InstantMessage.setFactory(new InstantMessage.Factory() {

            @Override
            public long generateSerialNumber(int msgType, Date time) {
                // because we must make sure all messages in a same chat box won't have
                // same serial numbers, so we can't use time-related numbers, therefore
                // the best choice is a totally random number, maybe.
                Random random = new Random();
                int sn = random.nextInt();
                if (sn > 0) {
                    return sn;
                } else if (sn < 0) {
                    return -sn;
                }
                // ZERO? do it again!
                return 9527 + 9394; // generateSerialNumber(msgType, time);
            }

            @Override
            public InstantMessage createInstantMessage(Envelope head, Content body) {
                return new PlainMessage(head, body);
            }

            @Override
            public InstantMessage parseInstantMessage(Map<String, Object> msg) {
                if (msg.get("content") == null) {
                    // msg.content should not empty
                    return null;
                }
                return new PlainMessage(msg);
            }
        });

        // Secure message factory
        SecureMessage.setFactory(new SecureMessage.Factory() {

            @Override
            public SecureMessage parseSecureMessage(Map<String, Object> msg) {
                if (msg.containsKey("signature")) {
                    return new NetworkMessage(msg);
                }
                return new EncryptedMessage(msg);
            }
        });

        // Reliable message factory
        ReliableMessage.setFactory(new ReliableMessage.Factory() {

            @Override
            public ReliableMessage parseReliableMessage(Map<String, Object> msg) {
                if (msg.get("sender") == null || msg.get("data") == null || msg.get("signature") == null) {
                    // msg.sender should not empty
                    // msg.data should not empty
                    // msg.signature should not empty
                    return null;
                }
                return new NetworkMessage(msg);
            }
        });
    }
}
