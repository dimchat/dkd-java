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
 *  Message Content
 *  ~~~~~~~~~~~~~~~
 *  This class is for creating message content
 *
 *  data format: {
 *      'type'    : 0x00,            // message type
 *      'sn'      : 0,               // serial number
 *
 *      'group'   : 'Group ID',      // for group message
 *
 *      //-- message info
 *      'text'    : 'text',          // for text message
 *      'command' : 'Command Name',  // for system command
 *      //...
 *  }
 */
public interface Content extends chat.dim.type.Map {

    // content type
    int getType();

    static int getType(Map<String, Object> content) {
        Object version = content.get("type");
        if (version == null) {
            throw new NullPointerException("content type not found: " + content);
        }
        return ((Number) version).intValue();
    }

    // serial number as message id
    long getSerialNumber();

    static long getSerialNumber(Map<String, Object> content) {
        Object sn = content.get("sn");
        if (sn == null) {
            throw new NullPointerException("serial number not found: " + content);
        }
        return ((Number) sn).longValue();
    }

    // message time
    Date getTime();

    static Date getTime(Map<String, Object> content) {
        Object timestamp = content.get("time");
        if (timestamp == null) {
            return null;
        }
        return new Date(((Number) timestamp).longValue() * 1000);
    }

    // Group ID/string for group message
    //    if field 'group' exists, it means this is a group message
    ID getGroup();
    void setGroup(ID group);

    static ID getGroup(Map<String, Object> content) {
        return ID.parse(content.get("group"));
    }
    static void setGroup(ID group, Map<String, Object> content) {
        if (group == null) {
            content.remove("group");
        } else {
            content.put("group", group.toString());
        }
    }

    //
    //  Factory method
    //
    static Content parse(Map<String, Object> content) {
        if (content == null) {
            return null;
        } else if (content instanceof Content) {
            return (Content) content;
        } else if (content instanceof chat.dim.type.Map) {
            content = ((chat.dim.type.Map) content).getMap();
        }
        // get factory by content type
        int type = getType(content);
        Factory factory = getFactory(type);
        if (factory == null) {
            factory = getFactory(0);  // unknown
            assert factory != null : "cannot parse content: " + content;
        }
        return factory.parseContent(content);
    }

    static Factory getFactory(int type) {
        return Factories.contentFactories.get(type);
    }
    static Factory getFactory(ContentType type) {
        return Factories.contentFactories.get(type.value);
    }
    static void register(int type, Factory factory) {
        Factories.contentFactories.put(type, factory);
    }
    static void register(ContentType type, Factory factory) {
        Factories.contentFactories.put(type.value, factory);
    }

    /**
     *  Content Factory
     *  ~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Parse map object to content
         *
         * @param content - content info
         * @return Content
         */
        Content parseContent(Map<String, Object> content);
    }
}
