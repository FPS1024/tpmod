package io.github.fps1024.tpmod.command;

import io.github.fps1024.tpmod.data.GlobalLocations;
import io.github.fps1024.tpmod.data.LocationData;
import io.github.fps1024.tpmod.service.TeleportationService;
import io.github.fps1024.tpmod.util.Constants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * TPM命令注册和处理类。
 * 负责注册 /tpm 指令及其子命令，并将业务逻辑委托给服务层处理。
 * 仅限权限等级2（管理员）及以上使用。
 *
 * @author FPS1024
 */
public final class TPMCommand {
    /**
     * 私有构造函数，防止实例化。
     */
    private TPMCommand() {
        throw new UnsupportedOperationException("Command class cannot be instantiated");
    }

    /**
     * 注册 /tpm 指令及其子命令到指令分发器。
     * 子命令包括：
     * <ul>
     *   <li>/tpm set &lt;name&gt; - 将当前位置保存为全局传送点</li>
     *   <li>/tpm &lt;player&gt; &lt;name&gt; - 将指定玩家传送到指定全局传送点</li>
     *   <li>/tpm rm &lt;name&gt; - 删除指定名称的全局传送点</li>
     *   <li>/tpm ls - 列出所有全局传送点</li>
     * </ul>
     *
     * @param dispatcher 命令分发器
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpm")
                .requires(source -> source.hasPermission(Constants.REQUIRED_PERMISSION_LEVEL))
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> executeSetLocation(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx, "name")
                                ))))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        getLocationNames(ctx.getSource()),
                                        builder
                                ))
                                .executes(ctx -> executeTeleportPlayer(
                                        ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"),
                                        StringArgumentType.getString(ctx, "name")
                                ))))
                .then(Commands.literal("rm")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        getLocationNames(ctx.getSource()),
                                        builder
                                ))
                                .executes(ctx -> executeRemoveLocation(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx, "name")
                                ))))
                .then(Commands.literal("ls")
                        .executes(ctx -> executeListLocations(ctx.getSource()))));
    }

    /**
     * 执行设置传送点命令。
     *
     * @param source 命令源
     * @param name   传送点名称
     * @return 命令执行结果代码
     * @throws CommandSyntaxException 如果命令语法错误
     */
    private static int executeSetLocation(CommandSourceStack source, String name)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Component message = TeleportationService.setLocation(player, name);
        source.sendSuccess(() -> message, true);
        return 1;
    }

    /**
     * 执行传送玩家命令。
     *
     * @param source       命令源
     * @param targetPlayer 目标玩家
     * @param locationName 传送点名称
     * @return 命令执行结果代码
     */
    private static int executeTeleportPlayer(
            CommandSourceStack source,
            ServerPlayer targetPlayer,
            String locationName
    ) {
        TeleportationService.TeleportationResult result =
                TeleportationService.teleportPlayer(targetPlayer, locationName);

        if (result.isSuccess()) {
            source.sendSuccess(() -> result.getAdminMessage(), true);
            if (result.getPlayerMessage() != null) {
                targetPlayer.sendSystemMessage(result.getPlayerMessage());
            }
        } else {
            source.sendFailure(result.getAdminMessage());
        }

        return 1;
    }

    /**
     * 执行删除传送点命令。
     *
     * @param source 命令源
     * @param name   传送点名称
     * @return 命令执行结果代码
     * @throws CommandSyntaxException 如果命令语法错误
     */
    private static int executeRemoveLocation(CommandSourceStack source, String name)
            throws CommandSyntaxException {
        source.getPlayerOrException(); // 验证玩家存在
        Component message = TeleportationService.removeLocation(source.getLevel(), name);

        if (message.getString().contains("removed")) {
            source.sendSuccess(() -> message, true);
        } else {
            source.sendFailure(message);
        }

        return 1;
    }

    /**
     * 执行列出传送点命令。
     *
     * @param source 命令源
     * @return 命令执行结果代码
     */
    private static int executeListLocations(CommandSourceStack source) {
        TeleportationService.LocationListResult result =
                TeleportationService.listLocations(source.getLevel());

        if (result.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal("There are no saved global locations."),
                    false
            );
            return 1;
        }

        source.sendSuccess(
                () -> Component.literal("Saved global locations:"),
                false
        );

        GlobalLocations locations = result.getLocations();
        Set<String> locationNames = result.getLocationNames();

        locationNames.forEach(name -> {
            LocationData loc = locations.getLocation(name);
            Component locationInfo = Component.literal(String.format(
                    "%s %d %d %d",
                    name,
                    loc.getX(),
                    loc.getY(),
                    loc.getZ()
            ));
            source.sendSuccess(() -> locationInfo, false);
        });

        return 1;
    }

    /**
     * 获取所有全局传送点名称，用于命令补全。
     *
     * @param source 命令源
     * @return 传送点名称集合
     */
    private static Set<String> getLocationNames(CommandSourceStack source) {
        return TeleportationService.getLocationNames(source.getLevel());
    }
}

