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

package org.opennms.netmgt.provision.service.vmware;

import java.util.Map;

import org.opennms.netmgt.provision.persist.ForeignSourceRepository;
import org.opennms.netmgt.provision.persist.RequisitionProvider;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Generates requisition for Vmware entities.
 *
 * See {@link VmwareImportRequest} for all supported options.
 *
 * @author jwhite
 */
public class VmwareRequisitionProvider implements RequisitionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(VmwareRequisitionProvider.class);

    @Autowired
    @Qualifier("fileDeployed")
    private ForeignSourceRepository foreignSourceRepository;

    @Override
    public Requisition getRequisition(Map<String, String> parameters) {
        // Generate a request using the parameter map
        final VmwareImportRequest request = new VmwareImportRequest(parameters);
        // Lookup the existing requisition, and store it in the request
        final Requisition existingRequisition = getExistingRequisition(request.getForeignSource());
        request.setExistingRequisition(existingRequisition);
        // Execute the request
        return getRequisition(request);
    }

    public Requisition getRequisition(VmwareImportRequest request) {
        final VmwareImporter importer = new VmwareImporter(request);
        return importer.getRequisition();
    }

    protected Requisition getExistingRequisition(String foreignSource) {
        try {
            return foreignSourceRepository.getRequisition(foreignSource);
        } catch (Exception e) {
            LOG.warn("Can't retrieve requisition {}", foreignSource, e);
            return null;
        }
    }

}