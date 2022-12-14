package clpqr.utils

import clpCore.getOuterVariable
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

internal class EquationDetector(
    private val substitution: Substitution.Unifier,
    private val equations: MutableMap<Var,List<Var>>,
) : DefaultTermVisitor<MutableMap<Var,List<Var>>>() {

    override fun defaultValue(term: Term) = equations

    override fun visitTuple(term: Tuple): MutableMap<Var,List<Var>>{
        val termTuple = term.unfoldedSequence
        val globalMap = equations
        for(elem in termTuple){
            globalMap.putAll(elem.accept(this))
        }
        return globalMap
    }

    override fun visitStruct(term: Struct): MutableMap<Var,List<Var>>{
        val struct = term.castToStruct()
        val firstTerm = struct.args[0]
        val secondTerm = struct.args[1]
        return if(struct.functor.let { it == "=" || it == "=:=" } && struct.arity == 2){
            if(firstTerm is Var){
                val firstOuter = firstTerm.getOuterVariable(substitution)
                val otherVars = secondTerm.variables.map { it.getOuterVariable(substitution) }.toList()
                mutableMapOf(firstOuter to otherVars)
            }else if(secondTerm is Var){
                val secondOuter = secondTerm.getOuterVariable(substitution)
                val otherVars = firstTerm.variables.map { it.getOuterVariable(substitution) }.toList()
                mutableMapOf(secondOuter to otherVars)
            } else
                equations
        } else
            equations
    }

}