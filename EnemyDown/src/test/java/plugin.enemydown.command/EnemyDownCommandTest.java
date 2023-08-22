package plugin.enemydown.command;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import plugin.enemydown.EnemyDown;

class EnemyDownCommandTest {

  @Mock
  EnemyDown enemyDown;

  @Test
  void getDifficultyに渡す引数のargsの最初の文字列がnormalの時にnormalの文字列が返ること() {
    EnemyDownCommand sut = new EnemyDownCommand(enemyDown);
    Player player = Mockito.mock(Player.class);

    String actual = sut.getDifficulty(player,new String[]{"normal"});
    Assertions.assertEquals("normal",actual);
  }
}

