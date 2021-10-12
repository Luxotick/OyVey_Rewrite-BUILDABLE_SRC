package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Bow32k extends Module {
  private boolean shooting;
  
  private long lastShootTime;
  
  public Setting<Boolean> Bows;
  
  public Setting<Boolean> pearls;
  
  public Setting<Boolean> eggs;
  
  public Setting<Boolean> snowballs;
  
  public Setting<Integer> Timeout;
  
  public Setting<Integer> spoofs;
  
  public Setting<Boolean> bypass;
  
  public Setting<Boolean> debug;
  
  public Bow32k() {
    super("Bow32k", "One Shots Players", Module.Category.MISC, true, false, false);
    this.Bows = register(new Setting("Bows", Boolean.valueOf(true)));
    this.pearls = register(new Setting("Pearls", Boolean.valueOf(true)));
    this.eggs = register(new Setting("Eggs", Boolean.valueOf(true)));
    this.snowballs = register(new Setting("SnowBalls", Boolean.valueOf(true)));
    this.Timeout = register(new Setting("Timeout", Integer.valueOf(5000), Integer.valueOf(100), Integer.valueOf(20000)));
    this.spoofs = register(new Setting("Spoofs", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(300)));
    this.bypass = register(new Setting("Bypasses", Boolean.valueOf(false)));
    this.debug = register(new Setting("DebugOnToggle", Boolean.valueOf(false)));
  }
  
  public void onEnable() {
    if (isEnabled()) {
      this.shooting = false;
      this.lastShootTime = System.currentTimeMillis();
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() != 0)
      return; 
    if (event.getPacket() instanceof CPacketPlayerDigging) {
      CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
      if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
        ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if (!handStack.isEmpty() && handStack.getItem() != null && handStack.getItem() instanceof net.minecraft.item.ItemBow && ((Boolean)this.Bows.getValue()).booleanValue()) {
          doSpoofs();
          if (((Boolean)this.debug.getValue()).booleanValue())
            Command.sendMessage("Spoof ATTEMPT"); 
        } 
      } 
    } else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
      CPacketPlayerTryUseItem packet2 = (CPacketPlayerTryUseItem)event.getPacket();
      if (packet2.getHand() == EnumHand.MAIN_HAND) {
        ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if (!handStack.isEmpty() && handStack.getItem() != null)
          if (handStack.getItem() instanceof net.minecraft.item.ItemEgg && ((Boolean)this.eggs.getValue()).booleanValue()) {
            doSpoofs();
          } else if (handStack.getItem() instanceof net.minecraft.item.ItemEnderPearl && ((Boolean)this.pearls.getValue()).booleanValue()) {
            doSpoofs();
          } else if (handStack.getItem() instanceof net.minecraft.item.ItemSnowball && ((Boolean)this.snowballs.getValue()).booleanValue()) {
            doSpoofs();
          }  
      } 
    } 
  }
  
  private void doSpoofs() {
    if (System.currentTimeMillis() - this.lastShootTime >= ((Integer)this.Timeout.getValue()).intValue()) {
      this.shooting = true;
      this.lastShootTime = System.currentTimeMillis();
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SPRINTING));
      for (int index = 0; index < ((Integer)this.spoofs.getValue()).intValue(); index++) {
        if (((Boolean)this.bypass.getValue()).booleanValue()) {
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-10D, mc.player.posZ, false));
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-10D, mc.player.posZ, true));
        } else {
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-10D, mc.player.posZ, true));
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-10D, mc.player.posZ, false));
        } 
      } 
      if (((Boolean)this.debug.getValue()).booleanValue())
        Command.sendMessage("Spoofed"); 
      this.shooting = false;
    } 
  }
}
