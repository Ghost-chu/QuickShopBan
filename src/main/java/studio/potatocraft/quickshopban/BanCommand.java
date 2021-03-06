package studio.potatocraft.quickshopban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.command.CommandHandler;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.List;
import java.util.Optional;

public class BanCommand implements CommandHandler<Player> {
    private final QuickShopBan plugin;
    public BanCommand (QuickShopBan plugin){
        this.plugin = plugin;
    }
    @Override
    public void onCommand(Player commandSender, String s, String[] strings) {
        if(strings.length < 1){
            MsgUtil.sendMessage(commandSender,MsgUtil.getMessage("wrong-args", commandSender));
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);
        if(!player.hasPlayedBefore()){
            MsgUtil.sendMessage(commandSender,plugin.getConfig().getString("lang.player-not-exists"));
            return;
        }

        final BlockIterator bIt = new BlockIterator(commandSender, 10);

        if (!bIt.hasNext()) {
            MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));
            return;
        }

        while (bIt.hasNext()) {
            Block b = bIt.next();
           Optional<Shop> shop = QuickShopAPI.getShopAPI().getShop(b.getLocation());
            if (shop.isPresent()) {
                Shop qshop = shop.get();
                ConfigurationSection extra = qshop.getExtra(plugin);
                List<String> bannedPlayers = extra.getStringList("bannedplayers");
                if(!bannedPlayers.contains(player.getUniqueId().toString())){
                    bannedPlayers.add(player.getUniqueId().toString());
                }
                qshop.setExtra(plugin,extra);
                MsgUtil.sendMessage(commandSender,MsgUtil.fillArgs(plugin.getConfig().getString("lang.ban-success"), player.getName()));
                return;
            }
        }
        MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));

    }
}
