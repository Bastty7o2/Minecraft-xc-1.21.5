package phonon.xc.gun.item

import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataContainer

import phonon.xc.XC
import phonon.xc.gun.Gun

public fun XC.setGunItemMetaAmmo(itemMeta: ItemMeta, gun: Gun, ammo: Int): ItemMeta {
    val itemData = itemMeta.persistentDataContainer
    itemData.set(this.namespaceKeyItemAmmo, PersistentDataType.INTEGER, ammo)
    itemMeta.setLore(gun.getItemDescriptionForAmmo(ammo))
    return itemMeta
}

public fun XC.setGunItemMetaAmmoAndModel(
    itemMeta: ItemMeta,
    itemData: PersistentDataContainer,
    gun: Gun,
    ammo: Int,
    useAimDownSights: Boolean,
): ItemMeta {
    itemData.set(this.namespaceKeyItemAmmo, PersistentDataType.INTEGER, ammo)
    itemMeta.setLore(gun.getItemDescriptionForAmmo(ammo))
    return setGunItemMetaModel(itemMeta, gun, ammo, useAimDownSights)
}

public fun XC.setGunItemMetaModel(
    itemMeta: ItemMeta,
    gun: Gun,
    ammo: Int,
    aimdownsights: Boolean,
): ItemMeta {
    if (ammo <= 0 && gun.itemModelEmpty > 0) {
        itemMeta.setCustomModelData(gun.itemModelEmpty)
    } else {
        if (gun.itemModelAimDownSights > 0 && aimdownsights) {
            itemMeta.setCustomModelData(gun.itemModelAimDownSights)
        } else {
            itemMeta.setCustomModelData(gun.itemModelDefault)
        }
    }

    return itemMeta
}

public fun XC.setGunItemMetaReloadModel(itemMeta: ItemMeta, gun: Gun): ItemMeta {
    if (gun.itemModelReload > 0) {
        itemMeta.setCustomModelData(gun.itemModelReload)
    } else {
        itemMeta.setCustomModelData(gun.itemModelDefault)
    }

    return itemMeta
}