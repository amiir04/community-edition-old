/*
 * #%L
 * Alfresco Solr 4
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.solr.query;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.*;
import org.apache.solr.search.DelegatingCollector;

public class AllAccessCollector extends DelegatingCollector
{
    public boolean acceptsDocsOutOfOrder()
    {
        return false;
    }

    public void setNextReader(AtomicReaderContext context) throws IOException
    {
        delegate.setNextReader(context);
    }

    public void setScorer(Scorer scorer) throws IOException
    {
        delegate.setScorer(scorer);
    }

    public void collect(int doc) throws IOException
    {
        delegate.collect(doc);
    }
}
