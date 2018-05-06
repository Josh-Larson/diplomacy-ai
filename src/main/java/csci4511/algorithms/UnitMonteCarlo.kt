package csci4511.algorithms

import csci4511.engine.ActionUtilities
import csci4511.engine.ExecutionUtilities
import csci4511.engine.data.Board
import csci4511.engine.data.Country
import csci4511.engine.data.Unit
import csci4511.engine.data.action.Action
import csci4511.engine.data.action.ActionHold
import csci4511.engine.resolve.ResolutionEngine
import me.joshlarson.jlcommon.log.Log
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class UnitMonteCarlo(val runTimeMillis: Long = TimeUnit.MINUTES.toMillis(2),
                     val selectConstant: Float = 0.4f,
                     val maxRandTurns: Int = 200,
                     val numRandGames: Int = 10) : Algorithm {

    var tree = ArrayList<StateNode>()
    var landmark = 0

    override fun determineActions(board: Board, country: Country, alliances: EnumSet<Country>): MutableList<Action> {
        landmark = board.units.size
        tree = ArrayList()

        val root = StateNode(board, country, board.units.toCollection(ArrayList()))
        tree.add(root)

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < runTimeMillis) {
            var currentNode = root
            var lastNode = currentNode

            while (tree.contains(currentNode)) {
                lastNode = currentNode
                currentNode = selectChildOf(currentNode)
            }

            val results = playOut(currentNode)
            expand(lastNode, results)

            currentNode = lastNode
            while (currentNode != root) {
                backpropogate(currentNode)
                currentNode = currentNode.parent!!
            }
        }

        val numActions = board.getUnitCount(country)
        val actions = ArrayList<Action>()
        var searchNode = root
        while (actions.size < numActions) {
            var best: StateNode? = null
            for (node in searchNode.children) {
                if (tree.contains(node) && node.score >= best?.score ?: 0.0)
                    best = node
            }
            actions.addAll(best?.actions ?: break)
            searchNode = best
        }

        return actions.toMutableList()
    }

    fun selectChildOf(state: StateNode): StateNode {
        state.visits++

        if (state.children.isEmpty()) {

            for (unit in state.remainingUnits) {

                if (unit.country == state.country) {
                    val actions = ActionUtilities.getActions(unit, EnumSet.of(unit.country))
                    if (actions.size == 0) {
                        actions.add(listOf(ActionHold(unit)))
                    }

                    for (action in actions) {
                        val boardClone = Board(state.board)
                        val usedUnits = action.mapTo(ArrayList(), { it.unit })

                        var remainingUnits = ArrayList<Unit>()
                        for (oldUnit in state.remainingUnits) {
                            if (!usedUnits.contains(oldUnit)) {
                                val newUnit = boardClone.getNode(oldUnit.node.name).garissoned!!
                                newUnit.node = oldUnit.node
                                remainingUnits.add(newUnit)
                            }
                        }

                        if (remainingUnits.size < landmark) {
                            landmark = remainingUnits.size
                            Log.i("New landmark! $landmark")
                        }

                        if (remainingUnits.isEmpty()) {
                            var node = state
                            val numActions = boardClone.units.size
                            val resolvedActions = ArrayList<Action>()
                            while (resolvedActions.size < numActions) {
                                resolvedActions.addAll(node.actions)
                                node = node.parent ?: break
                            }
                            ResolutionEngine().resolve(boardClone, resolvedActions)
                            remainingUnits = boardClone.units.toCollection(ArrayList())
                        }

                        val newCountry = if (remainingUnits.any { it.country == state.country }) state.country else remainingUnits.first().country

                        val newChild = StateNode(boardClone, newCountry, remainingUnits, usedUnits, action)
                        newChild.parent = state
                        state.children.add(newChild)
                    }
                }
            }
        }

        var selected = state
        var bestScore = 0.0
        for (child in state.children) {
            val score = child.score + selectConstant * Math.sqrt(Math.log((++child.visits).toDouble()) / state.visits)
            if (score >= bestScore) {
                bestScore = score
                selected = child
            }
        }

        return selected
    }

    fun playOut(state: StateNode): StateNode {
        val numActions = state.board.getUnitCount(state.country) - state.remainingUnits.filter { it.country == state.country }.size
        val parentActions = ArrayList<Action>()
        var node = state
        while (parentActions.size < numActions) {
            parentActions.addAll(node.actions)
            node = node.parent ?: break
        }

        val score = ExecutionUtilities.playRandom(state.board, parentActions, state.country, maxRandTurns, numRandGames)

        state.score = score

        return state
    }

    fun expand(parent: StateNode, resultNode: StateNode) {
        resultNode.parent = parent
        tree.add(resultNode)
    }

    fun backpropogate(state: StateNode) {
        var score = 0.0
        for (child in state.children) {
            score += child.score
        }
        state.score = score / state.visits
    }

    data class StateNode(
            val board: Board,
            val country: Country,
            val remainingUnits: ArrayList<Unit>,
            val actionedUnits: ArrayList<Unit> = ArrayList(),
            val actions: List<Action> = ArrayList(),
            var score: Double = 1.0,
            var visits: Int = 0
    ) {
        var parent: StateNode? = null
        var children: ArrayList<StateNode> = ArrayList()
    }
}
