/*
 * Fast Infoset ver. 0.1 software ("Software")
 *
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 *
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 *
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 *
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */
package org.jvnet.fastinfoset.sax.helpers;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import java.io.IOException;
import java.util.Map;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Attributes;

/**
 * Default implementation of the {@link EncodingAlgorithmAttributes} interface.
 *
 * <p>This class provides a default implementation of the SAX2
 * {@link EncodingAlgorithmAttributes} interface, with the
 * addition of manipulators so that the list can be modified or
 * reused.</p>
 *
 * <p>There are two typical uses of this class:</p>
 *
 * <ol>
 * <li>to take a persistent snapshot of an EncodingAlgorithmAttributes object
 *  in a {@link org.xml.sax.ContentHandler#startElement startElement} event; or</li>
 * <li>to construct or modify an EncodingAlgorithmAttributes object in a SAX2
 * driver or filter.</li>
 * </ol>
 */
public class EncodingAlgorithmAttributesImpl implements EncodingAlgorithmAttributes {
    private static final int DEFAULT_CAPACITY       = 8;
    
    private static final int URI_OFFSET             = 0;
    private static final int LOCALNAME_OFFSET       = 1;
    private static final int QNAME_OFFSET           = 2;
    private static final int TYPE_OFFSET            = 3;
    private static final int VALUE_OFFSET           = 4;
    private static final int ALGORITHMURI_OFFSET    = 5;
    
    private static final int SIZE                   = 6;
    
    private Map _registeredEncodingAlgorithms;
    
    private int _length;
    
    private String[] _data;
    
    private int[] _algorithmIds;
    
    private Object[] _algorithmData;
    
    /**
     * Construct a new, empty EncodingAlgorithmAttributesImpl object.
     */
    public EncodingAlgorithmAttributesImpl() {
        this(null, null);
    }
    
    /**
     * Copy an existing Attributes object.
     *
     * <p>This constructor is especially useful inside a
     * {@link org.xml.sax.ContentHandler#startElement startElement} event.</p>
     *
     * @param attributes The existing Attributes object.
     */
    public EncodingAlgorithmAttributesImpl(Attributes attributes) {
        this(null, attributes);
    }
    
    /**
     * Use registered encoding algorithms and copy an existing Attributes object.
     *
     * <p>This constructor is especially useful inside a
     * {@link org.xml.sax.ContentHandler#startElement startElement} event.</p>
     *
     * @param registeredEncodingAlgorithms
     *      The registeredEncodingAlgorithms encoding algorithms.
     * @param attributes The existing Attributes object.
     */
    public EncodingAlgorithmAttributesImpl(Map registeredEncodingAlgorithms,
            Attributes attributes) {
        _data = new String[DEFAULT_CAPACITY * SIZE];
        _algorithmIds = new int[DEFAULT_CAPACITY];
        _algorithmData = new Object[DEFAULT_CAPACITY];
        
        _registeredEncodingAlgorithms = registeredEncodingAlgorithms;
        
        if (attributes != null) {
            if (attributes instanceof EncodingAlgorithmAttributes) {
                setAttributes((EncodingAlgorithmAttributes)attributes);
            } else {
                setAttributes(attributes);
            }
        }
    }
    
    /**
     * Clear the attribute list for reuse.
     *
     */
    public final void clear() {
        for (int i = 0; i < _length; i++) {
            _data[i * SIZE + VALUE_OFFSET] = null;
            _algorithmData[i] = null;
        }
        _length = 0;
    }
    
    /**
     * Add an attribute to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified (prefixed) name, or the empty string
     *        if qualified names are not available.
     * @param type The attribute type as a string.
     * @param value The attribute value.
     */
    public void addAttribute(String URI, String localName, String qName,
            String type, String value) {
        if (_length >= _algorithmData.length) {
            resize();
        }
        
        int i = _length * SIZE;
        _data[i++] = replaceNull(URI);
        _data[i++] = replaceNull(localName);
        _data[i++] = replaceNull(qName);
        _data[i++] = replaceNull(type);
        _data[i++] = replaceNull(value);
        
        _length++;
    }
    
