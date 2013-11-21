// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MapQueryResult<String,Object> implements Iterable<Map.Entry<String,Object>>, QueryResult  {

	private Map<String,Object> map;

    // ------------------------------------------------------------------------

    protected MapQueryResult() {
        /* Jackson */
    }

	protected MapQueryResult(final Map<String,Object> map) {
		this.map = checkNotNull(map);
	}

    // ------------------------------------------------------------------------

    public void put(final String key, Object value) {
        if (null == map) {
            map = Maps.newHashMap();
        }
        map.put(key,value);
    }
    public void put(Map.Entry<String,Object> entry) {
        put(entry.getKey(), entry.getValue());
    }

	@Override
	public Iterator<Map.Entry<String,Object>> iterator() {
		return this.map.entrySet().iterator();
	}

    // ------------------------------------------------------------------------

	public int getCount() {
		return this.map.size();
	}

	public Map<String,Object> getMap() {
		return this.map;
	}

    public void setMap(final Map<String,Object> map) {
        this.map= checkNotNull(map);
    }



    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapQueryResult that = (MapQueryResult) o;

        if (!map.equals(that.map)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return Objects.toStringHelper(this)
                .add("map", map)
                .toString();
    }

}
