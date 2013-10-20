/*
 * Copyright (c) 2007-2008, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of OWASP nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html;

import org.owasp.validator.html.model.AntiSamyPattern;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Policy.java
 * <p/>
 * This file holds the model for our policy engine.
 *
 * @author Arshan Dabirsiaghi
 */

public class Policy {

    private static Policy _instance = null;
    private static final String DEFAULT_POLICY_URI = "resources/antisamy.xml";
    private static final String DEFAULT_ONINVALID = "removeAttribute";

    public static final int DEFAULT_MAX_INPUT_SIZE = 100000;
    public static final int DEFAULT_MAX_STYLESHEET_IMPORTS = 1;

    public static final String OMIT_XML_DECLARATION = "omitXmlDeclaration";
    public static final String OMIT_DOCTYPE_DECLARATION = "omitDoctypeDeclaration";
    public static final String MAX_INPUT_SIZE = "maxInputSize";
    public static final String USE_XHTML = "useXHTML";
    public static final String FORMAT_OUTPUT = "formatOutput";
    public static final String EMBED_STYLESHEETS = "embedStyleSheets";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";

    public static final String ENCODE_TAGS = "onUnknownTag";

    private static char REGEXP_BEGIN = '^';
    private static char REGEXP_END = '$';

    private HashMap commonRegularExpressions;
    private HashMap commonAttributes;
    private HashMap tagRules;
    private HashMap cssRules;
    private HashMap directives;
    private HashMap globalAttributes;
    private ArrayList encodeTags;

    private ArrayList tagNames;

