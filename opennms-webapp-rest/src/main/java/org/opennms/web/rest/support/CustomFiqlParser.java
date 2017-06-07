/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.web.rest.support;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.search.Beanspector;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.opennms.core.utils.InetAddressUtils;

import com.google.common.base.Strings;

/**
 * This FiqlParser is a custom version of the original {@link FiqlParser} from apache cxf.
 *
 * The default implementation lacks the possibility to map string values to certain types before setting them on the search bean, e.g. String conversion to InetAddress.
 * The custom implementation should address this issue.
 *
 * @param <T>
 * @author mvrueden
 */
public class CustomFiqlParser<T> extends FiqlParser<T> {

    interface TypeMapper<T> {
        T map(String value);
    }

    private final Map<String, TypeMapper<?>> customMappers = new HashMap<>();

    public CustomFiqlParser(Class<T> tclass, Map<String, String> contextProperties, Map<String, String> beanProperties) {
        super(tclass, contextProperties, beanProperties);

        // Add mapping from String to InetAddress
        this.customMappers.put(InetAddress.class.getName(), value -> {
            if (value == null || Strings.isNullOrEmpty(value)) {
                return null;
            }
            return InetAddressUtils.getInetAddress(value);
        });
    }

    @Override
    protected Object parseType(String originalPropName, Object ownerBean, Object lastCastedValue, String setter, Beanspector.TypeInfo typeInfo, String value) throws SearchParseException {
        // Custom mapper
        final TypeMapper<?> typeMapper = customMappers.get(typeInfo.getTypeClass().getName());
        if (typeMapper != null) {
            if (ownerBean != null) {
                throw new IllegalStateException("You cannot provide a ownerBean and have a typeMapper defined. Something is wrong.");
            }
            return typeMapper.map(value);
        }
        // No custom mapping, fall back to default behaviour
        return super.parseType(originalPropName, ownerBean, lastCastedValue, setter, typeInfo, value);
    }
}
