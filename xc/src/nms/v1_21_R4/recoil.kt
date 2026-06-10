package phonon.xc.nms.recoil

import org.bukkit.entity.Player
import org.bukkit.util.Vector

public fun Player.sendRecoilPacketUsingRelativeTeleport(
    recoilHorizontal: Float,
    recoilVertical: Float,
) {
    val loc = this.location

    loc.yaw += recoilHorizontal
    loc.pitch = (loc.pitch + recoilVertical).coerceIn(-90f, 90f)

    this.teleport(loc)
}

public fun Player.sendRecoilPacketUsingLookAt(
    dirX: Double,
    dirY: Double,
    dirZ: Double,
) {
    val loc = this.location
    loc.direction = Vector(dirX, dirY, dirZ)

    this.teleport(loc)
}