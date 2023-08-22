package plugin.enemydown.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {
      return onExecutePlayerCommand(player, command, label, args);
    } else {
      return onExecuteNPCCommand(sender, command, label, args);
    }
  }

  public abstract boolean onExecutePlayerCommand(Player player, Command command, String label,
      String[] args);

  public abstract boolean onExecuteNPCCommand(CommandSender sender, Command command, String label,
      String[] args);
}

