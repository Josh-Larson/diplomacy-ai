package csci4511.algorithms

import csci4511.engine.ActionUtilities
import csci4511.engine.data.Board
import csci4511.engine.data.Country
import csci4511.engine.data.Unit
import csci4511.engine.data.UnitType
import csci4511.engine.data.action.Action
import csci4511.engine.data.action.ActionHold
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class UnitMonteCarlo(var runTimeMillis: Int = 100000,
                     var selectConstant: Float = 0.4f) : Algorithm {
    var tree = ArrayList<StateNode>()

    lateinit var alliances: EnumSet<Country>

    override fun determineActions(board: Board, country: Country, alliances: EnumSet<Country>): MutableList<Action> {
        this.alliances = alliances
        tree = ArrayList()

        val root = StateNode(board, board.units.filter { it.country == country }.mapTo(ArrayList(), { SimpleUnit(it) }))
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

        val actions = ArrayList<Action>()
        var searchNode = root
        while (searchNode.children.isNotEmpty()) {
            var best: StateNode? = null
            for (node in root.children) {
                if (tree.contains(node) && node.score >= best?.score ?: 0f)
                    best = node
            }
            actions.add(best?.action ?: break)
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
                val remainingUnits = ArrayList<SimpleUnit>(state.remainingUnits)
                remainingUnits.remove(unit)
                val boardUnit = unit.toUnitFromBoard(state.board)
                val actions = ActionUtilities.getActions(boardUnit, alliances)
                if (actions.size == 0) {
                    actions.add(listOf(ActionHold(boardUnit)))
                }
                for (action in actions) {
                    TODO("Needs clonable boards and units")
                    val boardClone: Board = state.board//.clone()
                    val actionUnit: Unit = boardUnit//.clone()
//                    actionUnit.action = action[0]
                    boardClone.addUnit(actionUnit)
                    val newChild = StateNode(boardClone, remainingUnits, unit, action[0])
                    newChild.parent = state
                    state.children.add(newChild)
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
        if (state.remainingUnits.isEmpty()) {
            //TODO Resolve actions, set board, set remaining units (maybe done in selectChildOf?)
            var parent = state.parent
            while (parent != null && parent.remainingUnits.size > state.remainingUnits.size) {
                state.remainingUnits.add(parent.actionedUnit ?: break)
                parent = parent.parent
            }
        }
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
            val remainingUnits: ArrayList<SimpleUnit>,
            val actionedUnit: SimpleUnit? = null,
            val action: Action? = null,
            var score: Float = 1f,
            var visits: Int = 0,
            var parent: StateNode? = null,
            var children: ArrayList<StateNode> = ArrayList()
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
