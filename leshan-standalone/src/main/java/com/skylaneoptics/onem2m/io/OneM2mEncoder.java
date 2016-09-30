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
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * OneM2M encoder to convert an oneM2M object to its string representation
 * 
 * @author Pierre Saelens
 *
 */
public class OneM2mEncoder {

	private static final Log LOG = LogFactory.getLog(OneM2mEncoder.class);

	/**
	 * Method not allowed.
	 */
	private OneM2mEncoder(){
		// Not allowed
	}
	
	/**
	 * Convert an oneM2M java object to its string representation
	 * 
	 * @param obj
	 *            The object to convert
	 * @return the String representation of the object
	 */
	public static String toString(Resource obj) {
		try {
			Marshaller marshaller = OneM2mMapper.getInstance().getJAXBContext()
					.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream outputStream = new ByteArrayOutputStream();
			marshaller.marshal(obj, outputStream);
			return outputStream.toString();
		} catch (JAXBException e) {
			LOG.error("Error in encoding oneM2M resource", e);
		}
		return null;
	}

}
