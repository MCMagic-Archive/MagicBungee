package us.mcmagic.magicbungee.handlers;

import net.md_5.bungee.api.CommandSender;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marc on 7/2/15
 */
public abstract class MagicCommand {
    protected List<String> aliases = new ArrayList<>();
    private Rank rank;
    public boolean tabCompletePlayers = false;

    public MagicCommand() {
        rank = Rank.GUEST;
    }

    public MagicCommand(Rank rank) {
        this.rank = rank;
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    public boolean canPerformCommand(Rank rank) {
        return rank.getRankId() >= this.rank.getRankId();
    }

    public List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    public boolean doTabCompletePlayers() {
        return tabCompletePlayers;
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (tabCompletePlayers) {
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                list.add(tp.getName());
            }
            if (args.length > 0) {
                String arg2 = args[args.length - 1];
                List<String> l2 = new ArrayList<>();
                for (String s : list) {
                    if (s.toLowerCase().startsWith(arg2.toLowerCase())) {
                        l2.add(s);
                    }
                }
                Collections.sort(l2);
                return l2;
            }
        }
        Collections.sort(list);
        return list;
    }

    public Rank getRank() {
        return rank;
    }
}