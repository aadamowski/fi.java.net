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


package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.QualifiedName;
import org.xml.sax.Attributes;

public class AttributesHolder implements Attributes {
    private static final int DEFAULT_CAPACITY = 10;

    private int _attributeCount;
    private QualifiedName[] _names;
    private String[] _values;

    public AttributesHolder() {
        _names = new QualifiedName[DEFAULT_CAPACITY];
        _values = new String[DEFAULT_CAPACITY];
    }

    // -----
    
    public final int getLength() {
        return _attributeCount;
    }

    public final String getLocalName(int index) {
        return _names[index].localName;
    }

    public final String getQName(int index) {
        return _names[index].qName;
    }

    public final String getType(int index) {
        return "CDATA";
    }

    public final String getURI(int index) {
        return _names[index].namespaceName;
    }

    public final String getValue(int index) {
        return _values[index];
    }

    public final int getIndex(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        String localName = qName;
        if (i >= 0) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }
        
        for (i = 0; i < _attributeCount; i++) {
            QualifiedName name = _names[i];
            if (localName.equals(name.localName) &&
                prefix.equals(name.prefix)) {
                return i;
            }
        }
        return -1;
    }

    public final String getType(String qName) {
        int index = getIndex(qName);
        if (index >= 0) {
            return "CDATA";
        } else {
            return null;
        }
    }

    public final String getValue(String qName) {
        int index = getIndex(qName);
        if (index >= 0) {
            return _values[index];
        } else {
            return null;
        }
    }

    public final int getIndex(String uri, String localName) {
        for (int i = 0; i < _attributeCount; i++) {
            QualifiedName name = _names[i];
            if (localName.equals(name.localName) &&
                uri.equals(name.namespaceName)) {
                return i;
            }
        }
        return -1;
    }

    public final String getType(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index >= 0) {
            return "CDATA";
        } else {
            return null;
        }
    }

    public final String getValue(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index >= 0) {
            return _values[index];
        } else {
            return null;
        }
    }

    public final void clear() {
        for (int i = 0; i < _attributeCount; i++) {
            _values[i] = null;
        }
        _attributeCount = 0;
    }

    // -----
    
    public final void addAttribute(QualifiedName name, String value) {
        if (_attributeCount == _names.length) {
            QualifiedName[] names = new QualifiedName[_attributeCount * 3 / 2 + 1];
            String[] values = new String[_attributeCount  * 3 / 2 + 1];
            System.arraycopy(_names, 0, names, 0, _attributeCount);
            System.arraycopy(_values, 0, values, 0, _attributeCount);
            _names = names;
            _values = values;
        }
        _names[_attributeCount] = name;
        _values[_attributeCount++] = value;
    }

    public final QualifiedName getQualifiedName(int index) {
        return _names[index];
    }
    
    public final String getPrefix(int index) {
        return _names[index].prefix;
    }
}
