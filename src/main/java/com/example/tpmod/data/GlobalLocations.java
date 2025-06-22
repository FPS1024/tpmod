package com.example.tpmod.data;

import com.example.tpmod.TPMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局传送点数据存储类。
 * 负责在服务端保存、加载、管理所有全局传送点（名称->坐标）。
 * 继承自 SavedData，自动随世界存档持久化。
 */
public class GlobalLocations extends SavedData {
    // 存储所有全局传送点，key为名称，value为位置数据
    private final Map<String, LocationData> locations = new HashMap<>();

    public GlobalLocations() {
        super();
    }

    /**
     * 从NBT数据加载全局传送点。
     * @param tag 存档NBT
     * @param provider 数据修复器
     * @return 加载后的GlobalLocations对象
     */
    public static GlobalLocations load(CompoundTag tag, HolderLookup.Provider provider) {
        GlobalLocations savedData = new GlobalLocations();
        CompoundTag locationsTag = tag.getCompound("locations");
        for (String name : locationsTag.getAllKeys()) {
            savedData.locations.put(name, LocationData.fromNBT(locationsTag.getCompound(name)));
        }
        return savedData;
    }

    /**
     * 将全局传送点保存为NBT。
     * @param compoundTag 目标NBT
     * @param provider 数据修复器
     * @return 保存后的NBT
     */
    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag locationsTag = new CompoundTag();
        locations.forEach((name, loc) -> locationsTag.put(name, loc.toNBT()));
        compoundTag.put("locations", locationsTag);
        return compoundTag;
    }

    /**
     * 获取当前世界的全局传送点数据（仅限服务端）。
     * @param level 世界对象
     * @return GlobalLocations实例
     */
    public static GlobalLocations get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new RuntimeException("Cannot get locations on client side!");
        }
        DimensionDataStorage storage = serverLevel.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(new SavedData.Factory<>(GlobalLocations::new, GlobalLocations::load, DataFixTypes.LEVEL), TPMod.MODID + "_locations");
    }

    /**
     * 添加或覆盖一个全局传送点。
     * @param name 名称
     * @param location 位置数据
     */
    public void addLocation(String name, LocationData location) {
        locations.put(name, location);
        setDirty();
    }

    /**
     * 获取指定名称的全局传送点。
     * @param name 名称
     * @return 位置数据，若不存在返回null
     */
    public LocationData getLocation(String name) {
        return locations.get(name);
    }

    /**
     * 删除指定名称的全局传送点。
     * @param name 名称
     * @return 是否删除成功
     */
    public boolean removeLocation(String name) {
        if (locations.remove(name) != null) {
            setDirty();
            return true;
        }
        return false;
    }

    /**
     * 获取所有全局传送点名称集合。
     * @return 名称集合
     */
    public Set<String> getLocationNames() {
        return locations.keySet();
    }
}