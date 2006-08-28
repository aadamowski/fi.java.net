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

import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import java.io.IOException;
import java.util.HashMap;
import org.xml.sax.SAXException;
import java.util.Map;

/**
 * The Fast Infoset SAX serializer that maps prefixes to user specified prefixes
 * that are specified in a namespace URI to prefix map.
 * <p>
 * This serializer will not preserve the original prefixes and this serializer
 * should not be used when prefixes need to be preserved, such as the case
 * when there are qualified names in content.
 * <p>
 * A namespace URI to prefix map is utilized such that the prefixes
 * in the map are utilized rather than the prefixes specified in
 * the qualified name for elements and attributes.
 * <p>
 * Any namespace declarations with a namespace URI that is not present in
 * the map are added.
 * <p>
 */
public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer {
    protected Map _namespaceToPrefixMapping;
    protected String _lastCheckedNamespace;
    protected String _lastCheckedPrefix;
    
    public SAXDocumentSerializerWithPrefixMapping(Map namespaceToPrefixMapping) {
        // Use the local name to look up elements/attributes
        super(true);
        _namespaceToPrefixMapping = new HashMap(namespaceToPrefixMapping);
        _namespaceToPrefixMapping.put("", "");
    }
    
    // ContentHandler

    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (_elementHasNamespaces == false) {
                encodeTermination();

                // Mark the current buffer position to flag attributes if necessary
                mark();
                _elementHasNamespaces = true;

                // Write out Element byte with namespaces
                write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
            }

            final String p = getPrefix(uri);
            if (p != null)
                encodeNamespaceAttribute(p, uri);
            else {
                _namespaceToPrefixMapping.put(uri, prefix);
                encodeNamespaceAttribute(prefix, uri);
            }
            
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                final QualifiedName n = names[i];
                if (namespaceURI == n.namespaceName || namespaceURI.equals(n.namespaceName)) {
                    encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);            
    }
    
    protected final boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return true;
                }
            }
        }

        return encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);            
    }
    
    protected final String getPrefix(String namespaceURI) {
        if (_lastCheckedNamespace == namespaceURI) return _lastCheckedPrefix;
        
        _lastCheckedNamespace = namespaceURI;
        return _lastCheckedPrefix = (String)_namespaceToPrefixMapping.get(namespaceURI);
    }
}
