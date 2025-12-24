package io.github.fps1024.tpmod.service;

import io.github.fps1024.tpmod.data.GlobalLocations;
import io.github.fps1024.tpmod.data.LocationData;
import io.github.fps1024.tpmod.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * 传送服务类。
 * 负责处理所有与传送相关的业务逻辑，包括传送点的设置、删除、查询和玩家传送。
 *
 * @author FPS1024
 */
public final class TeleportationService {
    /**
     * 私有构造函数，防止实例化。
     */
    private TeleportationService() {
        throw new UnsupportedOperationException("Service class cannot be instantiated");
    }

    /**
     * 将玩家当前位置保存为指定名称的全局传送点。
     *
     * @param player 执行命令的玩家
     * @param name   传送点名称
     * @return 操作结果消息
     */
    public static Component setLocation(ServerPlayer player, String name) {
        BlockPos playerPos = player.blockPosition();
        String dimension = player.level().dimension().toString();

        LocationData newLocation = new LocationData(
                dimension,
                playerPos.getX(),
                playerPos.getY(),
                playerPos.getZ()
        );

        GlobalLocations locations = GlobalLocations.get(player.level());
        locations.addLocation(name, newLocation);

        return Component.literal(String.format(
                "Global location '%s' set to %d %d %d",
                name,
                playerPos.getX(),
                playerPos.getY(),
                playerPos.getZ()
        ));
    }

    /**
     * 将指定玩家传送到指定名称的全局传送点。
     * 仅支持同一维度内传送。
     *
     * @param targetPlayer 目标玩家
     * @param locationName 传送点名称
     * @return 操作结果，包含成功消息或错误消息
     */
    public static TeleportationResult teleportPlayer(ServerPlayer targetPlayer, String locationName) {
        GlobalLocations locations = GlobalLocations.get(targetPlayer.level());
        LocationData locationData = locations.getLocation(locationName);

        if (locationData == null) {
            return TeleportationResult.failure(
                    Component.literal(String.format("Global location '%s' not found.", locationName))
            );
        }

        String currentDimension = targetPlayer.level().dimension().toString();
        if (!currentDimension.equals(locationData.getDimension())) {
            return TeleportationResult.failure(
                    Component.literal(String.format(
                            "Cannot teleport across dimensions. Location is in %s",
                            locationData.getDimension()
                    ))
            );
        }

        double x = locationData.getX() + Constants.TELEPORT_OFFSET;
        double y = locationData.getY();
        double z = locationData.getZ() + Constants.TELEPORT_OFFSET;

        targetPlayer.teleportTo(x, y, z);

        Component adminMessage = Component.literal(String.format(
                "Teleported player %s to '%s'.",
                targetPlayer.getName().getString(),
                locationName
        ));
        Component playerMessage = Component.literal(String.format(
                "You have been teleported to '%s' by an admin.",
                locationName
        ));

        return TeleportationResult.success(adminMessage, playerMessage);
    }

    /**
     * 删除指定名称的全局传送点。
     *
     * @param level 世界对象
     * @param name  传送点名称
     * @return 操作结果消息
     */
    public static Component removeLocation(Level level, String name) {
        GlobalLocations locations = GlobalLocations.get(level);

        if (locations.removeLocation(name)) {
            return Component.literal(String.format("Global location '%s' removed.", name));
        } else {
            return Component.literal(String.format("Global location '%s' not found.", name));
        }
    }

    /**
     * 列出所有已保存的全局传送点。
     *
     * @param level 世界对象
     * @return 传送点列表结果
     */
    public static LocationListResult listLocations(Level level) {
        GlobalLocations locations = GlobalLocations.get(level);
        Set<String> locationNames = locations.getLocationNames();

        if (locationNames.isEmpty()) {
            return LocationListResult.empty();
        }

        return LocationListResult.withLocations(locations, locationNames);
    }

    /**
     * 获取所有全局传送点名称集合，用于命令补全。
     *
     * @param level 世界对象
     * @return 传送点名称集合
     */
    public static Set<String> getLocationNames(Level level) {
        GlobalLocations locations = GlobalLocations.get(level);
        return locations.getLocationNames();
    }

    /**
     * 传送操作结果类。
     * 封装传送操作的结果，包括成功/失败状态和相应的消息。
     */
    public static final class TeleportationResult {
        private final boolean success;
        private final Component adminMessage;
        private final Component playerMessage;

        private TeleportationResult(boolean success, Component adminMessage, Component playerMessage) {
            this.success = success;
            this.adminMessage = adminMessage;
            this.playerMessage = playerMessage;
        }

        /**
         * 创建成功结果。
         *
         * @param adminMessage 发送给管理员的成功消息
         * @param playerMessage 发送给玩家的消息
         * @return 成功结果对象
         */
        public static TeleportationResult success(Component adminMessage, Component playerMessage) {
            return new TeleportationResult(true, adminMessage, playerMessage);
        }

        /**
         * 创建失败结果。
         *
         * @param errorMessage 错误消息
         * @return 失败结果对象
         */
        public static TeleportationResult failure(Component errorMessage) {
            return new TeleportationResult(false, errorMessage, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public Component getAdminMessage() {
            return adminMessage;
        }

        public Component getPlayerMessage() {
            return playerMessage;
        }
    }

    /**
     * 传送点列表结果类。
     * 封装列出传送点的结果。
     */
    public static final class LocationListResult {
        private final boolean empty;
        private final GlobalLocations locations;
        private final Set<String> locationNames;

        private LocationListResult(boolean empty, GlobalLocations locations, Set<String> locationNames) {
            this.empty = empty;
            this.locations = locations;
            this.locationNames = locationNames;
        }

        /**
         * 创建空列表结果。
         *
         * @return 空列表结果对象
         */
        public static LocationListResult empty() {
            return new LocationListResult(true, null, null);
        }

        /**
         * 创建包含传送点的列表结果。
         *
         * @param locations     全局传送点数据对象
         * @param locationNames 传送点名称集合
         * @return 列表结果对象
         */
        public static LocationListResult withLocations(GlobalLocations locations, Set<String> locationNames) {
            return new LocationListResult(false, locations, locationNames);
        }

        public boolean isEmpty() {
            return empty;
        }

        public GlobalLocations getLocations() {
            return locations;
        }

        public Set<String> getLocationNames() {
            return locationNames;
        }
    }
}

