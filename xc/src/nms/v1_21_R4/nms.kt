package phonon.xc.nms

import net.minecraft.server.level.ServerPlayer
import net.minecraft.nbt.Tag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.world.item.ItemStack
import org.bukkit.craftbukkit.inventory.CraftItemStack as BukkitCraftItemStack
import org.bukkit.craftbukkit.entity.CraftPlayer as BukkitCraftPlayer
import org.bukkit.craftbukkit.util.CraftMagicNumbers as BukkitCraftMagicNumbers

internal typealias NmsItemStack = ItemStack
internal typealias NmsNBTTagCompound = CompoundTag
internal typealias NmsNBTTagList = ListTag
internal typealias NmsNBTTagString = StringTag
internal typealias NmsNBTTagInt = IntTag
internal typealias NmsPacketPlayOutSetSlot = ClientboundContainerSetSlotPacket
internal typealias CraftItemStack = BukkitCraftItemStack
internal typealias CraftPlayer = BukkitCraftPlayer
internal typealias CraftMagicNumbers = BukkitCraftMagicNumbers

internal fun CraftPlayer.getMainHandNMSItem(): NmsItemStack {
    return this.handle.mainHandItem
}

internal fun <T : PacketListener> ServerPlayer.sendPacket(p: Packet<T>) {
    this.connection.send(p)
}

internal fun <T : PacketListener> List<CraftPlayer>.broadcastPacketWithinDistance(
    packet: Packet<T>,
    originX: Double,
    originY: Double,
    originZ: Double,
    maxDistance: Double,
) {
    val maxDistanceSq = maxDistance * maxDistance

    for (player in this) {
        val loc = player.location

        val dx = loc.x - originX
        val dy = loc.y - originY
        val dz = loc.z - originZ

        val distanceSq = (dx * dx) + (dy * dy) + (dz * dz)

        if (distanceSq <= maxDistanceSq) {
            player.handle.connection.send(packet)
        }
    }
}

internal fun ServerPlayer.sendItemSlotChange(slot: Int, item: ItemStack) {
    val packet = NmsPacketPlayOutSetSlot(
        this.inventoryMenu.containerId,
        this.inventoryMenu.incrementStateId(),
        slot,
        item,
    )

    this.connection.send(packet)
}

internal fun CompoundTag.putTag(key: String, tag: Tag) {
    this.put(key, tag)
}

internal fun CompoundTag.containsKey(key: String): Boolean {
    return this.contains(key)
}

internal fun CompoundTag.containsKeyOfType(key: String, ty: Int): Boolean {
    return this.contains(key)
}

@JvmInline
internal value class NBTTagString(val tag: NmsNBTTagString) {
    constructor(s: String) : this(NmsNBTTagString.valueOf(s))

    fun toNms(): NmsNBTTagString {
        return this.tag
    }
}

@JvmInline
internal value class NBTTagInt(val tag: NmsNBTTagInt) {
    constructor(i: Int) : this(NmsNBTTagInt.valueOf(i))

    fun toNms(): NmsNBTTagInt {
        return this.tag
    }
}