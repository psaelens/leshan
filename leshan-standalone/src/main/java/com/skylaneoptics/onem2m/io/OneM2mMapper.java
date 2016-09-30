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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Mapper class to convert oneM2M XML representation to java object or vice-versa
 * 
 * @author Pierre Saelens
 *
 */
public class OneM2mMapper {
	/** Logger */
	private static final Log LOGGER = LogFactory.getLog(OneM2mMapper.class);
	/** Current instance of the mapper */
	private static OneM2mMapper oneM2mMapper;
	/** JAXB Context of oneM2M objects */
	private JAXBContext context;
	
	/** Package containing oneM2M objects */
	private static final String PACKAGE = "org.onem2m.xml.protocols";

	private OneM2mMapper() {
		try {
			context = JAXBContext.newInstance(PACKAGE);
		} catch (JAXBException e) {
			LOGGER.error("Error creating the JAXB context for oneM2M resources", e);
		}
	}

	protected JAXBContext getJAXBContext() {
		return context;
	}

	/**
	 * Return the current instance of the mapper
	 * 
	 * @return current instance of OneM2mMapper
	 */
	public static OneM2mMapper getInstance() {
		if(oneM2mMapper == null){
			oneM2mMapper = new OneM2mMapper();
		}
		return oneM2mMapper;
	}

}
