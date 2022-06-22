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

public class ParticlesData implements Serializable {

    private final static long serialVersionUID = 9032544941315713483L;
    @SerializedName("EffectsData")
    @Expose
    private EffectsData effectsData;

    /**
     * No args constructor for use in serialization
     */
    public ParticlesData() {
    }

    /**
     * @param effectsData
     */
    public ParticlesData(EffectsData effectsData) {
        super();
        this.effectsData = effectsData;
    }

    public EffectsData getEffectsData() {
        return effectsData;
    }

    public void setEffectsData(EffectsData effectsData) {
        this.effectsData = effectsData;
    }

    public ParticlesData withEffectsData(EffectsData effectsData) {
        this.effectsData = effectsData;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(effectsData).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ParticlesData) == false) {
            return false;
        }
        ParticlesData rhs = ((ParticlesData) other);
        return new EqualsBuilder().append(effectsData, rhs.effectsData).isEquals();
    }

}