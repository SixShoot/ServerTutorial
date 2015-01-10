package io.snw.tutorial.util;

import io.github.andrepl.chatlib.ChatPosition;
import io.github.andrepl.chatlib.Text;
import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.data.DataLoading;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TutorialUtils {
    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static TutorialUtils instance;

    public Location getLocation(String tutorialName, int viewID) {
        String[] loc = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".location").split(",");
        World w = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public void textUtils(Player player) {
        String name = player.getName();
        MessageType type = Getters.getGetters().getTutorialView(name).getMessageType();
        if (type == MessageType.TEXT || type == MessageType.ACTION) {
            player.getInventory().clear();
            ItemStack i = new ItemStack(Getters.getGetters().getCurrentTutorial(name).getItem());
            ItemMeta data = i.getItemMeta();
            data.setDisplayName(" ");
            i.setItemMeta(data);
            player.setItemInHand(i);

            String lines[] = tACC(Getters.getGetters().getTutorialView(name).getMessage()).split("\\\\n");

            if(type == MessageType.TEXT) {
                for (String msg : lines) {
                    player.sendMessage(msg);
                }
            } else {
                String msg = "";
                for (String line: lines) {
                    if(msg.equalsIgnoreCase("")) {
                        msg = line;
                    } else {
                        msg += "\n" + line;
                    }
                }

                Text text = new Text(msg);
                text.send(player, ChatPosition.ACTION);
            }
            //Todo
            //player.sendMessage(tACC(Getters.getGetters().getTutorialView(name).getMessage()));
        }
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static TutorialUtils getTutorialUtils() {
        if (instance == null) {
            instance = new TutorialUtils();
        }
        return instance;
    }
}
