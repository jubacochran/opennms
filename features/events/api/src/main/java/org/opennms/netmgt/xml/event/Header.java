/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id$
 */

package org.opennms.netmgt.xml.event;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Class Header.
 * 
 * @version $Revision$ $Date$
 */

@XmlRootElement(name="header")
@XmlAccessorType(XmlAccessType.FIELD)
//@ValidateUsing("event.xsd")
public class Header implements Serializable {
	private static final long serialVersionUID = 1963826810463701325L;

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

	/**
     * Field _ver.
     */
	@XmlElement(name="ver")
    private java.lang.String _ver;

    /**
     * Field _dpName.
     */
	@XmlElement(name="dpName")
    private java.lang.String _dpName;

    /**
     * The time at which this log was generated. The time
     *  is in the format generated by the java.text.DateFormat
     * using the
     *  DateFormat.FULL style for the default locale. For example:
     *  "Monday, February 18, 2002 3:01:58 PM EST"
     */
	@XmlElement(name="created")
    private java.lang.String _created;

    /**
     * Field _mstation.
     */
	@XmlElement(name="mstation")
    private java.lang.String _mstation;


      //----------------/
     //- Constructors -/
    //----------------/

    public Header() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'created'. The field 'created'
     * has the following description: The time at which this log
     * was generated. The time
     *  is in the format generated by the java.text.DateFormat
     * using the
     *  DateFormat.FULL style for the default locale. For example:
     *  "Monday, February 18, 2002 3:01:58 PM EST"
     * 
     * @return the value of field 'Created'.
     */
    public java.lang.String getCreated(
    ) {
        return this._created;
    }

    /**
     * Returns the value of field 'dpName'.
     * 
     * @return the value of field 'DpName'.
     */
    public java.lang.String getDpName(
    ) {
        return this._dpName;
    }

    /**
     * Returns the value of field 'mstation'.
     * 
     * @return the value of field 'Mstation'.
     */
    public java.lang.String getMstation(
    ) {
        return this._mstation;
    }

    /**
     * Returns the value of field 'ver'.
     * 
     * @return the value of field 'Ver'.
     */
    public java.lang.String getVer(
    ) {
        return this._ver;
    }

    /**
     * Sets the value of field 'created'. The field 'created' has
     * the following description: The time at which this log was
     * generated. The time
     *  is in the format generated by the java.text.DateFormat
     * using the
     *  DateFormat.FULL style for the default locale. For example:
     *  "Monday, February 18, 2002 3:01:58 PM EST"
     * 
     * @param created the value of field 'created'.
     */
    public void setCreated(
            final java.lang.String created) {
        this._created = created;
    }

    /**
     * Sets the value of field 'dpName'.
     * 
     * @param dpName the value of field 'dpName'.
     */
    public void setDpName(
            final java.lang.String dpName) {
        this._dpName = dpName;
    }

    /**
     * Sets the value of field 'mstation'.
     * 
     * @param mstation the value of field 'mstation'.
     */
    public void setMstation(
            final java.lang.String mstation) {
        this._mstation = mstation;
    }

    /**
     * Sets the value of field 'ver'.
     * 
     * @param ver the value of field 'ver'.
     */
    public void setVer(
            final java.lang.String ver) {
        this._ver = ver;
    }

        @Override
    public String toString() {
    	return new ToStringBuilder(this)
    		.append("ver", _ver)
    		.append("dpName", _dpName)
    		.append("created", _created)
    		.append("mstation", _mstation)
    		.toString();
    }
}