package io.github.fps1024.tpmod;

import io.github.fps1024.tpmod.command.TPMCommand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * TP Mod 主类。
 * 模组的入口点，负责初始化模组和注册事件监听器。
 *
 * @author FPS1024
 */
@Mod(TPMod.MODID)
public final class TPMod {
    /**
     * 模组唯一标识符。
     */
    public static final String MODID = "tpmod";

    /**
     * 日志记录器，用于输出日志信息。
     */
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数。
     * 将当前类注册到 MinecraftForge 的事件总线，以监听游戏事件。
     */
    public TPMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 注册命令事件处理器。
     * 当游戏注册命令时，注册自定义的 /tpm 命令。
     *
     * @param event 注册命令事件
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TPMCommand.register(event.getDispatcher());
        LOGGER.info("TPM commands registered.");
    }
}

