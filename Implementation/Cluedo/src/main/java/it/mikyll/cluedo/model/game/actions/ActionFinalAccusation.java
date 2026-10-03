package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionFinalAccusation extends GameAction {
    private Room room;
    private Character character;
    private Weapon weapon;
    private boolean correct;

    public ActionFinalAccusation(GameCluedo game, Player player, Room room, Character character, Weapon weapon) {
        super(game, player);

        this.room = room;
        this.character = character;
        this.weapon = weapon;
    }

    public boolean isCorrect() {return correct;}

    @Override
    public boolean isAllowed() {
        return getGame().canMakeFinalAccusation(getPlayer());
    }

    @Override
    public void execute() {
        this.correct = getGame().makeFinalAccusation(getPlayer(), character, weapon, room);
    }
}
