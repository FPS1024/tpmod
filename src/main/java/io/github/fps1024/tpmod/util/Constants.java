package io.github.fps1024.tpmod.util;

/**
 * 模组常量定义类。
 * 统一管理模组中使用的常量值，包括权限等级、消息文本等。
 *
 * @author FPS1024
 */
public final class Constants {
    /**
     * 私有构造函数，防止实例化。
     */
    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 命令执行所需的最低权限等级。
     */
    public static final int REQUIRED_PERMISSION_LEVEL = 2;

    /**
     * 传送点数据存储的NBT键名。
     */
    public static final String NBT_KEY_LOCATIONS = "locations";

    /**
     * 位置数据的NBT键名。
     */
    public static final String NBT_KEY_DIMENSION = "dimension";
    public static final String NBT_KEY_X = "x";
    public static final String NBT_KEY_Y = "y";
    public static final String NBT_KEY_Z = "z";

    /**
     * 传送点数据存储的文件名后缀。
     */
    public static final String SAVED_DATA_SUFFIX = "_locations";

    /**
     * 传送时的坐标偏移量（用于将方块坐标转换为实体坐标中心）。
     */
    public static final double TELEPORT_OFFSET = 0.5;
}

