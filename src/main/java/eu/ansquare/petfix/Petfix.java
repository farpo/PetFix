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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Optional;
import java.util.List;


public class Petfix extends JavaPlugin
{

    @Override
    public void onEnable(){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket().deepClone();
                int entityId = event.getPacket().getIntegers().read(0);
                Player player = getPlayer(entityId);
                Player clientPlayer = event.getPlayer();
                Object removed = null;
                if (player == clientPlayer) return;
                List<WrappedWatchableObject> metadata = packet.getWatchableCollectionModifier().read(0);
                WrappedWatchableObject wrappedowner = metadata.stream().filter(obj -> obj.getIndex() == 18).findAny().orElse(null);

                if(wrappedowner == null) return;
                if(wrappedowner.getValue() instanceof Optional<?> || wrappedowner.getValue() instanceof com.google.common.base.Optional<?>) {

                    System.out.println("marked");
                    wrappedowner.setValue(Optional.empty());
                    System.out.println(wrappedowner.getValue());
                }else{

                    return;
                }


                event.setPacket(packet);

            }
        });


    }
    private Player getPlayer(final int entityID) {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getEntityId() == entityID) return player;
        }
        return null;
    }


}