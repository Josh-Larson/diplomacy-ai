package csci4511.algorithms;

import me.joshlarson.jlcommon.log.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class MonteCarlo<S, R> {

    public final int runTimeMillis;
    public final float selectConstant;

    public MonteCarlo(int runTimeMillis, int selectConstant) {
        this.runTimeMillis = runTimeMillis;
        this.selectConstant = selectConstant;
    }

    public final R run(S state) {
        StateNode data = new StateNode();
        data.state = state;
        data.onGraph = true;
        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < runTimeMillis) {
            StateNode currentNode = data;
            Log.i("Selecting");
            while (currentNode.onGraph) {
                currentNode = select(currentNode);
            }
            Log.i("Playing out");
            StateNode resultNode = playOut(currentNode);
            expand(currentNode, resultNode);
            Log.i("Backpropogating");
            while (currentNode.parent != null) {
                backpropogate(currentNode);
                currentNode = currentNode.parent;
            }
        }

        StateNode best = null;
        for (StateNode node : data.children) {
            if (node.score > (best != null ? best.score : 0))
                best = node;
        }
        return best != null ? best.results : null;
    }

    protected StateNode select(StateNode node) {
        StateNode selected = null;
        float bestScore = 0;
        for (StateNode child : getPossibleChildren(node)) {
            float score = child.score + (selectConstant * ((float) Math.sqrt(Math.log(node.visits) / child.visits)));
            if (score > bestScore) {
                bestScore = score;
                selected = child;
            }
        }
        return selected;
    }

    protected abstract List<StateNode> getPossibleChildren(StateNode parent);

    protected abstract StateNode playOut(StateNode node);

    protected void expand(StateNode parent, StateNode resultNode) {
        parent.children.add(resultNode);
        resultNode.parent = parent;
        resultNode.onGraph = true;
    }

    protected void backpropogate(StateNode node) {
        float score = 0;
        for (StateNode child : node.children) {
            score += child.score;
        }
        node.score = score / node.visits;
    }

    protected class StateNode {
        public S state;

        public float score = 1;
        public int visits = 0;

        public R results;

        public boolean onGraph = false;
        public StateNode parent;
        public ArrayList<StateNode> children = new ArrayList<>(20);
    }
}
