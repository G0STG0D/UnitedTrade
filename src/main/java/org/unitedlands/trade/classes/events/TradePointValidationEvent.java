package org.unitedlands.trade.classes.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.trade.classes.TradePoint;

import net.kyori.adventure.text.Component;

public class TradePointValidationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private TradePoint tradePoint;

    private boolean isValid = true;

    private List<Component> messages = new ArrayList<>();

    public TradePointValidationEvent(Player player, TradePoint tradePoint) {
        this.player = player;
        this.tradePoint = tradePoint;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TradePoint getTradePoint() {
        return tradePoint;
    }

    public void setTradePoint(TradePoint tradePoint) {
        this.tradePoint = tradePoint;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public List<Component> getMessages() {
        return messages;
    }

    public void setMessages(List<Component> messages) {
        this.messages = messages;
    }

}
