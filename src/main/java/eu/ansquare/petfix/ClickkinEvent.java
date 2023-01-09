package eu.ansquare.petfix;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;
import java.util.Set;
import java.util.List;
import java.util.UUID;

public class ClickkinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if(Tameable.class.isAssignableFrom(entity.getClass())){
            Tameable tameable = (Tameable) entity;
            UUID ownerUid = tameable.getOwnerUniqueId();
            AnimalTamer owner = tameable.getOwner();
            if(owner != null){
                System.out.println(owner.getName());
               // Set<String> = entity.getScoreboardTags();
            }
            else {
                System.out.println("Owner is null");
            }
        } else {
            System.out.println("entity not tameable.."+entity.getName()+" "+entity.getClass().getName());
        }

    }
}
