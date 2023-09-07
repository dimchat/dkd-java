/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2022 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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

import chat.dim.protocol.Content;
import chat.dim.protocol.Envelope;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;
import chat.dim.type.Wrapper;

/**
 *  Message GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~~
 */
public class GeneralFactory {

    private final Map<Integer, Content.Factory> contentFactories = new HashMap<>();

    private Envelope.Factory envelopeFactory = null;

    private InstantMessage.Factory instantMessageFactory = null;
    private SecureMessage.Factory secureMessageFactory = null;
    private ReliableMessage.Factory reliableMessageFactory = null;

    //
    //  Content
    //

    public void setContentFactory(int type, Content.Factory factory) {
        contentFactories.put(type, factory);
    }

    public Content.Factory getContentFactory(int type) {
        return contentFactories.get(type);
    }

    public int getContentType(Map<String, Object> content) {
        Object type = content.get("type");
        return type == null ? 0 : ((Number) type).intValue();
    }

    public Content parseContent(Object content) {
        if (content == null) {
            return null;
        } else if (content instanceof Content) {
            return (Content) content;
        }
        Map<String, Object> info = Wrapper.getMap(content);
        assert info != null : "content error: " + content;
        // get factory by content type
        int type = getContentType(info);
        Content.Factory factory = getContentFactory(type);
        if (factory == null) {
            factory = getContentFactory(0);  // unknown
            assert factory != null : "cannot parse content: " + content;
        }
        return factory.parseContent(info);
    }

    //
    //  Envelope
    //

    public void setEnvelopeFactory(Envelope.Factory factory) {
        envelopeFactory = factory;
    }

    public Envelope.Factory getEnvelopeFactory() {
        return envelopeFactory;
    }

    public Envelope createEnvelope(ID from, ID to, Date when) {
        Envelope.Factory factory = getEnvelopeFactory();
        assert factory != null : "envelope factory not ready";
        return factory.createEnvelope(from, to, when);
    }

    public Envelope parseEnvelope(Object env) {
        if (env == null) {
            return null;
        } else if (env instanceof Envelope) {
            return (Envelope) env;
        }
        Map<String, Object> info = Wrapper.getMap(env);
        assert info != null : "envelope error: " + env;
        Envelope.Factory factory = getEnvelopeFactory();
        assert factory != null : "envelope factory not ready";
        return factory.parseEnvelope(info);
    }

    //
    //  InstantMessage
    //

    public void setInstantMessageFactory(InstantMessage.Factory factory) {
        instantMessageFactory = factory;
    }

    public InstantMessage.Factory getInstantMessageFactory() {
        return instantMessageFactory;
    }

    public InstantMessage createInstantMessage(Envelope head, Content body) {
        InstantMessage.Factory factory = getInstantMessageFactory();
        assert factory != null : "instant message factory not ready";
        return factory.createInstantMessage(head, body);
    }

    public InstantMessage parseInstantMessage(Object msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof InstantMessage) {
            return (InstantMessage) msg;
        }
        Map<String, Object> info = Wrapper.getMap(msg);
        assert info != null : "instant message error: " + msg;
        InstantMessage.Factory factory = getInstantMessageFactory();
        assert factory != null : "instant message factory not ready";
        return factory.parseInstantMessage(info);
    }

    public long generateSerialNumber(int msgType, Date now) {
        InstantMessage.Factory factory = getInstantMessageFactory();
        assert factory != null : "instant message factory not ready";
        return factory.generateSerialNumber(msgType, now);
    }

    //
    //  SecureMessage
    //

    public void setSecureMessageFactory(SecureMessage.Factory factory) {
        secureMessageFactory = factory;
    }

    public SecureMessage.Factory getSecureMessageFactory() {
        return secureMessageFactory;
    }

    public SecureMessage parseSecureMessage(Object msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof SecureMessage) {
            return (SecureMessage) msg;
        }
        Map<String, Object> info = Wrapper.getMap(msg);
        assert info != null : "secure message error: " + msg;
        SecureMessage.Factory factory = getSecureMessageFactory();
        assert factory != null : "secure message factory not ready";
        return factory.parseSecureMessage(info);
    }

    //
    //  ReliableMessage
    //

    public void setReliableMessageFactory(ReliableMessage.Factory factory) {
        reliableMessageFactory = factory;
    }

    public ReliableMessage.Factory getReliableMessageFactory() {
        return reliableMessageFactory;
    }

    public ReliableMessage parseReliableMessage(Object msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof ReliableMessage) {
            return (ReliableMessage) msg;
        }
        Map<String, Object> info = Wrapper.getMap(msg);
        assert info != null : "reliable message error: " + msg;
        ReliableMessage.Factory factory = getReliableMessageFactory();
        assert factory != null : "reliable message factory not ready";
        return factory.parseReliableMessage(info);
    }
}
