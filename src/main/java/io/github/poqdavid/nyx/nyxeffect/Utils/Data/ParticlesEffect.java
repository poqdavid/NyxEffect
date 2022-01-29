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

public class ParticlesEffect implements Serializable {

    private final static long serialVersionUID = -6133073363102734805L;
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("event")
    @Expose
    private String event;

    @SerializedName("relatedlocation")
    @Expose
    private Boolean relatedlocation;

    @SerializedName("relatedrotation")
    @Expose
    private Boolean relatedrotation;

    @SerializedName("cleartime")
    @Expose
    private long cleartime;

    @SerializedName("data")
    @Expose
    private String data;

    /**
     * No args constructor for use in serialization
     */
    public ParticlesEffect() {
    }

    /**
     * @param event
     * @param relatedrotation
     * @param data
     * @param relatedlocation
     * @param cleartime
     * @param type
     */
    public ParticlesEffect(String type, String event, Boolean relatedlocation, Boolean relatedrotation, long cleartime, String data) {
        super();
        this.type = type;
        this.event = event;
        this.relatedlocation = relatedlocation;
        this.relatedrotation = relatedrotation;
        this.cleartime = cleartime;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ParticlesEffect withType(String type) {
        this.type = type;
        return this;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public ParticlesEffect withEvent(String event) {
        this.event = event;
        return this;
    }

    public Boolean getRelatedlocation() {
        return relatedlocation;
    }

    public void setRelatedlocation(Boolean relatedlocation) {
        this.relatedlocation = relatedlocation;
    }

    public ParticlesEffect withRelatedlocation(Boolean relatedlocation) {
        this.relatedlocation = relatedlocation;
        return this;
    }

    public Boolean getRelatedrotation() {
        return relatedrotation;
    }

    public void setRelatedrotation(Boolean relatedrotation) {
        this.relatedrotation = relatedrotation;
    }

    public ParticlesEffect withRelatedrotation(Boolean relatedrotation) {
        this.relatedrotation = relatedrotation;
        return this;
    }

    public long getcleartime() {
        return cleartime;
    }

    public void setcleartime(long cleartime) {
        this.cleartime = cleartime;
    }

    public ParticlesEffect withcleartime(long cleartime) {
        this.cleartime = cleartime;
        return this;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ParticlesEffect withData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(event).append(relatedlocation).append(relatedrotation).append(cleartime).append(data).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ParticlesEffect) == false) {
            return false;
        }
        ParticlesEffect rhs = ((ParticlesEffect) other);
        return new EqualsBuilder().append(type, rhs.type).append(event, rhs.event).append(relatedlocation, rhs.relatedlocation).append(relatedrotation, rhs.relatedrotation).append(cleartime, rhs.cleartime).append(data, rhs.data).isEquals();
    }

}
