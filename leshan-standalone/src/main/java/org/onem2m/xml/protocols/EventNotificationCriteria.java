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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventNotificationCriteria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventNotificationCriteria">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crb" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="cra" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="ms" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="us" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="sts" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="stb" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="exb" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="exa" type="{http://www.onem2m.org/xml/protocols}timestamp" minOccurs="0"/>
 *         &lt;element name="sza" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="szb" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="om" type="{http://www.onem2m.org/xml/protocols}operation" maxOccurs="5" minOccurs="0"/>
 *         &lt;element name="atr" type="{http://www.onem2m.org/xml/protocols}attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="net" type="{http://www.onem2m.org/xml/protocols}notificationEventType" maxOccurs="4" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventNotificationCriteria", propOrder = {
    "crb",
    "cra",
    "ms",
    "us",
    "sts",
    "stb",
    "exb",
    "exa",
    "sza",
    "szb",
    "om",
    "atr",
    "net"
})
public class EventNotificationCriteria {

    protected String crb;
    protected String cra;
    protected String ms;
    protected String us;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger sts;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stb;
    protected String exb;
    protected String exa;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sza;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger szb;
    protected List<BigInteger> om;
    protected List<Attribute> atr;
    protected List<BigInteger> net;

    /**
     * Gets the value of the crb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrb() {
        return crb;
    }

    /**
     * Sets the value of the crb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrb(String value) {
        this.crb = value;
    }

    /**
     * Gets the value of the cra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCra() {
        return cra;
    }

    /**
     * Sets the value of the cra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCra(String value) {
        this.cra = value;
    }

    /**
     * Gets the value of the ms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMs() {
        return ms;
    }

    /**
     * Sets the value of the ms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMs(String value) {
        this.ms = value;
    }

    /**
     * Gets the value of the us property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUs() {
        return us;
    }

    /**
     * Sets the value of the us property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUs(String value) {
        this.us = value;
    }

    /**
     * Gets the value of the sts property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSts() {
        return sts;
    }

    /**
     * Sets the value of the sts property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSts(BigInteger value) {
        this.sts = value;
    }

    /**
     * Gets the value of the stb property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStb() {
        return stb;
    }

    /**
     * Sets the value of the stb property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStb(BigInteger value) {
        this.stb = value;
    }

    /**
     * Gets the value of the exb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExb() {
        return exb;
    }

    /**
     * Sets the value of the exb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExb(String value) {
        this.exb = value;
    }

    /**
     * Gets the value of the exa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExa() {
        return exa;
    }

    /**
     * Sets the value of the exa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExa(String value) {
        this.exa = value;
    }

    /**
     * Gets the value of the sza property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSza() {
        return sza;
    }

    /**
     * Sets the value of the sza property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSza(BigInteger value) {
        this.sza = value;
    }

    /**
     * Gets the value of the szb property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSzb() {
        return szb;
    }

    /**
     * Sets the value of the szb property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSzb(BigInteger value) {
        this.szb = value;
    }

    /**
     * Gets the value of the om property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the om property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getOm() {
        if (om == null) {
            om = new ArrayList<BigInteger>();
        }
        return this.om;
    }

    /**
     * Gets the value of the atr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAtr() {
        if (atr == null) {
            atr = new ArrayList<Attribute>();
        }
        return this.atr;
    }

    /**
     * Gets the value of the net property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the net property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getNet() {
        if (net == null) {
            net = new ArrayList<BigInteger>();
        }
        return this.net;
    }

}
