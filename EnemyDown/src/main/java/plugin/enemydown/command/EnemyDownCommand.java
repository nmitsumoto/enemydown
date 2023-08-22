package plugin.enemydown.command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import plugin.enemydown.EnemyDown;
import plugin.enemydown.PlayerScoreData;
import plugin.enemydown.data.ExecutingPlayer;
import plugin.enemydown.mapper.data.PlayerScore;

public class EnemyDownCommand extends BaseCommand implements Listener {

  public static final int GAME_TIME = 50;

  public static final String NORMAL = "normal";
  public static final String NONE = "none";
  public static final String LIST = "list";
  public static final String GAMESTART = "gamestart";


  private final EnemyDown enemyDown;
  private final PlayerScoreData playerScoreData = new PlayerScoreData();

  private final List<ExecutingPlayer> executingPlayerList = new ArrayList<>();
  private final List<PlayerScore> playerScoreList  = new ArrayList<>();
  private final List<Entity> LivingEntityList = new ArrayList<>();

  public EnemyDownCommand(EnemyDown enemyDown) {

    this.enemyDown = enemyDown;
  }
  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args){
    if (args.length == 1 && LIST.equals(args[0])) {
      sendPlayerScoreList(player);
      return false;
    }
    String difficulty = getDifficulty(player, args);
    if(difficulty.equals(NONE)){
      return false;
    }
    ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);

    initPlayerStatus(player);

    gamePLay(player, nowExecutingPlayer, difficulty);
    return true;
  }
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreData.selectlist();

    for(PlayerScore playerScore : playerScoreList){
      player.sendMessage(playerScore.getId() + " | "
          + playerScore.getPlayerName() + " | "
          + playerScore.getScore() + " | "
          + playerScore.getRegisteredDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label,
      String[] args) {
    return false;
  }

   public static String getDifficulty(Player player, String[] args) {
    if (args.length == 1 && (NORMAL.equals(args[0]) || GAMESTART.equals(args[0]))){
      return args[0];
  }
    player.sendMessage(ChatColor.RED + "実行できません。コマンドの引数の一つ目に難易度指定が必要です。[normal,gamestart,list]");
    return NONE;
}

  private ExecutingPlayer getPlayerScore(Player player) {
    ExecutingPlayer executingPlayer = new ExecutingPlayer(player.getName());

    if (executingPlayerList.isEmpty()) {
      executingPlayer = addNewPlayer(player);
    } else {
    executingPlayer =  executingPlayerList.stream()
        .findFirst()
        .map(ps -> ps.getPlayerName().equals(player.getName())
              ? ps
              : addNewPlayer(player)).orElse(executingPlayer);
    }
    executingPlayer.setGameTime(GAME_TIME);
    executingPlayer.setScore(0);
    removePotionEffect(player);
    return executingPlayer;
    }

  private static void removePotionEffect(Player player) {
    player.getActivePotionEffects().stream()
        .map(PotionEffect::getType)
        .forEach(player::removePotionEffect);
  }

  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newPlayer = new ExecutingPlayer(player.getName());
    executingPlayerList.add(newPlayer);
    return newPlayer;
  }

  @EventHandler
  public void EntityPickup (EntityPickupItemEvent e) {
    LivingEntity entity = e.getEntity();
    Player player = entity.getKiller();
    boolean isLivingEntity = LivingEntityList.stream()
        .anyMatch(entity::equals);
    if (Objects.isNull(player) || !isLivingEntity) {
      return;
    }
    for (PlayerScore playerScore : playerScoreList) {
      if(playerScore.getPlayerName().equals(player.getName()) && this.executingPlayerList.get(0).getGameTime() > 0){
      int point = 0;
      if (EntityType.CHICKEN.equals(entity.getType()) && this.executingPlayerList.get(0).getGameTime() < 300) {
        point = 20;
      } else if (EntityType.CHICKEN.equals(entity.getType())) {
        point = 10;
      } else if (EntityType.RABBIT.equals(entity.getType())){
        point = 100;
      }
        playerScore.setScore(playerScore.getScore() + point);
        player.sendMessage("敵を倒した。現在のスコアは" + playerScore.getScore() + "点をゲット。");
    }
  }
  }
    public void gamePLay(Player player, ExecutingPlayer nowExecutingPlayer,String difficulty) {
    Bukkit.getScheduler().runTaskTimer(enemyDown, Runnable -> {
      if (nowExecutingPlayer.getGameTime() <= 0) {
        Runnable.cancel();
        player.sendTitle("ゲーム終了",
            nowExecutingPlayer.getPlayerName() + "合計 " + nowExecutingPlayer.getScore() + "点！",
            0, 60, 0);

        removePotionEffect(player);
        playerScoreData.insert(
        new PlayerScore(nowExecutingPlayer.getPlayerName(),
            nowExecutingPlayer.getScore(),
            difficulty));
        return;
      }
      player.sendMessage("残り"+ nowExecutingPlayer.getGameTime()+"秒。現在"+nowExecutingPlayer.getScore()+"点！");
      nowExecutingPlayer.setGameTime(nowExecutingPlayer.getGameTime() - 10);
    }, 0, 10 * 20);

      player.sendTitle("ゲームスタート",
          nowExecutingPlayer.getPlayerName() + "さん頑張ってください。現在 " + nowExecutingPlayer.getScore() + "点！",
          0, 100, 0);
  }
  private static void initPlayerStatus (Player player){
    player.setHealth(20);
    player.setFoodLevel(20);

    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
    inventory.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
    inventory.setBoots(new ItemStack(Material.NETHERITE_BOOTS));
    inventory.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
    inventory.setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
  }
}


