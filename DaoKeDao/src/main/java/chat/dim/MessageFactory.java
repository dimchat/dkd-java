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
package chat.dim;

import java.util.Date;
import java.util.Map;

import chat.dim.dkd.BaseContent;
import chat.dim.dkd.EncryptedMessage;
import chat.dim.dkd.MessageEnvelope;
import chat.dim.dkd.NetworkMessage;
import chat.dim.dkd.PlainMessage;
import chat.dim.protocol.Content;
import chat.dim.protocol.Envelope;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;

public class MessageFactory {

    public static Envelope getEnvelope(ID from, ID to) {
        return new MessageEnvelope(from, to, new Date());
    }

    public static Envelope getEnvelope(ID from, ID to, Date when) {
        return new MessageEnvelope(from, to, when);
    }

    public static Envelope getEnvelope(ID from, ID to, long timestamp) {
        return new MessageEnvelope(from, to, timestamp);
    }

    public static Envelope getEnvelope(Map<String, Object> envelope) {
        if (envelope == null) {
            return null;
        } else if (envelope instanceof Envelope) {
            return (Envelope) envelope;
        }
        return messageParser.parseEnvelope(envelope);
    }

    public static InstantMessage getInstantMessage(Envelope head, Content body) {
        return new PlainMessage(head, body);
    }

    public static InstantMessage getInstantMessage(Map<String, Object> msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof InstantMessage) {
            return (InstantMessage) msg;
        }
        return messageParser.parseInstantMessage(msg);
    }

    public static SecureMessage getSecureMessage(Map<String, Object> msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof SecureMessage) {
            return (SecureMessage) msg;
        }
        return messageParser.parseSecureMessage(msg);
    }

    public static ReliableMessage getReliableMessage(Map<String, Object> msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof ReliableMessage) {
            return (ReliableMessage) msg;
        }
        return messageParser.parseReliableMessage(msg);
    }

    public static Content getContent(Map<String, Object> content) {
        if (content == null) {
            return null;
        } else if (content instanceof Content) {
            return (Content) content;
        }
        return contentParser.parseContent(content);
    }

    /**
     *  Message Parser
     *  ~~~~~~~~~~~~~~
     */
    public interface MessageParser extends Envelope.Parser, InstantMessage.Parser, SecureMessage.Parser, ReliableMessage.Parser {
    }

    // default message parser
    public static MessageParser messageParser = new MessageParser() {

        @Override
        public Envelope parseEnvelope(Map<String, Object> envelope) {
            return new MessageEnvelope(envelope);
        }

        @Override
        public InstantMessage parseInstantMessage(Map<String, Object> msg) {
            return new PlainMessage(msg);
        }

        @Override
        public SecureMessage parseSecureMessage(Map<String, Object> msg) {
            if (msg.containsKey("signature")) {
                return new NetworkMessage(msg);
            }
            return new EncryptedMessage(msg);
        }

        @Override
        public ReliableMessage parseReliableMessage(Map<String, Object> msg) {
            return new NetworkMessage(msg);
        }
    };

    /**
     *  Content Parser
     *  ~~~~~~~~~~~~~~
     */

    // default content parser
    public static Content.Parser contentParser = new Content.Parser() {
        @Override
        public Content parseContent(Map<String, Object> content) {
            return new BaseContent(content);
        }
    };
}