    public boolean isTagInListToEncode(String s) {
        for (int i = 0; i < encodeTags.size(); i++) {
            if (s.equals((String) encodeTags.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a Tag from the Policy.
     *
     * @param tagName The name of the Tag to look up.
     * @return The Tag associated with the name specified, or null if none is found.
     */
    public Tag getTagByName(String tagName) {

        return (Tag) tagRules.get(tagName.toLowerCase());

    }

    /**
     * Retrieves a CSS Property from the Policy.
     *
     * @param propertyName The name of the CSS Property to look up.
     * @return The CSS Property associated with the name specified, or null if none is found.
     */
    public Property getPropertyByName(String propertyName) {

        return (Property) cssRules.get(propertyName.toLowerCase());

    }

    /**
     * This retrieves a Policy based on a default location ("resources/antisamy.xml")
     *
     * @return A populated Policy object based on the XML policy file located in the default location.
     * @throws PolicyException If the file is not found or there is a problem parsing the file.
     */
    public static Policy getInstance() throws PolicyException {

        try {
            _instance = new Policy(new FileInputStream(DEFAULT_POLICY_URI));
        } catch (IOException e) {
            throw new PolicyException(e);
        }

        return _instance;
    }

    /**
     * This retrieves a Policy based on the file name passed in
     *
     * @param filename The path to the XML policy file.
     * @return A populated Policy object based on the XML policy file located in the location passed in.
     * @throws PolicyException If the file is not found or there is a problem parsing the file.
     */
    public static Policy getInstance(String filename) throws PolicyException {

        try {
            _instance = new Policy(new FileInputStream(filename));
        } catch (IOException e) {
            throw new PolicyException(e);
        }

        return _instance;
    }

    /**
     * This retrieves a Policy based on the File object passed in
     *
     * @param file A File object which contains the XML policy information.
     * @return A populated Policy object based on the XML policy file pointed to by the File parameter.
     * @throws PolicyException If the file is not found or there is a problem parsing the file.
     */
    public static Policy getInstance(File file) throws PolicyException {

        try {
            _instance = new Policy(new FileInputStream(file.getAbsoluteFile()));
        } catch (IOException e) {
            throw new PolicyException(e);
        }

        return _instance;
    }


    /**
     * Load the policy from an XML file.
     *
     * @param file Load a policy from the File object.
     * @throws PolicyException
     */
    private Policy(File file) throws PolicyException, IOException {
        this(new FileInputStream(file));
    }

    /**
     * This retrieves a Policy based on the InputStream object passed in
     *
     * @param inputStream An InputStream which contains thhe XML policy information.
     * @return A populated Policy object based on the XML policy file pointed to by the inputStream parameter.
     * @throws PolicyException If there is a problem parsing the input stream.
     */
    public static Policy getInstance(InputStream inputStream) throws PolicyException {

        _instance = new Policy(inputStream);

        return _instance;
    }

    /**
     * Load the policy from an XML file.
     *
     * @param filename Load a policy from the filename specified.
     * @throws PolicyException
     */
    private Policy(InputStream is) throws PolicyException {

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = null;

            /**
             * Load and parse the file.
             */
            dom = db.parse(is);


            /**
             * Get the policy information out of it!
             */

            Element topLevelElement = dom.getDocumentElement();

            /**
             * First thing to read is the <common-regexps> section.
             */

            Element commonRegularExpressionListNode = (Element) topLevelElement.getElementsByTagName("common-regexps").item(0);

            this.commonRegularExpressions = parseCommonRegExps(commonRegularExpressionListNode);


            /**
             * Next we read in the directives.
             */

            Element directiveListNode = (Element) topLevelElement.getElementsByTagName("directives").item(0);
            this.directives = parseDirectives(directiveListNode);


            /**
             * Next we read in the common attributes.
             */
            Element commonAttributeListNode = (Element) topLevelElement.getElementsByTagName("common-attributes").item(0);

            this.commonAttributes = parseCommonAttributes(commonAttributeListNode);

            /**
             * Next we need the global tag attributes (id, style, etc.)
             */

            Element globalAttributeListNode = (Element) topLevelElement.getElementsByTagName("global-tag-attributes").item(0);

            this.globalAttributes = parseGlobalAttributes(globalAttributeListNode);

            /**
             * Next we read in the tags that should be encoded when they're encountered like <g>.
             */

            NodeList tagsToEncodeList = topLevelElement.getElementsByTagName("tags-to-encode");
            if (tagsToEncodeList != null && tagsToEncodeList.getLength() != 0) {
                this.encodeTags = parseTagsToEncode((Element) tagsToEncodeList.item(0));
            } else {
                this.encodeTags = new ArrayList();
            }

            /**
             * Next, we read in the tag restrictions.
             */
            Element tagListNode = (Element) topLevelElement.getElementsByTagName("tag-rules").item(0);

            this.tagRules = parseTagRules(tagListNode);

            /**
             * Finally, we read in the CSS rules.
             */
            Element cssListNode = (Element) topLevelElement.getElementsByTagName("css-rules").item(0);

            this.cssRules = parseCSSRules(cssListNode);


        } catch (SAXException e) {
            throw new PolicyException(e);
        } catch (ParserConfigurationException e) {
            throw new PolicyException(e);
        } catch (IOException e) {
            throw new PolicyException(e);
        }
    }


    /**
     * Go through <directives> section of the policy file.
     *
     * @param directiveListNode Top level of <directives>
     * @return A HashMap of directives for validation behavior.
     */
    private HashMap parseDirectives(Element root) {

        HashMap directives = new HashMap();

        NodeList directiveNodes = root.getElementsByTagName("directive");

        for (int i = 0; i < directiveNodes.getLength(); i++) {

            Element ele = (Element) directiveNodes.item(i);

            String name = XMLUtil.getAttributeValue(ele, "name");
            String value = XMLUtil.getAttributeValue(ele, "value");

            directives.put(name, value);

        }

        return directives;
    }

    /**
     * Go through <tags-to-encode> section of the policy file.
     *
     * @param root Top level of <tags-to-encode>
     * @return A HashMap of String tags that are to be encoded when they're encountered.
     * @throws PolicyException
     */
    private ArrayList parseTagsToEncode(Element root) throws PolicyException {

        ArrayList tagsToEncode = new ArrayList();

        NodeList tagsToEncodeNodes = root.getElementsByTagName("tag");

        if (tagsToEncodeNodes != null) {

            for (int i = 0; i < tagsToEncodeNodes.getLength(); i++) {

                Element ele = (Element) tagsToEncodeNodes.item(i);
                if (ele.getFirstChild() != null && ele.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                    tagsToEncode.add(ele.getFirstChild().getNodeValue());
                }

            }
        }

        return tagsToEncode;
    }

    /**
     * Go through <global-tag-attributes> section of the policy file.
     *
     * @param globalAttributeListNode Top level of <global-tag-attributes>
     * @return A HashMap of global Attributes that need validation for every tag.
     * @throws PolicyException
     */
    private HashMap parseGlobalAttributes(Element root) throws PolicyException {

        HashMap globalAttributes = new HashMap();

        NodeList globalAttributeNodes = root.getElementsByTagName("attribute");

		/*
         * Loop through the list of regular expressions and add them to the collection.
		 */
        for (int i = 0; i < globalAttributeNodes.getLength(); i++) {
            Element ele = (Element) globalAttributeNodes.item(i);

            String name = XMLUtil.getAttributeValue(ele, "name");

            Attribute toAdd = getCommonAttributeByName(name);

            if (toAdd != null) {
                globalAttributes.put(name.toLowerCase(), toAdd);
            } else {
                throw new PolicyException("Global attribute '" + name + "' was not defined in <common-attributes>");
            }
        }

        return globalAttributes;
    }

    /**
     * Go through the <common-regexps> section of the policy file.
     *
     * @param root Top level of <common-regexps>
     * @return An ArrayList of AntiSamyPattern objects.
     */
    private HashMap parseCommonRegExps(Element root) {

        HashMap commonRegularExpressions = new HashMap();

        NodeList commonRegExpPatternNodes = root.getElementsByTagName("regexp");

		/*
         * Loop through the list of regular expressions and add them to the collection.
		 */
        for (int i = 0; i < commonRegExpPatternNodes.getLength(); i++) {
            Element ele = (Element) commonRegExpPatternNodes.item(i);

            String name = XMLUtil.getAttributeValue(ele, "name");
            Pattern pattern = Pattern.compile(XMLUtil.getAttributeValue(ele, "value"));

            commonRegularExpressions.put(name, new AntiSamyPattern(name, pattern));

        }

        return commonRegularExpressions;
    }


    /**
     * Go through the <common-attributes> section of the policy file.
     *
     * @param root Top level of <common-attributes>
     * @return An ArrayList of Attribute objects.
     */
    private HashMap parseCommonAttributes(Element root) {

        HashMap commonAttributes = new HashMap();

        NodeList commonAttributesNodes = root.getElementsByTagName("attribute");

		/*
		 * Loop through the list of attributes and add them to the collection.
		 */
        for (int i = 0; i < commonAttributesNodes.getLength(); i++) {

            Element ele = (Element) commonAttributesNodes.item(i);

            String onInvalid = XMLUtil.getAttributeValue(ele, "onInvalid");
            String name = XMLUtil.getAttributeValue(ele, "name");

            Attribute attribute = new Attribute(XMLUtil.getAttributeValue(ele, "name"));
            attribute.setDescription(XMLUtil.getAttributeValue(ele, "description"));

            if (onInvalid != null && onInvalid.length() > 0) {
                attribute.setOnInvalid(onInvalid);
            } else {
                attribute.setOnInvalid(DEFAULT_ONINVALID);
            }

            Element regExpListNode = (Element) ele.getElementsByTagName("regexp-list").item(0);


            if (regExpListNode != null) {
                NodeList regExpList = regExpListNode.getElementsByTagName("regexp");

				/*
				 * First go through the allowed regular expressions.
				 */
                for (int j = 0; j < regExpList.getLength(); j++) {
                    Element regExpNode = (Element) regExpList.item(j);

                    String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
                    String value = XMLUtil.getAttributeValue(regExpNode, "value");

                    if (regExpName != null && regExpName.length() > 0) {
						/*
						 * Get the common regular expression.
						 */
                        attribute.addAllowedRegExp(getRegularExpression(regExpName).getPattern());
                    } else {
                        attribute.addAllowedRegExp(Pattern.compile(REGEXP_BEGIN + value + REGEXP_END));
                    }
                }
            }

            Element literalListNode = (Element) ele.getElementsByTagName("literal-list").item(0);

            if (literalListNode != null) {

                NodeList literalList = literalListNode.getElementsByTagName("literal");
				/*
				 * Then go through the allowed constants.
				 */
                for (int j = 0; j < literalList.getLength(); j++) {
                    Element literalNode = (Element) literalList.item(j);

                    String value = XMLUtil.getAttributeValue(literalNode, "value");

                    if (value != null && value.length() > 0) {
                        attribute.addAllowedValue(value);
                    } else if (literalNode.getNodeValue() != null) {
                        attribute.addAllowedValue(literalNode.getNodeValue());
                    }

                }

            }

            commonAttributes.put(name.toLowerCase(), attribute);

        }

        return commonAttributes;
    }


    /**
     * Private method for parsing the <tag-rules> from the XML file.
     *
     * @param root The root element for <tag-rules>
     * @return A List<Tag> containing the rules.
     * @throws PolicyException
     */
    private HashMap parseTagRules(Element root) throws PolicyException {

        HashMap tags = new HashMap();

        NodeList tagList = root.getElementsByTagName("tag");

		/*
		 * Go through tags.
		 */
        for (int i = 0; i < tagList.getLength(); i++) {

            Element tagNode = (Element) tagList.item(i);

            String name = XMLUtil.getAttributeValue(tagNode, "name");
            String action = XMLUtil.getAttributeValue(tagNode, "action");

			/*
			 * Create tag.
			 */

            Tag tag = new Tag(name);

            if (tagNames == null) {
                tagNames = new ArrayList();
            }

            tagNames.add(name);

            tag.setAction(action);

            NodeList attributeList = tagNode.getElementsByTagName("attribute");

			/*
			 * Add its attribute rules.
			 */
            for (int j = 0; j < attributeList.getLength(); j++) {

                Element attributeNode = (Element) attributeList.item(j);

                if (!attributeNode.hasChildNodes()) {
                    Attribute attribute = getCommonAttributeByName(XMLUtil.getAttributeValue(attributeNode, "name"));

					/*
					 * All they provided was the name, so they must want a common
					 * attribute.
					 */
                    if (attribute != null) {

						/*
						 * If they provide onInvalid/description values here they will
						 * override the common values.
						 */

                        String onInvalid = XMLUtil.getAttributeValue(attributeNode, "onInvalid");
                        String description = XMLUtil.getAttributeValue(attributeNode, "description");

                        if (onInvalid != null && onInvalid.length() != 0) {
                            attribute.setOnInvalid(onInvalid);
                        }
                        if (description != null && description.length() != 0) {
                            attribute.setDescription(description);
                        }

                        tag.addAttribute((Attribute) attribute.clone());

                    } else {

                        throw new PolicyException("Attribute '" + XMLUtil.getAttributeValue(attributeNode, "name") + "' was referenced as a common attribute in definition of '" + tag.getName() + "', but does not exist in <common-attributes>");

                    }

                } else {
					/*
					 * Custom attribute for this tag.
					 */
                    Attribute attribute = new Attribute(XMLUtil.getAttributeValue(attributeNode, "name"));
                    attribute.setOnInvalid(XMLUtil.getAttributeValue(attributeNode, "onInvalid"));
                    attribute.setDescription(XMLUtil.getAttributeValue(attributeNode, "description"));

					/*
					 * Get the list of regexps for the attribute.
					 */
                    Element regExpListNode = (Element) attributeNode.getElementsByTagName("regexp-list").item(0);

                    if (regExpListNode != null) {
                        NodeList regExpList = regExpListNode.getElementsByTagName("regexp");

                        for (int k = 0; k < regExpList.getLength(); k++) {

                            Element regExpNode = (Element) regExpList.item(k);

                            String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
                            String value = XMLUtil.getAttributeValue(regExpNode, "value");

							/*
							 * Look up common regular expression specified
							 * by the "name" field. They can put a common
							 * name in the "name" field or provide a custom
							 * value in the "value" field. They must choose
							 * one or the other, not both.
							 */
                            if (regExpName != null && regExpName.length() > 0) {

                                AntiSamyPattern pattern = getRegularExpression(regExpName);

                                if (pattern != null) {

                                    attribute.addAllowedRegExp(pattern.getPattern());
                                } else {

                                    throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + tag.getName() + "', but does not exist in <common-regexp>");
                                }

                            } else if (value != null && value.length() > 0) {
                                attribute.addAllowedRegExp(Pattern.compile(REGEXP_BEGIN + value + REGEXP_END));
                            }
                        }
                    }

					/*
					 * Get the list of constant values for the attribute.
					 */
                    Element literalListNode = (Element) attributeNode.getElementsByTagName("literal-list").item(0);

                    if (literalListNode != null) {
                        NodeList literalList = literalListNode.getElementsByTagName("literal");

                        for (int k = 0; k < literalList.getLength(); k++) {
                            Element literalNode = (Element) literalList.item(k);
                            String value = XMLUtil.getAttributeValue(literalNode, "value");

							/*
							 * Any constant value will do.
							 */

                            if (value != null && value.length() > 0) {
                                attribute.addAllowedValue(value);
                            } else if (literalNode.getNodeValue() != null) {
                                attribute.addAllowedValue(literalNode.getNodeValue());
                            }

                        }
                    }
					/*
					 * Add fully built attribute.
					 */
                    tag.addAttribute(attribute);
                }

            }

            tags.put(name.toLowerCase(), tag);
        }

        return tags;
    }

    /**
     * Go through the <css-rules> section of the policy file.
     *
     * @param root Top level of <css-rules>
     * @return An ArrayList of Property objects.
     * @throws PolicyException
     */
    private HashMap parseCSSRules(Element root) throws PolicyException {

        HashMap properties = new HashMap();

        NodeList propertyNodes = root.getElementsByTagName("property");

		/*
		 * Loop through the list of attributes and add them to the collection.
		 */
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Element ele = (Element) propertyNodes.item(i);

            String name = XMLUtil.getAttributeValue(ele, "name");
            String description = XMLUtil.getAttributeValue(ele, "description");

            Property property = new Property(name);
            property.setDescription(description);

            String onInvalid = XMLUtil.getAttributeValue(ele, "onInvalid");

            if (onInvalid != null && onInvalid.length() > 0) {
                property.setOnInvalid(onInvalid);
            } else {
                property.setOnInvalid(DEFAULT_ONINVALID);
            }

            Element regExpListNode = (Element) ele.getElementsByTagName("regexp-list").item(0);


            if (regExpListNode != null) {
                NodeList regExpList = regExpListNode.getElementsByTagName("regexp");

				/*
				 * First go through the allowed regular expressions.
				 */
                for (int j = 0; j < regExpList.getLength(); j++) {
                    Element regExpNode = (Element) regExpList.item(j);

                    String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
                    String value = XMLUtil.getAttributeValue(regExpNode, "value");

                    AntiSamyPattern pattern = getRegularExpression(regExpName);

                    if (pattern != null) {

                        property.addAllowedRegExp(pattern.getPattern());
                    } else if (value != null) {
                        property.addAllowedRegExp(Pattern.compile(REGEXP_BEGIN + value + REGEXP_END));

                    } else {

                        throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + property.getName() + "', but does not exist in <common-regexp>");
                    }

                }
            }

            Element literalListNode = (Element) ele.getElementsByTagName("literal-list").item(0);

            if (literalListNode != null) {

                NodeList literalList = literalListNode.getElementsByTagName("literal");
				/*
				 * Then go through the allowed constants.
				 */
                for (int j = 0; j < literalList.getLength(); j++) {
                    Element literalNode = (Element) literalList.item(j);
                    property.addAllowedValue(XMLUtil.getAttributeValue(literalNode, "value"));
                }

            }

            Element shorthandListNode = (Element) ele.getElementsByTagName("shorthand-list").item(0);
            if (shorthandListNode != null) {

                NodeList shorthandList = shorthandListNode.getElementsByTagName("shorthand");
				/*
				 * Then go through the allowed constants.
				 */
                for (int j = 0; j < shorthandList.getLength(); j++) {
                    Element shorthandNode = (Element) shorthandList.item(j);
                    property.addShorthandRef(XMLUtil.getAttributeValue(shorthandNode, "name"));
                }

            }

            properties.put(name.toLowerCase(), property);

        }

        return properties;
    }


