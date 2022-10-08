package ltotj.minecraft.man10pvpmanager.equipmentLimit

import ltotj.minecraft.man10pvpmanager.Main
import ltotj.minecraft.man10pvpmanager.utilities.ConfigManager.ConfigManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class EquipmentLimit:Listener {

        val field = ArrayList<String>()
        val allowedEquipment = ArrayList<String>()
        val deniedEquipMent = ArrayList<String>()
        val armoredPlayers = ArrayList<Player>()
        var enable =true
        val equipLimitConfig = ConfigManager(Main.plugin, "config", "equipmentLimit")
        lateinit var command: EquipLimitCommand

    private val armors= arrayListOf(Material.LEATHER_HELMET
        ,Material.LEATHER_CHESTPLATE
        ,Material.LEATHER_LEGGINGS
        ,Material.LEATHER_BOOTS
        ,Material.CHAINMAIL_HELMET
        ,Material.CHAINMAIL_CHESTPLATE
        ,Material.CHAINMAIL_LEGGINGS
        ,Material.CHAINMAIL_BOOTS
        ,Material.IRON_HELMET
        ,Material.IRON_CHESTPLATE
        ,Material.IRON_LEGGINGS
        ,Material.IRON_BOOTS
        ,Material.GOLDEN_HELMET
        ,Material.GOLDEN_CHESTPLATE
        ,Material.GOLDEN_LEGGINGS
        ,Material.GOLDEN_BOOTS
        ,Material.DIAMOND_HELMET
        ,Material.DIAMOND_CHESTPLATE
        ,Material.DIAMOND_LEGGINGS
        ,Material.DIAMOND_BOOTS
        ,Material.NETHERITE_HELMET
        ,Material.NETHERITE_CHESTPLATE
        ,Material.NETHERITE_LEGGINGS
        ,Material.NETHERITE_BOOTS)

    init{
        equipLimitConfig.saveDefConfig()
        enable= equipLimitConfig.getBoolean("enable")
        field.addAll(equipLimitConfig.getStringList("worlds"))
        allowedEquipment.addAll(equipLimitConfig.getStringList("allowedItem"))
        deniedEquipMent.addAll(equipLimitConfig.getStringList("deniedItem"))
        command=EquipLimitCommand(this,Main.plugin,"eqlimit", Main.pluginTitle)
        if(enable) {
            register()
        }
    }

    fun addField(sender: CommandSender, str:String){
        if(Main.plugin.server.getWorld(str)==null){
            sender.sendMessage("${Main.pluginTitle}§cそのワールドは存在しません")
            return
        }
        if(field.contains(str)){
            sender.sendMessage("${Main.pluginTitle}§c既に追加されています")
            return
        }
        field.add(str)
        equipLimitConfig.setValue("worlds", field)
        command.removeWorldObject.setArguments(field)
        sender.sendMessage("${Main.pluginTitle}§c装備制限ワールドに設定しました")
    }

    fun removeField(sender:CommandSender,str:String){
        if(!field.contains(str)){
            sender.sendMessage("${Main.pluginTitle}§c既に除外されています")
            return
        }
        field.remove(str)
        equipLimitConfig.setValue("worlds", field)
        command.removeWorldObject.setArguments(field)
        sender.sendMessage("${Main.pluginTitle}§c装備制限ワールドから削除しました")
    }


    fun unregister(){
        HandlerList.unregisterAll(this)
    }

    fun register(){
        unregister()
        Main.plugin.server.pluginManager.registerEvents(this, Main.plugin)
    }

    fun armored(player:Player):Boolean{
        for(item in player.inventory.armorContents){
            if(item==null)continue
            if((armors.contains(item.type)&&!allowedEquipment.contains(item.itemMeta.displayName)|| deniedEquipMent.contains(item.itemMeta.displayName)))return true
        }
        return false
    }

    fun saveItemConfig(){
        equipLimitConfig.setValue("deniedItems", deniedEquipMent)
        equipLimitConfig.setValue("allowedItems", allowedEquipment)
        equipLimitConfig.save()
    }

    @EventHandler
    fun joinField(e:PlayerChangedWorldEvent){
        if(!field.contains(e.player.world.name))return
        if(armored(e.player)){
            armoredPlayers.add(e.player)
        }
        else{
            armoredPlayers.remove(e.player)
        }
    }

    @EventHandler
    fun rightClickEquip(e: PlayerInteractEvent){
        val player=e.player
        if(!field.contains(player.world.name))return
        if(e.action.isLeftClick)return
        val item=e.item?:return
        if(!armors.contains(e.material)&&!deniedEquipMent.contains(item.itemMeta?.displayName))return
        if(allowedEquipment.contains(item.itemMeta?.displayName))return

        e.isCancelled=true
        e.player.sendMessage("§cこのエリアでは装備を着ることができません")

    }

    @EventHandler
    fun move(e:PlayerMoveEvent){
        if(armoredPlayers.contains(e.player)){
            if(field.contains(e.player.world.name)){
                e.isCancelled=true
                e.player.sendMessage("§cこのエリアでは防具を装備することはできません!")
            }
        }
    }

    @EventHandler
    fun closeInv(e:InventoryCloseEvent){
        val player=e.player as Player
        if(!field.contains(player.world.name))return
        if(armored(player)){
            armoredPlayers.add(player)
        }
        else{
            armoredPlayers.remove(player)
        }
    }

    @EventHandler
    fun logout(e:PlayerQuitEvent){
        armoredPlayers.remove(e.player)
    }

    @EventHandler
    fun command(e: PlayerCommandPreprocessEvent){
        val player=e.player
        if(field.contains(player.world.name)){
            if(e.message.substringBefore("mhat")=="/"){
                e.isCancelled=true
                player.sendMessage("§4ここではmhatを使うことができません")
            }
        }
    }


}