package plugin.enemydown.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutingPlayer {

  private String playerName;
  private int score;
  public int gameTime;
  public int item_id;

  public ExecutingPlayer(String playerName) {
    this.playerName = playerName;


    }

  }


