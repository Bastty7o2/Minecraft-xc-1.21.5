package phonon.xc.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import phonon.xc.XC
import phonon.xc.ammo.Ammo
import phonon.xc.armor.Hat
import phonon.xc.gun.Gun
import phonon.xc.landmine.Landmine
import phonon.xc.melee.MeleeWeapon
import phonon.xc.throwable.ThrowableItem
import phonon.xc.nms.NmsItemStack
import phonon.xc.nms.CraftItemStack
import phonon.xc.nms.CraftPlayer
import phonon.xc.nms.getMainHandNMSItem
import org.bukkit.persistence.PersistentDataType

internal const val BUKKIT_STORAGE_TAG = "PublicBukkitValues"
internal const val NBT_TAG_INT = 3

private fun nmsToBukkit(nmsItem: NmsItemStack): ItemStack {
    return CraftItemStack.asBukkitCopy(nmsItem)
}

private fun getCustomModelId(item: ItemStack?): Int {
    if (item == null || item.type == Material.AIR) return -1

    val meta = item.itemMeta ?: return -1

    return if (meta.hasCustomModelData()) {
        meta.customModelData
    } else {
        -1
    }
}

private fun <T> getObjectFromBukkitItemStack(
    item: ItemStack?,
    materialType: Material,
    storage: Array<T>,
): T? {
    if (item == null || item.type != materialType) return null

    val modelId = getCustomModelId(item)

    if (modelId >= 0 && modelId < storage.size) {
        return storage[modelId]
    }

    return null
}

private fun XC.namespacedKeyFromString(key: String): NamespacedKey {
    val cleanKey = if (key.contains(":")) {
        key.substringAfter(":")
    } else {
        key
    }

return NamespacedKey("xc", cleanKey)
}

private fun XC.getPdcInt(item: ItemStack?, key: String): Int {
    if (item == null || item.type == Material.AIR) return -1

    val meta = item.itemMeta ?: return -1
    val pdc = meta.persistentDataContainer
    val wantedKey = namespacedKeyFromString(key)

    val direct = pdc.get(wantedKey, org.bukkit.persistence.PersistentDataType.INTEGER)
    if (direct != null) return direct

    for (existingKey in pdc.keys) {
        if (existingKey.key == key || existingKey.toString() == key) {
            val value = pdc.get(existingKey, org.bukkit.persistence.PersistentDataType.INTEGER)
            if (value != null) return value
        }
    }

    return -1
}

public fun XC.getItemTypeInHand(player: Player): Int {
    val item = player.inventory.itemInMainHand
    return this.config.materialToCustomItemType[item.type]
}

public fun XC.getCustomItemIdInHand(player: Player, itemType: Int): Int {
    val item = player.inventory.itemInMainHand

    val itemTypeInHand = this.config.materialToCustomItemType[item.type]
    if (itemTypeInHand != itemType) {
        return -1
    }

    return getCustomModelId(item)
}

public fun <T> getObjectFromNmsItemStack(
    nmsItem: NmsItemStack,
    materialType: Material,
    storage: Array<T>,
): T? {
    val item = nmsToBukkit(nmsItem)
    return getObjectFromBukkitItemStack(item, materialType, storage)
}

public fun <T> getCustomItemUnchecked(
    nmsItem: NmsItemStack,
    storage: Array<T>,
): T? {
    val item = nmsToBukkit(nmsItem)
    val modelId = getCustomModelId(item)

    if (modelId >= 0 && modelId < storage.size) {
        return storage[modelId]
    }

    return null
}

internal object GetNmsItemStack {
    public fun from(item: CraftItemStack): NmsItemStack {
        return CraftItemStack.asNMSCopy(item)
    }
}

public fun XC.getItemIntDataIfMaterialMatches(
    item: ItemStack,
    material: Material,
    key: String,
): Int {
    if (item.type != material) return -1
    return this.getPdcInt(item, key)
}

internal fun getInventorySlotForCustomItemWithNbtKey(
    player: Player,
    material: Material,
    nbtKey: String,
    value: Int,
): Int {
    return -1
}

internal fun setItemArmorNMS(
    item: ItemStack,
    armor: Int,
    slot: String,
    uuidLeast: Int,
    uuidMost: Int,
): ItemStack {
    // Temporarily disabled for 1.21.5 port.
    // Old version used raw NMS AttributeModifiers NBT, which changed after 1.20.5.
    return item
}

public fun checkHandMaterialAndGetNbtIntKey(
    player: Player,
    material: Material,
    key: String,
): Int {
    val item = player.inventory.itemInMainHand
    if (item.type != material) return -1

    val meta = item.itemMeta ?: return -1
    val cleanKey = if (key.contains(":")) key.substringAfter(":") else key
    val namespacedKey = org.bukkit.NamespacedKey("xc", cleanKey.lowercase())

    return meta.persistentDataContainer.get(
        namespacedKey,
        PersistentDataType.INTEGER
    ) ?: -1
}

