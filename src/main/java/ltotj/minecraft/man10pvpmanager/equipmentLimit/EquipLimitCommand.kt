package ltotj.minecraft.man10pvpmanager.equipmentLimit

import ltotj.minecraft.man10pvpmanager.utilities.CommandManager.CommandArgumentType
import ltotj.minecraft.man10pvpmanager.utilities.CommandManager.CommandManager
import ltotj.minecraft.man10pvpmanager.utilities.CommandManager.CommandObject
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EquipLimitCommand(val equipmentLimit: EquipmentLimit,plugin:JavaPlugin,alias:String,pluginTitle:String):CommandManager(plugin,alias,pluginTitle){

    val removeWorldObject=CommandObject("remove")
        .addNextArgument(
            CommandObject(equipmentLimit.field,"ワールド名")
                .setFunction{
                    equipmentLimit.removeField(it.first,it.second[1])
                }
        )



    init {

        setPermission("eqlimit.admin")

        addFirstArgument(
            CommandObject("add")
                .addNextArgument(
                    CommandObject(CommandArgumentType.STRING)
                        .setComment("ワールド名")
                        .setExplanation("装備禁止ワールドを追加")
                        .setFunction{
                            equipmentLimit.addField(it.first,it.second[1])
                        }
                )
        )

        addFirstArgument(
            removeWorldObject
        )

        addFirstArgument(
            CommandObject("on")
                .setFunction{
                    if(equipmentLimit.enable){
                        sendPluginMessage(it.first,"§a既にオンです")
                    }
                    else{
                        equipmentLimit.register()
                        sendPluginMessage(it.first,"§aオンにしました")
                        equipmentLimit.enable=true
                        equipmentLimit.equipLimitConfig.setValue("enable",true)
                        equipmentLimit.equipLimitConfig.save()
                    }
                }
        )

        addFirstArgument(
            CommandObject("off")
                .setFunction{
                    if(equipmentLimit.enable){
                        equipmentLimit.unregister()
                        sendPluginMessage(it.first,"§aオフにしました")
                        equipmentLimit.enable=false
                        equipmentLimit.equipLimitConfig.setValue("enable",false)
                        equipmentLimit.equipLimitConfig.save()
                    }
                    else{
                        sendPluginMessage(it.first,"§a既にオフです")
                    }
                }
        )

        addFirstArgument(
            CommandObject("banitem")
                .setOnlyPlayer(true)
                .setExplanation("手持ちアイテムを装備禁止にする")
                .setFunction{
                    val player=it.first as Player
                    equipmentLimit.deniedEquipMent.add(player.inventory.itemInMainHand.itemMeta.displayName)
                    sendPluginMessage(player,"§e${player.inventory.itemInMainHand.itemMeta.displayName}§cを装備禁止リストに追加しました")
                    equipmentLimit.saveItemConfig()
                }
        )

        addFirstArgument(
            CommandObject("unbantem")
                .setExplanation("手持ちアイテムの装備禁止を解除する")
                .setOnlyPlayer(true)
                .setFunction{
                    val player=it.first as Player
                    equipmentLimit.deniedEquipMent.remove(player.inventory.itemInMainHand.itemMeta.displayName)
                    sendPluginMessage(player,"§e${player.inventory.itemInMainHand.itemMeta.displayName}§cを装備禁止リストから除外しました")
                    equipmentLimit.saveItemConfig()
                }
        )

        addFirstArgument(
            CommandObject("allowitem")
                .setOnlyPlayer(true)
                .setExplanation("手持ちアイテムの装備を許可する")
                .setFunction{
                    val player=it.first as Player
                    equipmentLimit.allowedEquipment.add(player.inventory.itemInMainHand.itemMeta.displayName)
                    sendPluginMessage(player,"§e${player.inventory.itemInMainHand.itemMeta.displayName}§cを装備可能リストに追加しました")
                    equipmentLimit.saveItemConfig()
            }
        )

        addFirstArgument(
            CommandObject("denyitem")
                .setOnlyPlayer(true)
                .setExplanation("手持ちアイテムの装備許可を取り下げる")
                .setFunction{
                    val player=it.first as Player
                    equipmentLimit.allowedEquipment.remove(player.inventory.itemInMainHand.itemMeta.displayName)
                    sendPluginMessage(player,"§e${player.inventory.itemInMainHand.itemMeta.displayName}§cを装備可能リストから除外しました")
                    equipmentLimit.saveItemConfig()
                }
        )

    }

}