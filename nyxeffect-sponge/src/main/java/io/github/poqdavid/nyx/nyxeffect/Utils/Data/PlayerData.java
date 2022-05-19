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

public class PlayerData implements Serializable {

    private final static long serialVersionUID = -2619153589713477580L;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("onmove")
    @Expose
    private Boolean onmove;

    /**
     * No args constructor for use in serialization
     */
    public PlayerData() {
    }

    /**
     * @param name
     * @param onmove
     */
    public PlayerData(String name, Boolean onmove) {
        super();
        this.name = name;
        this.onmove = onmove;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerData withName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getOnmove() {
        return onmove;
    }

    public void setOnmove(Boolean onmove) {
        this.onmove = onmove;
    }

    public PlayerData withOnmove(Boolean onmove) {
        this.onmove = onmove;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(onmove).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PlayerData)) {
            return false;
        }
        PlayerData rhs = ((PlayerData) other);
        return new EqualsBuilder().append(name, rhs.name).append(onmove, rhs.onmove).isEquals();
    }

}