// ============================================================================
// GUN ITEM GETTERS
// ============================================================================

public fun XC.getGunFromNmsItemStack(nmsItem: NmsItemStack): Gun? {
    return getObjectFromNmsItemStack(
        nmsItem,
        this.config.materialGun,
        this.storage.gun,
    )
}

public fun XC.getGunInHand(player: Player): Gun? {
    return getObjectFromBukkitItemStack(
        player.inventory.itemInMainHand,
        this.config.materialGun,
        this.storage.gun,
    )
}

public fun XC.getGunInHandUnchecked(player: Player): Gun? {
    val modelId = getCustomModelId(player.inventory.itemInMainHand)

    if (modelId >= 0 && modelId < this.storage.gun.size) {
        return this.storage.gun[modelId]
    }

    return null
}

public fun XC.getGunInSlot(player: Player, slot: Int): Gun? {
    return getObjectFromBukkitItemStack(
        player.inventory.getItem(slot),
        this.config.materialGun,
        this.storage.gun,
    )
}

public fun XC.getGunFromItem(item: ItemStack): Gun? {
    return getGunFromItemBukkit(item)
}

internal fun XC.getGunFromItemNMS(item: ItemStack): Gun? {
    return getGunFromItemBukkit(item)
}

internal fun XC.getGunFromItemBukkit(item: ItemStack): Gun? {
    if (item.type != this.config.materialGun) return null

    val modelId = getCustomModelId(item)

    if (modelId >= 0 && modelId < this.config.maxGunTypes) {
        return this.storage.gun[modelId]
    }

    return null
}

// ============================================================================
// THROWABLE ITEM GETTERS
// ============================================================================

public fun XC.getThrowableFromNmsItemStack(nmsItem: NmsItemStack): ThrowableItem? {
    return getObjectFromNmsItemStack(
        nmsItem,
        this.config.materialThrowable,
        this.storage.throwable,
    )
}

public fun XC.getThrowableInHand(player: Player): ThrowableItem? {
    return getObjectFromBukkitItemStack(
        player.inventory.itemInMainHand,
        this.config.materialThrowable,
        this.storage.throwable,
    )
}

public fun XC.getThrowableInHandUnchecked(player: Player): ThrowableItem? {
    val modelId = getCustomModelId(player.inventory.itemInMainHand)

    if (modelId >= 0 && modelId < this.storage.throwable.size) {
        return this.storage.throwable[modelId]
    }

    return null
}

public fun XC.getThrowableFromItem(item: ItemStack): ThrowableItem? {
    return getThrowableFromItemBukkit(item)
}

internal fun XC.getThrowableFromItemNMS(item: ItemStack): ThrowableItem? {
    return getThrowableFromItemBukkit(item)
}

internal fun XC.getThrowableFromItemBukkit(item: ItemStack): ThrowableItem? {
    if (item.type != this.config.materialThrowable) return null

    val modelId = getCustomModelId(item)

    if (modelId >= 0 && modelId < this.config.maxThrowableTypes) {
        return this.storage.throwable[modelId]
    }

    return null
}

// ============================================================================
// MELEE WEAPON ITEM GETTERS
// ============================================================================

public fun XC.getMeleeFromNmsItemStack(nmsItem: NmsItemStack): MeleeWeapon? {
    return getObjectFromNmsItemStack(
        nmsItem,
        this.config.materialMelee,
        this.storage.melee,
    )
}

public fun XC.getMeleeInHand(player: Player): MeleeWeapon? {
    return getObjectFromBukkitItemStack(
        player.inventory.itemInMainHand,
        this.config.materialMelee,
        this.storage.melee,
    )
}

public fun XC.getMeleeInHandUnchecked(player: Player): MeleeWeapon? {
    val modelId = getCustomModelId(player.inventory.itemInMainHand)

    if (modelId >= 0 && modelId < this.storage.melee.size) {
        return this.storage.melee[modelId]
    }

    return null
}

// ============================================================================
// ARMOR/HAT ITEM GETTERS
// ============================================================================

public fun XC.getHatFromNmsItemStack(nmsItem: NmsItemStack): Hat? {
    return getObjectFromNmsItemStack(
        nmsItem,
        this.config.materialArmor,
        this.storage.hat,
    )
}

public fun XC.getHatInHand(player: Player): Hat? {
    return getObjectFromBukkitItemStack(
        player.inventory.itemInMainHand,
        this.config.materialArmor,
        this.storage.hat,
    )
}

public fun XC.getHatInHandUnchecked(player: Player): Hat? {
    val modelId = getCustomModelId(player.inventory.itemInMainHand)

    if (modelId >= 0 && modelId < this.storage.hat.size) {
        return this.storage.hat[modelId]
    }

    return null
}