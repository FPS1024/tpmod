package com.example.tpmod;

import com.example.tpmod.command.TPMCommand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TPMod.MODID) // 声明该类为 Forge 模组主类，MODID 为模组唯一标识
public class TPMod {
    public static final String MODID = "tpmod"; // 模组 ID，供 Forge 识别
    private static final Logger LOGGER = LogUtils.getLogger(); // 日志记录器，用于输出日志信息

    public TPMod() {
        // 将当前类注册到 MinecraftForge 的事件总线，以监听游戏事件
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent // 标记该方法为事件监听器
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // 注册自定义命令到命令分发器
        TPMCommand.register(event.getDispatcher());
        // 输出命令注册成功的日志
        LOGGER.info("TPM commands registered.");
    }
}