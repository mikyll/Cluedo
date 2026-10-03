package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.player.Player;

/*
 * Answers the pending clue question, showing one of the asked clues to the player who asked.
 */
public class ActionShowClue extends GameAction {
    private Clue clue;

    public ActionShowClue(GameCluedo game, Player player, Clue clue) {
        super(game, player);

        this.clue = clue;
    }

    @Override
    public boolean isAllowed() {
        ClueQuestion question = getGame().getClueQuestion();
        return getGame().isWaitingForAnswer()
                && getPlayer().equals(question.getAnswerer())
                && question.getAnswerOptions().contains(clue);
    }

    @Override
    public void execute() {
        getGame().answerClueQuestion(getPlayer(), clue);
    }
}
