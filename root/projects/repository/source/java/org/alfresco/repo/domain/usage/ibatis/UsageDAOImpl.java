/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.domain.usage.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.usage.AbstractUsageDAOImpl;
import org.alfresco.repo.domain.usage.UsageDeltaEntity;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * iBatis-specific implementation of the Usage DAO.
 * 
 * @author janv
 * @since 3.4
 */
public class UsageDAOImpl extends AbstractUsageDAOImpl
{
    private static Log logger = LogFactory.getLog(UsageDAOImpl.class);
    
    private static final String INSERT_USAGE_DELTA = "alfresco.usage.insert_UsageDelta";
    private static final String SELECT_USAGE_DELTA_TOTAL_SIZE_BY_NODE = "alfresco.usage.select_GetTotalDeltaSizeByNodeId";
    private static final String SELECT_USAGE_DELTA_NODES = "alfresco.usage.select_GetUsageDeltaNodes";
    private static final String SELECT_USERS_WITH_USAGE = "alfresco.usage.select_GetUsersWithUsage";
    private static final String SELECT_USERS_WITHOUT_USAGE = "alfresco.usage.select_GetUsersWithoutUsage";
    private static final String SELECT_CONTENT_SIZES_NEW = "alfresco.usage.select_GetContentSizesForStoreNew";
    private static final String DELETE_USAGE_DELTAS_BY_NODE = "alfresco.usage.delete_UsageDeltasByNodeId";
    
    
    private SqlMapClientTemplate template;
    private QNameDAO qnameDAO;
    
    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate)
    {
        this.template = sqlMapClientTemplate;
    }
    
    public void setQnameDAO(QNameDAO qnameDAO)
    {
        this.qnameDAO = qnameDAO;
    }
    
    @Override
    protected UsageDeltaEntity insertUsageDeltaEntity(UsageDeltaEntity entity)
    {
        entity.setVersion(0L);
        Long id = (Long)template.insert(INSERT_USAGE_DELTA, entity);
        entity.setId(id);
        return entity;
    }
    
    @Override
    protected UsageDeltaEntity selectTotalUsageDeltaSize(long nodeEntityId)
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("id", nodeEntityId);
        
        return (UsageDeltaEntity) template.queryForObject(SELECT_USAGE_DELTA_TOTAL_SIZE_BY_NODE, params);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<Long> selectUsageDeltaNodes()
    {
        return (List<Long>) template.queryForList(SELECT_USAGE_DELTA_NODES);
    }
    
    @Override
    protected int deleteUsageDeltaEntitiesByNodeId(long nodeEntityId)
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("id", nodeEntityId);
        
        return template.delete(DELETE_USAGE_DELTAS_BY_NODE, params);
    }
    
    @Override
    protected void selectUsersWithoutUsage(StoreRef storeRef, MapHandler resultsCallback)
    {
        long personTypeQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.TYPE_PERSON).getFirst();
        long usernamePropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_USERNAME).getFirst();
        long sizeCurrentPropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_SIZE_CURRENT).getFirst();
        
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("personTypeQNameID", personTypeQNameEntityId); // cm:person (type)
        params.put("usernamePropQNameID", usernamePropQNameEntityId); // cm:username (prop)
        params.put("sizeCurrentPropQNameID", sizeCurrentPropQNameEntityId); // cm:sizeCurrent (prop)
        params.put("storeProtocol", storeRef.getProtocol());
        params.put("storeIdentifier", storeRef.getIdentifier());
        
        MapRowHandler rowHandler = new MapRowHandler(resultsCallback);
        
        template.queryWithRowHandler(SELECT_USERS_WITHOUT_USAGE, params, rowHandler);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("   Listed " + rowHandler.total + " users without usage");
        }
    }
    
    @Override
    protected void selectUsersWithUsage(StoreRef storeRef, MapHandler resultsCallback)
    {
        long personTypeQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.TYPE_PERSON).getFirst();
        long usernamePropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_USERNAME).getFirst();
        long sizeCurrentPropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_SIZE_CURRENT).getFirst();
        
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("personTypeQNameID", personTypeQNameEntityId); // cm:person (type)
        params.put("usernamePropQNameID", usernamePropQNameEntityId); // cm:username (prop)
        params.put("sizeCurrentPropQNameID", sizeCurrentPropQNameEntityId); // cm:sizeCurrent (prop)
        params.put("storeProtocol", storeRef.getProtocol());
        params.put("storeIdentifier", storeRef.getIdentifier());
        
        MapRowHandler rowHandler = new MapRowHandler(resultsCallback);
        
        template.queryWithRowHandler(SELECT_USERS_WITH_USAGE, params, rowHandler);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("   Listed " + rowHandler.total + " users with usage");
        }
    }
    
    @Override
    protected void selectUserContentSizesForStore(StoreRef storeRef, MapHandler resultsCallback)
    {
        long contentTypeQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.TYPE_CONTENT).getFirst();
        long ownerPropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_OWNER).getFirst();
        long contentPropQNameEntityId = qnameDAO.getOrCreateQName(ContentModel.PROP_CONTENT).getFirst();
        
        MapRowHandler rowHandler = new MapRowHandler(resultsCallback);
        
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("contentTypeQNameID", contentTypeQNameEntityId); // cm:content (type)
        params.put("ownerPropQNameID", ownerPropQNameEntityId); // cm:owner (prop)
        params.put("contentPropQNameID", contentPropQNameEntityId); // cm:content (prop)
        params.put("storeProtocol", storeRef.getProtocol());
        params.put("storeIdentifier", storeRef.getIdentifier());
        
        // Query for the 'new' (FK) style content data properties (stored in 'string_value')
        template.queryWithRowHandler(SELECT_CONTENT_SIZES_NEW, params, rowHandler);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("   Listed " + rowHandler.total + " old content sizes");
        }
    }
    
    /**
     * Row handler for getting map of strings
     */
    private static class MapRowHandler implements RowHandler
    {
        private final MapHandler handler;
        
        private int total = 0;
        
        private MapRowHandler(MapHandler handler)
        {
            this.handler = handler;
        }
        
        @SuppressWarnings("unchecked")
        public void handleRow(Object valueObject)
        {
            handler.handle((Map<String, Object>)valueObject);
            total++;
            if (logger.isDebugEnabled() && (total == 0 || (total % 1000 == 0) ))
            {
                logger.debug("   Listed " + total + " map entries");
            }
        }
    }
}
