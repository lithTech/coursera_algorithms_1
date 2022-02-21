import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private static final int VERTEX_S = 0;
    private static final int VERTEX_T = 1;
    private final Map<Integer, TeamState> teamStates;
    private final Map<String, Integer> teams;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In teamDef = new In(filename);
        int teamCount = Integer.parseInt(teamDef.readLine());
        teams = new LinkedHashMap<>(teamCount);
        teamStates = new LinkedHashMap<>(teamCount);
        int pos = -1;
        while (teamDef.hasNextLine()) {
            if (teamDef.isEmpty()) break;

            String team = teamDef.readString();

            teams.put(team, ++pos);

            int win = teamDef.readInt();
            int loss = teamDef.readInt();
            int remain = teamDef.readInt();
            int[] remainsWith = new int[teamCount];
            for (int j = 0; j < remainsWith.length; j++)
                remainsWith[j] = teamDef.readInt();

            teamStates.put(pos, new TeamState(team, win, loss, remain, remainsWith));
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        return getTeamState(team).getWins();
    }

    // number of losses for given team
    public int losses(String team) {
        return getTeamState(team).getLosses();
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return getTeamState(team).getRemains();
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        assertTeam(team2);
        int p = teams.get(team2);

        return getTeamState(team1).getRemainsWith()[p];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        Iterable<String> better = certificateOfElimination(team);
        return better != null && better.iterator().hasNext();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        List<String> betterTeams = getTrivialElimination(team);
        if (!betterTeams.isEmpty()) return betterTeams;

        List<String> result = getBetterTeams(teams.get(team));
        if (result.isEmpty()) return null;
        return result;
    }

    private void assertTeam(String team) {
        if (team == null || !teams.containsKey(team))
            throw new IllegalArgumentException("Invalid team specified");
    }

    private TeamState getTeamState(String team) {
        assertTeam(team);
        return teamStates.get(teams.get(team));
    }

    private List<String> getTrivialElimination(String team) {
        assertTeam(team);
        TeamState teamState = getTeamState(team);
        List<String> betterTeams = new ArrayList<>();
        for (String otherTeam : teams.keySet()) {
            if (team.equals(otherTeam)) continue;

            TeamState other = getTeamState(otherTeam);
            if (teamState.getRemains() + teamState.getWins() < other.getWins())
                betterTeams.add(otherTeam);
        }
        return betterTeams;
    }

    private List<String> getBetterTeams(int teamXPosition) {
        int totalTeams = teamStates.size();
        int vId = VERTEX_T;
        Map<Integer, Integer> teamToId = new HashMap<>();
        final FlowNetwork flowNetwork = new FlowNetwork(2 + totalTeams + totalTeams * totalTeams);

        // construct right side from teams to t
        int teamId = -1;
        int teamXMaxWins = teamStates.get(teamXPosition).getWins() + teamStates.get(teamXPosition).getRemains();
        while (vId < totalTeams + VERTEX_T) {
            teamId++;
            vId++;
            if (teamId == teamXPosition) continue;
            int maxToGo = teamXMaxWins - teamStates.get(teamId).getWins();
            if (maxToGo < 0) maxToGo = 0;
            flowNetwork.addEdge(new FlowEdge(vId, VERTEX_T, maxToGo));
            teamToId.put(teamId, vId);
        }
        // construct left side from s to games
        for (int j = 0; j < totalTeams; j++) {
            if (j == teamXPosition) continue;
            int[] remainsWith = teamStates.get(j).getRemainsWith();
            for (int k = j + 1; k < remainsWith.length; k++) {
                if (k == teamXPosition) continue;
                int games = remainsWith[k];
                flowNetwork.addEdge(new FlowEdge(VERTEX_S, ++vId, games));

                flowNetwork.addEdge(new FlowEdge(vId, teamToId.get(j), Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(vId, teamToId.get(k), Double.POSITIVE_INFINITY));
            }
        }
        return getBetterTeams(flowNetwork, teamXPosition, teamToId);
    }

    private List<String> getBetterTeams(FlowNetwork flowNetwork, int teamX, Map<Integer, Integer> teamToId) {
        FordFulkerson ff = new FordFulkerson(flowNetwork, VERTEX_S, VERTEX_T);
        List<String> betterTeams = new ArrayList<>();
        for (int teamId : teamStates.keySet()) {
            if (teamId == teamX) continue;
            if (ff.inCut(teamToId.get(teamId))) betterTeams.add(teamStates.get(teamId).getTeam());
        }
        return betterTeams;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