    /**
     * A simple method for returning on of the <common-regexp> entries by
     * name.
     *
     * @param name The name of the common regexp we want to look up.
     * @return An AntiSamyPattern associated with the lookup name specified.
     */
    public AntiSamyPattern getRegularExpression(String name) {

        return (AntiSamyPattern) commonRegularExpressions.get(name);

    }

    /**
     * A simple method for returning on of the <global-attribute> entries by
     * name.
     *
     * @param name The name of the global-attribute we want to look up.
     * @return An Attribute associated with the global-attribute lookup name specified.
     */
    public Attribute getGlobalAttributeByName(String name) {

        return (Attribute) globalAttributes.get(name.toLowerCase());

    }

    /**
     * A simple method for returning on of the <common-attribute> entries by
     * name.
     *
     * @param name The name of the common-attribute we want to look up.
     * @return An Attribute associated with the common-attribute lookup name specified.
     */
    private Attribute getCommonAttributeByName(String attributeName) {

        return (Attribute) commonAttributes.get(attributeName.toLowerCase());

    }


    /**
     * Return all the tags accepted by the Policy object.
     *
     * @return A String array of all the tag names accepted by the current Policy.
     */
    public String[] getTags() {
        return (String[]) tagNames.toArray(new String[1]);
    }

    /**
     * Return a directive value based on a lookup name.
     *
     * @return A String object containing the directive associated with the lookup name, or null if none is found.
     */
    public String getDirective(String name) {
        return (String) directives.get(name);
    }

    /**
     * Set a directive for a value based on a name.
     *
     * @param name  A directive to set a value for.
     * @param value The new value for the directive.
     */
    public void setDirective(String name, String value) {
        directives.put(name, value);
    }

    /**
     * Returns the maximum input size. If this value is not specified by
     * the policy, the <code>DEFAULT_MAX_INPUT_SIZE</code> is used.
     *
     * @return the maximium input size.
     */
    public int getMaxInputSize() {
        int maxInputSize = Policy.DEFAULT_MAX_INPUT_SIZE;

        try {
            maxInputSize = Integer.parseInt(getDirective("maxInputSize"));
        } catch (NumberFormatException nfe) {
        }

        return maxInputSize;
    }

    /**
     * Main test unit.
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // parse an XML document into a DOM tree
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new File("resources/antisamy.xml"));

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new File("resources/antisamy.xsd"));
        Schema schema = factory.newSchema(schemaFile);

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
            System.out.println("made it through!");
        } catch (SAXException e) {
            // instance document is invalid!
            e.printStackTrace();
        }

    }
}