    /**
     * Add an attribute with built in algorithm data to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified (prefixed) name, or the empty string
     *        if qualified names are not available.
     * @param builtInAlgorithmID The built in algorithm ID.
     * @param algorithmData The built in algorithm data.
     */
    public void addAttributeWithBuiltInAlgorithmData(String URI, String localName, String qName,
            int builtInAlgorithmID, Object algorithmData) {
        if (_length >= _algorithmData.length) {
            resize();
        }
        
        int i = _length * SIZE;
        _data[i++] = replaceNull(URI);
        _data[i++] = replaceNull(localName);
        _data[i++] = replaceNull(qName);
        _data[i++] = "CDATA";
        _data[i++] = "";
        _data[i++] = null;
        _algorithmIds[_length] = builtInAlgorithmID;
        _algorithmData[_length] = algorithmData;
        
        _length++;
    }
    
    /**
     * Add an attribute with algorithm data to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified (prefixed) name, or the empty string
     *        if qualified names are not available.
     * @param algorithmURI The algorithm URI, or null if a built in algorithm
     * @param algorithmID The algorithm ID.
     * @param algorithmData The algorithm data.
     */
    public void addAttributeWithAlgorithmData(String URI, String localName, String qName,
            String algorithmURI, int algorithmID, Object algorithmData) {
        if (_length >= _algorithmData.length) {
            resize();
        }
        
        int i = _length * SIZE;
        _data[i++] = replaceNull(URI);
        _data[i++] = replaceNull(localName);
        _data[i++] = replaceNull(qName);
        _data[i++] = "CDATA";
        _data[i++] = "";
        _data[i++] = algorithmURI;
        _algorithmIds[_length] = algorithmID;
        _algorithmData[_length] = algorithmData;
        
        _length++;
    }
    
    /**
     * Replace an attribute value with algorithm data.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param index The index of the attribute whose value is to be replaced
     * @param algorithmURI The algorithm URI, or null if a built in algorithm
     * @param algorithmID The algorithm ID.
     * @param algorithmData The algorithm data.
     */
    public void replaceAttributeAlgorithmData(int index,
            String algorithmURI, int algorithmID, Object algorithmData) {
        if (index < 0 || index >= _length) return;
        
        int i = index * SIZE;
        _data[i + VALUE_OFFSET] = null;
        _data[i + ALGORITHMURI_OFFSET] = algorithmURI;
        _algorithmIds[index] = algorithmID;
        _algorithmData[index] = algorithmData;
    }
    
    /**
     * Copy an entire Attributes object.
     *
     * @param atts The attributes to copy.
     */
    public void setAttributes(Attributes atts) {
        _length = atts.getLength();
        if (_length > 0) {
            
            if (_length >= _algorithmData.length) {
                resizeNoCopy();
            }
            
            int index = 0;
            for (int i = 0; i < _length; i++) {
                _data[index++] = atts.getURI(i);
                _data[index++] = atts.getLocalName(i);
                _data[index++] = atts.getQName(i);
                _data[index++] = atts.getType(i);
                _data[index++] = atts.getValue(i);
                index++;
            }
        }
    }
    
    /**
     * Copy an entire EncodingAlgorithmAttributes object.
     *
     * @param atts The attributes to copy.
     */
    public void setAttributes(EncodingAlgorithmAttributes atts) {
        _length = atts.getLength();
        if (_length > 0) {
            
            if (_length >= _algorithmData.length) {
                resizeNoCopy();
            }
            
            int index = 0;
            for (int i = 0; i < _length; i++) {
                _data[index++] = atts.getURI(i);
                _data[index++] = atts.getLocalName(i);
                _data[index++] = atts.getQName(i);
                _data[index++] = atts.getType(i);
                _data[index++] = atts.getValue(i);
                _data[index++] = atts.getAlgorithmURI(i);
                _algorithmIds[i] = atts.getAlgorithmIndex(i);
                _algorithmData[i] = atts.getAlgorithmData(i);
            }
        }
    }
    
    // org.xml.sax.Attributes
    
    public final int getLength() {
        return _length;
    }
    
