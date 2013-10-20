package com.github.jdubois.responses.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.OpenBitSet;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

import java.io.IOException;

/**
 * @author Julien Dubois
 */
public class InstanceFilter extends org.apache.lucene.search.Filter {

    private static final long serialVersionUID = -5733154197336767101L;
    private String instanceId;

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Key
    public FilterKey getInstanceId() {
        StandardFilterKey key = new StandardFilterKey();
        key.addParameter(instanceId);
        return key;
    }

    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        OpenBitSet bitSet = new OpenBitSet(reader.maxDoc());
        TermDocs termDocs = reader.termDocs(new Term("instance.id", instanceId));
        while (termDocs.next()) {
            bitSet.set(termDocs.doc());
        }
        return bitSet;
    }
}


