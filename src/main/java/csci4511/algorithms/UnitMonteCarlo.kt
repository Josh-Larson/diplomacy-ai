package csci4511.algorithms

import csci4511.engine.ActionUtilities
import csci4511.engine.data.Board
import csci4511.engine.data.Country
import csci4511.engine.data.Unit
import csci4511.engine.data.UnitType
import csci4511.engine.data.action.Action
import csci4511.engine.data.action.ActionHold
import csci4511.engine.resolve.ResolutionEngine
import me.joshlarson.jlcommon.log.Log
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class UnitMonteCarlo(var runTimeMillis: Int = 100000,
                     var selectConstant: Float = 0.4f) : Algorithm {
    var tree = ArrayList<StateNode>()

    var landmark = 100

    override fun determineActions(board: Board, country: Country, alliances: EnumSet<Country>): MutableList<Action> {
        tree = ArrayList()

//        val root = StateNode(board, board.units.filter { it.country == country }.mapTo(ArrayList(), { SimpleUnit(it) }))
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

        val numActions = board.units.filter { it.country == country }.size
        val actions = ArrayList<Action>()
        var searchNode = root
        while (actions.size < numActions) {
            var best: StateNode? = null
            for (node in searchNode.children) {
                if (tree.contains(node) && node.score >= best?.score ?: 0f)
                    best = node
            }
            actions.addAll(best?.actions ?: break)
            searchNode = best
        }

        return actions.toMutableList()
    }

    fun selectChildOf(state: StateNode): StateNode {
        var selected = state
        var bestScore = 0f

        state.visits++

        if (state.children.isEmpty()) {

            for (unit in state.remainingUnits) {

                if (unit.country == state.country) {
                    val actions = ActionUtilities.getActions(unit, EnumSet.of(unit.country))
                    if (actions.size == 0) {
                        actions.add(listOf(ActionHold(unit)))
                    }

                    for (action in actions) {
                        val usedUnits = action.mapTo(ArrayList(), { it.unit })

                        var remainingUnits = ArrayList<Unit>()
                        for (oldUnit in state.remainingUnits) {
                            if (!usedUnits.contains(oldUnit)) {
                                val newUnit = Unit(oldUnit)
                                newUnit.node = oldUnit.node
                                remainingUnits.add(newUnit)
                            }
                        }

                        if (remainingUnits.size < landmark) {
                            landmark = remainingUnits.size
                            Log.i("New landmark! $landmark")
                        }

                        val boardClone = Board(state.board)

                        if (remainingUnits.isEmpty()) {
                            var node = state
                            val resolvedActions = ArrayList<Action>()
                            while (node.remainingUnits.size > resolvedActions.size) {
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

        for (child in state.children) {
            val score = child.score + selectConstant * Math.sqrt(Math.log((++child.visits).toDouble()) / state.visits).toFloat()
            if (score >= bestScore) {
                bestScore = score
                selected = child
            }
        }

        return selected
    }

    fun playOut(state: StateNode): StateNode {
        //TODO Actually score this right
        state.score = ThreadLocalRandom.current().nextFloat()
        return state
    }

    fun expand(parent: StateNode, resultNode: StateNode) {
        resultNode.parent = parent
        tree.add(resultNode)
    }

    fun backpropogate(state: StateNode) {
        var score = 0f
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
            var score: Float = 1f,
            var visits: Int = 0
    ) {
        var parent: StateNode? = null
        var children: ArrayList<StateNode> = ArrayList()
    }

    data class StateNodeSimple(
            val board: Board,
            val remainingUnits: ArrayList<SimpleUnit>,
            val actionedUnit: SimpleUnit? = null,
            val action: Action? = null,
            var score: Float = 1f,
            var visits: Int = 0,
            var parent: StateNodeSimple? = null,
            var children: ArrayList<StateNodeSimple> = ArrayList()
    )

    data class SimpleUnit(
            val type: UnitType,
            val country: Country,
            val location: String) {
        constructor(unit: Unit) : this(unit.type, unit.country, unit.node.name)

        fun toUnitFromBoard(board: Board): Unit {
            return board.getNode(location).garissoned!!
        }
    }
}
