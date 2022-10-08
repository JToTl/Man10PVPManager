package ltotj.minecraft.man10pvpmanager

import ltotj.minecraft.man10pvpmanager.equipmentLimit.EquipLimitCommand
import ltotj.minecraft.man10pvpmanager.equipmentLimit.EquipmentLimit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object{
        lateinit var plugin:JavaPlugin
        const val pluginTitle="§f§l[§eMan10PVPManager§f§l]"
    }

    override fun onEnable() {
        // Plugin startup logic

        plugin=this

        EquipmentLimit()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

}