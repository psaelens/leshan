//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.28 at 04:33:37 PM CEST 
//


package org.onem2m.xml.protocols;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.onem2m.org/xml/protocols}regularResource">
 *       &lt;sequence>
 *         &lt;element name="st" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="sr" type="{http://www.onem2m.org/xml/protocols}ID"/>
 *         &lt;element name="tg" type="{http://www.onem2m.org/xml/protocols}ID"/>
 *         &lt;element name="ls" type="{http://www.onem2m.org/xml/protocols}timestamp"/>
 *         &lt;element name="eca" type="{http://www.onem2m.org/xml/protocols}eventCat"/>
 *         &lt;element name="dmd" type="{http://www.onem2m.org/xml/protocols}deliveryMetaData"/>
 *         &lt;element name="arq" type="{http://www.onem2m.org/xml/protocols}aggregatedRequest"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ch" type="{http://www.onem2m.org/xml/protocols}childResourceRef" maxOccurs="unbounded"/>
 *           &lt;choice maxOccurs="unbounded">
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}sub"/>
 *           &lt;/choice>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "st",
    "sr",
    "tg",
    "ls",
    "eca",
    "dmd",
    "arq",
    "ch",
    "sub"
})
@XmlRootElement(name = "dlv")
public class Dlv
    extends RegularResource
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger st;
    @XmlElement(required = true)
    protected String sr;
    @XmlElement(required = true)
    protected String tg;
    @XmlElement(required = true)
    protected String ls;
    @XmlElement(required = true)
    protected String eca;
    @XmlElement(required = true)
    protected DeliveryMetaData dmd;
    @XmlElement(required = true)
    protected AggregatedRequest arq;
    protected List<ChildResourceRef> ch;
    @XmlElement(namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Sub> sub;

    /**
     * Gets the value of the st property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSt() {
        return st;
    }

    /**
     * Sets the value of the st property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSt(BigInteger value) {
        this.st = value;
    }

    /**
     * Gets the value of the sr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSr() {
        return sr;
    }

    /**
     * Sets the value of the sr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSr(String value) {
        this.sr = value;
    }

    /**
     * Gets the value of the tg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTg() {
        return tg;
    }

    /**
     * Sets the value of the tg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTg(String value) {
        this.tg = value;
    }

    /**
     * Gets the value of the ls property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLs() {
        return ls;
    }

    /**
     * Sets the value of the ls property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLs(String value) {
        this.ls = value;
    }

    /**
     * Gets the value of the eca property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEca() {
        return eca;
    }

    /**
     * Sets the value of the eca property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEca(String value) {
        this.eca = value;
    }

    /**
     * Gets the value of the dmd property.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryMetaData }
     *     
     */
    public DeliveryMetaData getDmd() {
        return dmd;
    }

    /**
     * Sets the value of the dmd property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryMetaData }
     *     
     */
    public void setDmd(DeliveryMetaData value) {
        this.dmd = value;
    }

    /**
     * Gets the value of the arq property.
     * 
     * @return
     *     possible object is
     *     {@link AggregatedRequest }
     *     
     */
    public AggregatedRequest getArq() {
        return arq;
    }

    /**
     * Sets the value of the arq property.
     * 
     * @param value
     *     allowed object is
     *     {@link AggregatedRequest }
     *     
     */
    public void setArq(AggregatedRequest value) {
        this.arq = value;
    }

    /**
     * Gets the value of the ch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCh().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChildResourceRef }
     * 
     * 
     */
    public List<ChildResourceRef> getCh() {
        if (ch == null) {
            ch = new ArrayList<ChildResourceRef>();
        }
        return this.ch;
    }

    /**
     * Gets the value of the sub property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sub property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSub().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sub }
     * 
     * 
     */
    public List<Sub> getSub() {
        if (sub == null) {
            sub = new ArrayList<Sub>();
        }
        return this.sub;
    }

}
