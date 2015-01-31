package io.snw.tutorial.conversation;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.api.AddViewEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.DataLoading;
import io.snw.tutorial.data.Getters;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ViewConversation {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private String name;

    public void createNewView(Player player, String tutorialName) {
        this.name = tutorialName;
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                        // .withPrefix(new Prefix())
                .withFirstPrompt(new Welcome())
                .withEscapeSequence("/quit")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("You must be in game!");
        factory.buildConversation(player).begin();
    }

    private class Welcome extends MessagePrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            context.setSessionData("name", name);
            return ChatColor.translateAlternateColorCodes('&',
                                                          "&6-------------------------------\n&8>&fWelcome to the &bServerTutorial&f view creation!\n&8>&7This will guide you adding a view to your tutorial\n&8>&7Currently adding view to the tutorial &b"
                                                          + name + "&7!");
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ChooseMessageType();
        }
    }

    private class ChooseMessageType extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7First, Choose a MessageType: META, TEXT or ACTION\n"
                                                               + "&8>&6META &7- uses the item name\n"
                                                               + "&8>&6TEXT &7- normal text message\n"
                                                               + "&8>&6ACTION &7- uses 1.8 action text messages\n"
                                                               + "&8>&7>&6> &7Type a MessageType to continue!");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("text") || input.equalsIgnoreCase("meta")) {
                context.setSessionData("messagetype", input.toUpperCase());
                return new Message();
            }
            return new ChooseMessageType();
        }
    }

    private class Message extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7>&6> &7Type what message this view should have:");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("message", input);
            return new FinishMessage();
        }
    }

    private class FinishMessage extends MessagePrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7The view for tutorial &b" + name
                                                               + "&7 has been successfully created!\n&8>&7It is a &f" + context
                    .getSessionData("messagetype").toString() + " &7based view with message &f" + context.getSessionData("message").toString()
                                                               + "&7!\n&6-------------------------------");
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            Player player = (Player) context.getForWhom();
            String
                    location =
                    plugin.getServer().getPlayer(player.getName()).getLocation().getWorld().getName() + "," + plugin.getServer()
                            .getPlayer(player.getName()).getLocation().getX() + "," + plugin.getServer().getPlayer(player.getName()).getLocation()
                            .getY() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getZ() + "," + plugin.getServer()
                            .getPlayer(player.getName()).getLocation().getYaw() + "," + plugin.getServer().getPlayer(player.getName()).getLocation()
                            .getPitch();
            String message = context.getSessionData("message").toString();
            String messageType = context.getSessionData("messagetype").toString();
            String name = context.getSessionData("name").toString();
            int viewID = 1;
            while (DataLoading.getDataLoading().getData().get("tutorials." + context.getSessionData("name") + ".views." + viewID) != null) {
                viewID++;
            }
            try {
                DataLoading.getDataLoading().getData().set("tutorials." + name + ".views." + viewID + ".message", message);
                DataLoading.getDataLoading().getData().set("tutorials." + name + ".views." + viewID + ".messagetype", messageType);
                DataLoading.getDataLoading().getData().set("tutorials." + name + ".views." + viewID + ".location", location);
                DataLoading.getDataLoading().saveData();
                Caching.getCaching().reCasheTutorials();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AddViewEvent event = new AddViewEvent(player, Getters.getGetters().getTutorial(name), Getters.getGetters().getTutorialView(name, viewID));
            plugin.getServer().getPluginManager().callEvent(event);
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }
}