/*
 *     This file is part of NyxEffect.
 *
 *     NyxEffect is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NyxEffect is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NyxEffect.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Copyright (c) POQDavid <https://github.com/poqdavid/NyxEffect>
 *     Copyright (c) contributors
 */

package io.github.poqdavid.nyx.nyxeffect.Utils.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class EffectsData implements Serializable {

    private final static long serialVersionUID = -8907830139865507463L;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("interval")
    @Expose
    private Integer interval;
    @SerializedName("ParticleDataList")
    @Expose
    private List<ParticleDataList> particleDataList = null;

    /**
     * No args constructor for use in serialization
     */
    public EffectsData() {
    }

    /**
     * @param id
     * @param particleDataList
     * @param name
     * @param interval
     */
    public EffectsData(String name, String id, Integer interval, List<ParticleDataList> particleDataList) {
        super();
        this.name = name;
        this.id = id;
        this.particleDataList = particleDataList;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EffectsData withName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EffectsData withId(String id) {
        this.id = id;
        return this;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public EffectsData withInterval(Integer interval) {
        this.interval = interval;
        return this;
    }

    public List<ParticleDataList> getParticleDataList() {
        return particleDataList;
    }

    public void setParticleDataList(List<ParticleDataList> particleDataList) {
        this.particleDataList = particleDataList;
    }

    public EffectsData withParticleDataList(List<ParticleDataList> particleDataList) {
        this.particleDataList = particleDataList;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(id).append(interval).append(particleDataList).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof EffectsData) == false) {
            return false;
        }
        EffectsData rhs = ((EffectsData) other);
        return new EqualsBuilder().append(name, rhs.name).append(id, rhs.id).append(interval, rhs.interval).append(particleDataList, rhs.particleDataList).isEquals();
    }

}
