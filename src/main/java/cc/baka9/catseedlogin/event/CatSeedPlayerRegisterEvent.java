package cc.baka9.catseedlogin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class CatSeedPlayerRegisterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public CatSeedPlayerRegisterEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }


}
