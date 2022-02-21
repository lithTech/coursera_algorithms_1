import java.util.Arrays;

public class TeamState {
    private final String team;
    private final int wins;
    private final int losses;
    private final int remains;
    private final int[] remainsWith;

    public TeamState(String team, int wins, int losses, int remains, int[] remainsWith) {
        this.team = team;
        this.wins = wins;
        this.losses = losses;
        this.remains = remains;
        this.remainsWith = Arrays.copyOf(remainsWith, remainsWith.length);
    }

    public String getTeam() {
        return team;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getRemains() {
        return remains;
    }

    public int[] getRemainsWith() {
        return Arrays.copyOf(remainsWith, remainsWith.length);
    }
}
