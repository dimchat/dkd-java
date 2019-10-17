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

import chat.dim.protocol.ContentType;
import chat.dim.protocol.ForwardContent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
public class Content extends Dictionary {

    // message type: text, image, ...
    public final int type;

    // serial number: random number to identify message content
    public final long serialNumber;

    protected Content(Map<String, Object> dictionary) {
        super(dictionary);
        type         = (int) dictionary.get("type");
        serialNumber = ((Number) dictionary.get("sn")).longValue();
    }

    protected Content(int msgType) {
        super();
        type         = msgType;
        serialNumber = randomPositiveInteger();
        dictionary.put("type", type);
        dictionary.put("sn", serialNumber);
    }

    private static long randomPositiveInteger() {
        Random random = new Random();
        long sn = random.nextLong();
        if (sn > 0) {
            return sn;
        } else if (sn < 0) {
            return -sn;
        }
        // ZERO? do it again!
        return 9527 + 9394; // randomPositiveInteger();
    }

    // Group ID/string for group message
    //    if field 'group' exists, it means this is a group message
    public Object getGroup() {
        return dictionary.get("group");
    }
    public void setGroup(Object identifier) {
        if (identifier == null) {
            dictionary.remove("group");
        } else {
            dictionary.put("group", identifier);
        }
    }

    //-------- Runtime --------

    private static Map<Integer, Class> contentClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(Integer type, Class clazz) {
        // check whether clazz is subclass of Content
        if (clazz.equals(Content.class)) {
            throw new IllegalArgumentException("should not add Content.class itself!");
        }
        clazz = clazz.asSubclass(Content.class);
        contentClasses.put(type, clazz);
    }

    @SuppressWarnings("unchecked")
    private static Content createInstance(Map<String, Object> dictionary) {
        // get subclass by content type
        int type = (int) dictionary.get("type");
        Class clazz = contentClasses.get(type);
        if (clazz == null) {
            //throw new ClassNotFoundException("unknown content type: " + type);
            return new Content(dictionary);
        }
        // try 'getInstance()' of subclass
        try {
            Method method = clazz.getMethod("getInstance", Object.class);
            if (method.getDeclaringClass().equals(clazz)) {
                return (Content) method.invoke(null, dictionary);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //e.printStackTrace();
        }
        // try 'new MyContent(dict)'
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (Content) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Content getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Content) {
            return (Content) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("content error: " + object);
        }
    }

    static {
        // Forward content for Top-Secret message
        Content.register(ContentType.FORWARD.value, ForwardContent.class);
        // ...
    }
}