    public final String getLocalName(int index) {
        if (index >= 0 && index < _length) {
            return _data[index * SIZE + LOCALNAME_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getQName(int index) {
        if (index >= 0 && index < _length) {
            return _data[index * SIZE + QNAME_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getType(int index) {
        if (index >= 0 && index < _length) {
            return _data[index * SIZE + TYPE_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getURI(int index) {
        if (index >= 0 && index < _length) {
            return _data[index * SIZE + URI_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getValue(int index) {
        if (index >= 0 && index < _length) {
            final String value = _data[index * SIZE + VALUE_OFFSET];
            if (value != null) return value;
        } else {
            return null;
        }
        
        if (_algorithmData[index] == null || _registeredEncodingAlgorithms == null) {
            return null;
        }
        
        try {
            return _data[index * SIZE + VALUE_OFFSET] = convertEncodingAlgorithmDataToString(
                    _algorithmIds[index],
                    _data[index * SIZE + ALGORITHMURI_OFFSET],
                    _algorithmData[index]).toString();
        } catch (IOException e) {
            return null;
        } catch (FastInfosetException e) {
            return null;
        }
    }
    
    public final int getIndex(String qName) {
        for (int index = 0; index < _length; index++) {
            if (qName.equals(_data[index * SIZE + QNAME_OFFSET])) {
                return index;
            }
        }
        return -1;
    }
    
    public final String getType(String qName) {
        int index = getIndex(qName);
        if (index >= 0) {
            return _data[index * SIZE + TYPE_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getValue(String qName) {
        int index = getIndex(qName);
        if (index >= 0) {
            return _data[index * SIZE + VALUE_OFFSET];
        } else {
            return null;
        }
    }
    
    public final int getIndex(String uri, String localName) {
        for (int index = 0; index < _length; index++) {
            if (localName.equals(_data[index * SIZE + LOCALNAME_OFFSET]) &&
                    uri.equals(_data[index * SIZE + URI_OFFSET])) {
                return index;
            }
        }
        return -1;
    }
    
    public final String getType(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index >= 0) {
            return _data[index * SIZE + TYPE_OFFSET];
        } else {
            return null;
        }
    }
    
    public final String getValue(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index >= 0) {
            return _data[index * SIZE + VALUE_OFFSET];
        } else {
            return null;
        }
    }
    
    // EncodingAlgorithmAttributes
    
    public final String getAlgorithmURI(int index) {
        if (index >= 0 && index < _length) {
            return _data[index * SIZE + ALGORITHMURI_OFFSET];
        } else {
            return null;
        }
    }
    
    public final int getAlgorithmIndex(int index) {
        if (index >= 0 && index < _length) {
            return _algorithmIds[index];
        } else {
            return -1;
        }
    }
    
    public final Object getAlgorithmData(int index) {
        if (index >= 0 && index < _length) {
            return _algorithmData[index];
        } else {
            return null;
        }
    }
    
    
    // -----
    
    private final String replaceNull(String s) {
        return (s != null) ? s : "";
    }
    
    private final void resizeNoCopy() {
        final int newLength = _length * 3 / 2 + 1;
        
        _data = new String[newLength * SIZE];
        _algorithmIds = new int[newLength];
        _algorithmData = new Object[newLength];
    }
    
    private final void resize() {
        final int newLength = _length * 3 / 2 + 1;
        
        String[] data = new String[newLength * SIZE];
        int[] algorithmIds = new int[newLength];
        Object[] algorithmData = new Object[newLength];
        
        System.arraycopy(_data, 0, data, 0, _length * SIZE);
        System.arraycopy(_algorithmIds, 0, algorithmIds, 0, _length);
        System.arraycopy(_algorithmData, 0, algorithmData, 0, _length);
        
        _data = data;
        _algorithmIds = algorithmIds;
        _algorithmData = algorithmData;
    }
    
    private final StringBuffer convertEncodingAlgorithmDataToString(
            int identifier, String URI, Object data) throws FastInfosetException, IOException {
        EncodingAlgorithm ea = null;
        if (identifier < EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            ea = BuiltInEncodingAlgorithmFactory.table[identifier];
        } else if (identifier == EncodingAlgorithmIndexes.CDATA) {
            throw new EncodingAlgorithmException(
                    CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
        } else if (identifier >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            if (URI == null) {
                throw new EncodingAlgorithmException(
                        CommonResourceBundle.getInstance().getString("message.URINotPresent") + identifier);
            }
            
            ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea == null) {
                throw new EncodingAlgorithmException(
                        CommonResourceBundle.getInstance().getString("message.algorithmNotRegistered") + URI);
            }
        } else {
            // Reserved built-in algorithms for future use
            // TODO should use sax property to decide if event will be
            // reported, allows for support through handler if required.
            throw new EncodingAlgorithmException(
                    CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        
        final StringBuffer sb = new StringBuffer();
        ea.convertToCharacters(data, sb);
        return sb;
    }
}
