/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package hr.caellian.m3l.util.nbt;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtTagCompound;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * {@link NbtSerializable} is capable of storing any serializable object. It is based on {@link NbtByteArray} and
 * externally stores objects as {@link NbtByteArray} tags.
 * <p/>
 * {@link NbtSerializable} is not capable of holding objects or pointers to them. When {@link NbtSerializable} is
 * initialized, given object is converted/serialized into byte array.
 * <p/>
 * {@link NbtSerializable} implements {@link Serializable} and can thus be nested but it will only increase tag size
 * and make code harder to read. It makes no sense, it's bad, don't do it.
 *
 * @author Caellian
 * @see NbtByteArray
 * @see Serializable
 */
public class NbtSerializable extends NbtByteArray implements Cloneable, Serializable {
    /**
     * Constructor used to initialize a new serializable tag.<br>
     * <p/>
     * <b>Usage example:</b><br>
     * {@code NbtSerializable<ArrayListMultimap<String, Block>> arrayListMultimapTag  = new
     * NbtSerializable<ArrayListMultimap<String, Block>>(multimapObject)}<br>
     * Where {@code multimapObject} is instance of {@link com.google.common.collect.ArrayListMultimap}
     *
     * @param serializableObject Object that is going to be serialized
     */
    public NbtSerializable(Serializable serializableObject) {
        super(SerializationUtils.serialize(serializableObject));
    }

    /**
     * This constructor is used to retrieve {@link NbtSerializable} object from byte[].
     * This constrictor was created as a work around for NBT format being unable to return instances of unidentified
     * classes.
     *
     * @param serializedData Object transferred to {@link NbtByteArray#getDataValue()}
     */
    private NbtSerializable(byte[] serializedData) {
        super(serializedData);
    }

    /**
     * Reads {@link NbtSerializable} object from given {@link NbtTagCompound}.
     *
     * @param parentTagCompound Tag compound containing wanted object.
     * @param objectID          ID of the wanted object.
     * @return NBT object stored within <i>parentTagCompound</i> under <i>objectID</i> tag.
     */
    public static NbtSerializable getSerializable(NbtTagCompound parentTagCompound, String objectID) {
        return new NbtSerializable(parentTagCompound.getAsByteArray(objectID));
    }

    /**
     * @return Id of this {@link net.minecraft.nbt.Nbt} used to identify NBT element type on reading NBT files.
     */
    public byte getId() {
        return (byte) 7;
    }

    /**
     * Creates a new {@link NbtSerializable} object containing data of this object.
     */
    public NbtSerializable copy() {
        byte[] clonedBuffer = new byte[this.getDataValue().length];
        System.arraycopy(this.getDataValue(), 0, clonedBuffer, 0, this.getDataValue().length);
        return new NbtSerializable(clonedBuffer);
    }

    /**
     * @param other Object to compare this object with.
     * @return True if the objects contain equal data, false if not.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NbtSerializable)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }

        NbtSerializable that = (NbtSerializable) other;

        return Arrays.equals(getDataValue(), that.getDataValue());

    }

    /**
     * @return Object hashcode.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.getDataValue());
        return result;
    }

    /**
     * This method deserializes stored object and returns it.
     *
     * @return Object version of serialized data.
     */
    @SuppressWarnings("unchecked")
    public Object getObject() {
        return SerializationUtils.deserialize(this.getDataValue());
    }
}
