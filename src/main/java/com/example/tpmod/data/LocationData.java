package com.example.tpmod.data;

import net.minecraft.nbt.CompoundTag;

/**
 * LocationData 类用于存储和管理一个三维坐标点及其所在维度的信息。
 * 提供了将数据序列化为NBT格式和从NBT反序列化的方法，便于数据持久化和网络传输。
 */
public class LocationData {
    // 维度名称
    private final String dimension;
    // 坐标X
    private final int x;
    // 坐标Y
    private final int y;
    // 坐标Z
    private final int z;

    /**
     * 构造方法，初始化维度和坐标。
     * @param dimension 维度名称
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     */
    public LocationData(String dimension, int x, int y, int z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * 获取维度名称。
     * @return 维度名称
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * 获取X坐标。
     * @return X坐标
     */
    public int getX() {
        return x;
    }

    /**
     * 获取Y坐标。
     * @return Y坐标
     */
    public int getY() {
        return y;
    }

    /**
     * 获取Z坐标。
     * @return Z坐标
     */
    public int getZ() {
        return z;
    }

    /**
     * 将当前对象序列化为NBT格式。
     * @return 包含位置数据的CompoundTag
     */
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("dimension", dimension);
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);
        return nbt;
    }

    /**
     * 从NBT数据反序列化为LocationData对象。
     * @param nbt 包含位置数据的CompoundTag
     * @return 反序列化后的LocationData对象
     */
    public static LocationData fromNBT(CompoundTag nbt) {
        return new LocationData(
                nbt.getString("dimension"),
                nbt.getInt("x"),
                nbt.getInt("y"),
                nbt.getInt("z"));
    }
}