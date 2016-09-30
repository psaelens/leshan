/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
package com.skylaneoptics.onem2m.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onem2m.xml.protocols.Resource;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * oneM2M decoder to obtain the javaobject corresponding to an oneM2M XML
 * representation.
 * 
 * @author Pierre Saelens
 *
 */
public class OneM2mDecoder {

	private static final Log LOG = LogFactory.getLog(OneM2mDecoder.class);
	
	/**
	 * Method not allowed
	 */
	private OneM2mDecoder(){
		// Not allowed
	}

	/**
	 * Get the java object from the oneM2M XML representation
	 * 
	 * @param representation
	 *            Sting from the oBIX object
	 * @return the oBIX object decoded
	 */
	public static Resource fromString(String representation) {
		StringReader stringReader = new StringReader(representation);
		try {
			Unmarshaller unmarshaller = OneM2mMapper.getInstance()
					.getJAXBContext().createUnmarshaller();
			return (Resource) unmarshaller.unmarshal(stringReader);
		} catch (JAXBException e) {
			LOG.error("Error in decoding oneM2M resource", e);
		}
		return null;
	}
}
