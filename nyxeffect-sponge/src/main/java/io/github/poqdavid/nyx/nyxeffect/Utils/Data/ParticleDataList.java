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

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class ParticleDataList implements Serializable {

    private final static long serialVersionUID = -8722004533973523270L;
    @SerializedName("ParticlesEffect")
    @Expose
    private ParticlesEffect particleEffect;
    @SerializedName("Vector3d")
    @Expose
    private Vector3d vector3d;

    /**
     * No args constructor for use in serialization
     */
    public ParticleDataList() {
    }

    /**
     * @param particleEffect
     * @param vector3d
     */
    public ParticleDataList(ParticlesEffect particleEffect, Vector3d vector3d) {
        super();
        this.particleEffect = particleEffect;
        this.vector3d = vector3d;
    }

    public ParticlesEffect getParticleEffect() {
        return particleEffect;
    }

    public void setParticleEffect(ParticlesEffect particleEffect) {
        this.particleEffect = particleEffect;
    }

    public ParticleDataList withParticleEffect(ParticlesEffect particleEffect) {
        this.particleEffect = particleEffect;
        return this;
    }

    public Vector3d getVector3d() {
        return vector3d;
    }

    public void setVector3d(Vector3d vector3d) {
        this.vector3d = vector3d;
    }

    public ParticleDataList withVector3d(Vector3d vector3d) {
        this.vector3d = vector3d;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(particleEffect).append(vector3d).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ParticleDataList) == false) {
            return false;
        }
        ParticleDataList rhs = ((ParticleDataList) other);
        return new EqualsBuilder().append(particleEffect, rhs.particleEffect).append(vector3d, rhs.vector3d).isEquals();
    }

}
