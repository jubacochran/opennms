/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2014 The OpenNMS Group, Inc.
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

package org.opennms.web.rest.v2;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.opennms.core.config.api.JaxbListWrapper;
import org.opennms.core.criteria.Alias.JoinType;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.netmgt.dao.api.MonitoringLocationDao;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.events.api.EventProxy;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;
import org.opennms.netmgt.model.events.EventUtils;
import org.opennms.netmgt.model.monitoringLocations.OnmsMonitoringLocation;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.web.api.RestUtils;
import org.opennms.web.rest.support.MultivaluedMapImpl;
import org.opennms.web.rest.support.RedirectHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic Web Service using REST for {@link OnmsNode} entity
 *
 * @author <a href="seth@opennms.org">Seth Leger</a>
 * @author <a href="agalue@opennms.org">Alejandro Galue</a>
 */
@Component
@Path("nodes")
@Transactional
public class NodeRestService extends AbstractDaoRestService<OnmsNode,Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(NodeRestService.class);

    @Autowired
    private MonitoringLocationDao m_locationDao;

    @Autowired
    private NodeDao m_dao;

    @Autowired
    @Qualifier("eventProxy")
    private EventProxy m_eventProxy;

    protected NodeDao getDao() {
        return m_dao;
    }

    protected Class<OnmsNode> getDaoClass() {
        return OnmsNode.class;
    }

    protected CriteriaBuilder getCriteriaBuilder(UriInfo uriInfo) {
        final CriteriaBuilder builder = new CriteriaBuilder(OnmsNode.class);

        builder.alias("snmpInterfaces", "snmpInterface", JoinType.LEFT_JOIN);
        builder.alias("ipInterfaces", "ipInterface", JoinType.LEFT_JOIN);
        builder.alias("categories", "category", JoinType.LEFT_JOIN);
        builder.alias("assetRecord", "assetRecord", JoinType.LEFT_JOIN);
        builder.alias("ipInterfaces.monitoredServices.serviceType", "serviceType", JoinType.LEFT_JOIN);

        // Order by label by default
        builder.orderBy("label").desc();

        return builder;
    }

    @Override
    protected JaxbListWrapper<OnmsNode> createListWrapper(Collection<OnmsNode> list) {
        return new OnmsNodeList(list);
    }

    @Override
    public Response doCreate(final UriInfo uriInfo, final OnmsNode object) {
        if (object.getLocation() == null) {
            OnmsMonitoringLocation location = m_locationDao.getDefaultLocation();
            LOG.debug("addNode: Assigning new node to default location: {}", location.getLocationName());
            object.setLocation(location);
        }
        Integer id = getDao().save(object);
        final Event e = EventUtils.createNodeAddedEvent("Rest", id, object.getLabel(), object.getLabelSource());
        sendEvent(e);

        return Response.created(RedirectHelper.getRedirectUri(uriInfo, id)).build();
    }

    // Overrides default implementation
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML})
    public Response getByCriteria(@Context final UriInfo uriInfo, @PathParam("id") final String lookupCriteria) {
        LOG.debug("getByCriteria: getting node using '{}' as criteria", lookupCriteria);
        OnmsNode retval = getNode(lookupCriteria);
        if (retval == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(retval).build();
        }
    }

    // Overrides default implementation
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{id}")
    public Response update(@Context final UriInfo uriInfo, @PathParam("id") final String lookupCriteria, final OnmsNode object) {
        writeLock();
        try {
            if (object == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            OnmsNode retval = getNode(lookupCriteria);
            if (retval == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            if (!retval.getId().equals(object.getId())) {
                return Response.status(Status.NOT_FOUND).build();
            }
            LOG.debug("update: updating object {}", object);
            getDao().saveOrUpdate(object);
            return Response.noContent().build();
        } finally {
            writeUnlock();
        }
    }

    // Overrides default implementation
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{id}")
    public Response updateProperties(@Context final UriInfo uriInfo, @PathParam("id") final String lookupCriteria, final MultivaluedMapImpl params) {
        writeLock();
        try {
            OnmsNode object = getNode(lookupCriteria);
            if (object == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            LOG.debug("update: updating object {}", object);
            RestUtils.setBeanProperties(object, params);
            LOG.debug("update: object {} updated", object);
            getDao().saveOrUpdate(object);
            return Response.noContent().build();
        } finally {
            writeUnlock();
        }
    }

    // Overrides default implementation
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String lookupCriteria) {
        writeLock();
        try {
            final OnmsNode object = getNode(lookupCriteria);
            if (object == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            LOG.debug("delete: deleting object {}", lookupCriteria);
            getDao().delete(object);
            return Response.noContent().build();
        } finally {
            writeUnlock();
        }
    }

    @Path("{lookupCriteria}/ipinterfaces")
    public NodeIpInterfacesRestService getIpInterfaceResource(@Context final ResourceContext context) {
        return context.getResource(NodeIpInterfacesRestService.class);
    }

    @Path("{lookupCriteria}/snmpinterfaces")
    public NodeSnmpInterfacesRestService getSnmpInterfaceResource(@Context final ResourceContext context) {
        return context.getResource(NodeSnmpInterfacesRestService.class);
    }

    private OnmsNode getNode(final String lookupCriteria) {
        return getDao().get(lookupCriteria);
    }

}
