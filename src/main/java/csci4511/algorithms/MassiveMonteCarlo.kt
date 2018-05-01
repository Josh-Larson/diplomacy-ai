package csci4511.algorithms

import java.util.*
import java.util.concurrent.ThreadLocalRandom

abstract class MassiveMonteCarlo<S, R>(var runTimeMillis: Int = 30000,
                                       var selectConstant: Float = 0.4f) {

    val map: HashMap<S, StateNode<S, R>> = HashMap()
    private var lastState: StateNode<S, R>? = null

    fun step(state: S): R? {
        var node = map[state]
        if (node == null) {
            node = StateNode(state, parent = lastState)
            map.put(state, node)
        }
        lastState = node
        return monteCarlo(node)
    }

    private fun monteCarlo(rootNode: StateNode<S, R>): R? {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < runTimeMillis) {
            var currentNode = rootNode
            var lastNode = currentNode
            while (map.containsValue(currentNode)) {
                lastNode = currentNode
                currentNode = select(currentNode)
            }

            val results = playOut(currentNode)

            expand(lastNode, results)

            currentNode = lastNode
            while (currentNode != rootNode) {
                backpropogate(currentNode)
                currentNode = currentNode.parent!!
            }
        }

        var best: StateNode<S, R>? = null
        for (node in rootNode.children) {
            if (node.score > (if (best != null) best.score else 0f))
                best = node
        }
        return if (best != null) best.results else null
    }

    protected open fun select(node: StateNode<S, R>): StateNode<S, R> {
        var selected: StateNode<S, R> = node
        var bestScore = 0f

        val childStates = node.children.mapTo(ArrayList()) { it.state }
        getPossibleChildStates(node.state).filterNotTo(childStates) { childStates.contains(it) }

        for (state in childStates) {
            val childNode = map[state] ?: StateNode(state)
            val score = childNode.score + selectConstant * Math.sqrt(Math.log(node.visits.toDouble()) / childNode.visits).toFloat()
            if (score >= bestScore) {
                bestScore = score
                selected = childNode
            }
        }

        selected.parent = selected.parent ?: node
        return selected
    }

    protected open fun playOut(startPoint: StateNode<S, R>): StateNode<S, R> {
        var firstNode = startPoint
        var currentState = startPoint.state

        while (getScore(currentState) < 0) {
            val children = getPossibleChildStates(currentState)
            val pick = ThreadLocalRandom.current().nextInt(0, children.size)
            currentState = children[pick]

            if (firstNode == startPoint) firstNode = StateNode(currentState)
        }

        firstNode.score = getScore(currentState)
        return firstNode
    }

    protected open fun expand(parent: StateNode<S, R>, resultNode: StateNode<S, R>) {
        parent.children.add(resultNode)
        resultNode.parent = parent
        map.put(resultNode.state, resultNode)
    }

    protected open fun backpropogate(node: StateNode<S, R>) {
        var score = 0f
        for (child in node.children) {
            score += child.score
        }
        node.score = score / node.visits
    }

    abstract fun getScore(state: S): Float

    abstract fun getPossibleChildStates(state: S): List<S>

    data class StateNode<S, R>(
            var state: S,
            var results: R? = null,
            var score: Float = 1f,
            var visits: Int = 0,
            var parent: StateNode<S, R>? = null,
            var children: ArrayList<StateNode<S, R>> = ArrayList(20)
    )
}