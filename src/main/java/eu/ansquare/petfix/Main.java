package eu.ansquare.petfix;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.Bukkit;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Optional;
import java.util.List;


public class Main extends JavaPlugin
{
    boolean burning;
    boolean glowing;

    public enum EntityData {

        ON_FIRE((byte) 0x01),
        CROUCHING((byte) 0x02),
        SPRINTING((byte) 0x08),
        SWIMMING((byte) 0x10),
        INVISIBLE((byte) 0x20),
        GLOWING((byte) 0x40),
        ELYTRA_FLY((byte) 0x80);

        final byte bitMask;

        EntityData(byte bitMask) {
            this.bitMask = bitMask;
        }

        public byte getBitMask() {
            return bitMask;
        }

        public boolean isPresent(byte bits) {
            return (this.bitMask & bits) == this.bitMask;
        }

        public byte setBit(byte bits) {
            return (byte) (bits | this.bitMask);
        }

        public byte unsetBit(byte bits) {
            return (byte) (bits & ~this.bitMask);
        }
    }
    @Override
    public void onEnable(){
        burning = false;
        glowing = false;
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket().deepClone();
                int entityId = event.getPacket().getIntegers().read(0); // Get the entity id.
                Player player = getPlayer(entityId); // Will be null if the entity is not a Player
                Player clientPlayer = event.getPlayer();
                if (player == clientPlayer) return;
                List<WrappedWatchableObject> metadata = packet.getWatchableCollectionModifier().read(0);
                WrappedWatchableObject wrappedowner = metadata.stream().filter(obj -> obj.getIndex() == 18).findAny().orElse(null);
                WrappedWatchableObject bitMaskContainer = metadata.stream().filter(obj -> obj.getIndex() == 0).findAny().orElse(null);

                if (bitMaskContainer == null) return;
                byte bitMask = (byte) bitMaskContainer.getValue();
                if(burning == true){
                    bitMask = EntityData.ON_FIRE.setBit(bitMask);

                }
                if(glowing == true){
                    bitMask = EntityData.GLOWING.setBit(bitMask);

                }

                bitMaskContainer.setValue(bitMask);
                if(wrappedowner != null) {

                    if (wrappedowner.getValue() instanceof Optional<?> || wrappedowner.getValue() instanceof com.google.common.base.Optional<?>) {

                        System.out.println("marked");
                        wrappedowner.setValue(Optional.empty());
                        System.out.println(wrappedowner.getValue());
                    }
                }

                event.setPacket(packet);

            }
        });


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(label.equalsIgnoreCase("ill")) {
            if (args[0].equalsIgnoreCase("burn")) {
                if(burning == false){
                    burning = true;
                    sender.sendMessage("It burns now");
                    return true;
                }
                else {
                    burning = false;
                    sender.sendMessage("It no longer burns");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("glow")) {
                if(glowing == false){
                    glowing = true;
                    sender.sendMessage("It glows now");
                    return true;
                }
                else {
                    glowing = false;
                    sender.sendMessage("It bo longer glows");
                    return true;
                }
            } else {
                sender.sendMessage("Unrecognized argument - " + args[0]);
                return true;
            }

        }
        return false;
    }

    private Player getPlayer(final int entityID) {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getEntityId() == entityID) return player;
        }
        return null;
    }


}
