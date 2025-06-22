package com.example.tpmod.command;

import com.example.tpmod.data.GlobalLocations;
import com.example.tpmod.data.LocationData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

// TPMCommand 类用于注册和实现 /tpm 管理全局传送点的指令。
// 支持设置、删除、列出和传送玩家到指定位置。
// 仅限权限等级2（管理员）及以上使用。

public class TPMCommand {

    /**
     * 注册 /tpm 指令及其子命令到指令分发器。
     * 子命令包括：
     *   /tpm set <name>         - 将当前位置保存为全局传送点
     *   /tpm <player> <name>    - 将指定玩家传送到指定全局传送点
     *   /tpm rm <name>          - 删除指定名称的全局传送点
     *   /tpm ls                 - 列出所有全局传送点
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpm")
                // 仅允许权限等级2及以上执行
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> setLocation(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "name")))))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider
                                        .suggest(getLocations(ctx.getSource()), builder))
                                .executes(ctx -> teleportPlayerToLocation(ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"),
                                        StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("rm")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider
                                        .suggest(getLocations(ctx.getSource()), builder))
                                .executes(ctx -> removeLocation(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("ls")
                        .executes(ctx -> listLocations(ctx.getSource()))));
    }

    /**
     * 将执行者当前位置保存为指定名称的全局传送点。
     */
    private static int setLocation(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BlockPos playerPos = player.blockPosition();
        String dimension = player.level().dimension().toString();

        LocationData newLocation = new LocationData(dimension, playerPos.getX(), playerPos.getY(), playerPos.getZ());

        GlobalLocations locations = GlobalLocations.get(player.level());
        locations.addLocation(name, newLocation);

        source.sendSuccess(() -> Component.literal("Global location '" + name + "' set to " + playerPos.getX() + " "
                + playerPos.getY() + " " + playerPos.getZ()), true);

        return 1;
    }

    /**
     * 将指定玩家传送到指定名称的全局传送点。
     * 仅支持同一维度内传送。
     */
    private static int teleportPlayerToLocation(CommandSourceStack source, ServerPlayer targetPlayer, String name) {
        GlobalLocations locations = GlobalLocations.get(targetPlayer.level());
        LocationData locationData = locations.getLocation(name);

        if (locationData != null) {
            // 仅支持同一维度内传送
            if (targetPlayer.level().dimension().toString().equals(locationData.getDimension())) {
                targetPlayer.teleportTo(locationData.getX() + 0.5, locationData.getY(), locationData.getZ() + 0.5);
                source.sendSuccess(
                        () -> Component.literal(
                                "Teleported player " + targetPlayer.getName().getString() + " to '" + name + "'."),
                        true);
                targetPlayer.sendSystemMessage(
                        Component.literal("You have been teleported to '" + name + "' by an admin."));
            } else {
                source.sendFailure(Component
                        .literal("Cannot teleport across dimensions. Location is in " + locationData.getDimension()));
            }
        } else {
            source.sendFailure(Component.literal("Global location '" + name + "' not found."));
        }

        return 1;
    }

    /**
     * 删除指定名称的全局传送点。
     */
    private static int removeLocation(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        GlobalLocations locations = GlobalLocations.get(player.level());

        if (locations.removeLocation(name)) {
            source.sendSuccess(() -> Component.literal("Global location '" + name + "' removed."), true);
        } else {
            source.sendFailure(Component.literal("Global location '" + name + "' not found."));
        }

        return 1;
    }

    /**
     * 列出所有已保存的全局传送点。
     */
    private static int listLocations(CommandSourceStack source) {
        GlobalLocations locations = GlobalLocations.get(source.getLevel());
        Set<String> locationNames = locations.getLocationNames();

        if (locationNames.isEmpty()) {
            source.sendSuccess(() -> Component.literal("There are no saved global locations."), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal("Saved global locations:"), false);
        locationNames.forEach(name -> {
            LocationData loc = locations.getLocation(name);
            source.sendSuccess(() -> Component.literal(name + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ()),
                    false);
        });

        return 1;
    }

    /**
     * 获取所有全局传送点名称，用于指令补全。
     */
    private static Set<String> getLocations(CommandSourceStack source) {
        GlobalLocations locations = GlobalLocations.get(source.getLevel());
        return locations.getLocationNames();
    }
}