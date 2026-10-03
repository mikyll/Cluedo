package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;

/*
 * Asks a clue question about the room the player is in.
 */
public class ActionAskClue extends GameAction {
    private Character character;
    private Weapon weapon;
    private ClueQuestion result;

    public ActionAskClue(GameCluedo game, Player player, Character character, Weapon weapon) {
        super(game, player);

        this.character = character;
        this.weapon = weapon;
    }

    public ClueQuestion getResult() {return result;}

    @Override
    public boolean isAllowed() {
        return getGame().canAskClue(getPlayer());
    }

    @Override
    public void execute() {
        GameCluedo game = getGame();
        this.result = game.makeAccusation(getPlayer(), game.getRoomOf(getPlayer()), character, weapon);
    }
}
