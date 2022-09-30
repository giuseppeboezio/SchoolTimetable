package clpb

import clpCore.*
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Labeling: UnaryPredicate.NonBacktrackable<ExecutionContext>("labeling") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val chocoModel = chocoModel
        val vars = first.variables.distinct().toList()
        val varsMap = chocoModel.variablesMap(vars).toMutableMap()
        // if the labeling is used only with taut, it must fail
        if (varsMap.isEmpty())
            return replyFail()
        // set of variables not contained in the model
        val newVars = (vars.toSet() subtract varsMap.values.toSet()).toList()
        for(variable in newVars){
            chocoModel.boolVar(variable.completeName)
        }
        val newVarsMap = chocoModel.variablesMap(newVars)
        varsMap.putAll(newVarsMap)
        val solver = chocoModel.solver
        return replyWith(solver.solutions(varsMap).first())
    }
}