package clpqr.utils

import clpCore.getOuterVariable
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.variables.Variable
import org.chocosolver.solver.variables.impl.FixedRealVarImpl

class ExpressionParser<T : Variable>(
    private val chocoModel: Model,
    private val variables: Map<Var, T>,
    private val substitution: Substitution.Unifier
) : DefaultTermVisitor<CArExpression>() {
    override fun defaultValue(term: Term): CArExpression =
        error("Unsupported sub-expression: $term")

    override fun visitVar(term: Var): CArExpression {
        val actual = term.getOuterVariable(substitution)
        return asExpression(variables[actual] ?: error("No such a variable: $term"))
    }


    override fun visitInteger(term: Integer): CArExpression {
        val value = term.value.toDouble()
        return FixedRealVarImpl("$value",value, chocoModel)
    }

    override fun visitReal(term: Real): CArExpression {
        val value = term.value.toDouble()
        return FixedRealVarImpl("$value",value, chocoModel)
    }

    override fun visitStruct(term: Struct): CArExpression {
        when (term.arity) {
            2 -> when (term.functor) {
                "+" -> return binaryExpression(term, CArExpression::add)
                "-" -> return binaryExpression(term, CArExpression::sub)
                "*" -> return binaryExpression(term, CArExpression::mul)
                "^" -> throw NotImplementedError("This operator must still be implemented")
                "/" -> return binaryExpression(term, CArExpression::div)
                "min" -> return binaryExpression(term, CArExpression::min)
                "max" -> return binaryExpression(term, CArExpression::max)

            }
            1 -> when (term.functor) {
                "abs" -> return unaryExpression(term, CArExpression::abs)
                "-" -> return unaryExpression(term, CArExpression::neg)
                "sin" -> return unaryExpression(term, CArExpression::sin)
                "cos" -> return unaryExpression(term, CArExpression::cos)
                "tan" -> throw NotImplementedError("This operator must still be implemented")
                "exp" -> throw NotImplementedError("This operator must still be implemented")
                "pow" -> throw NotImplementedError("This operator must still be implemented")
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun asExpression(variable: Variable): CArExpression {
        return variable as CArExpression // this may fail!
    }

    private fun unaryExpression(struct: Struct, operator: (CArExpression) -> CArExpression): CArExpression {
        val first = struct[0].accept(this)
        return operator(first)
    }

    private fun binaryExpression(struct: Struct, operator: (CArExpression, CArExpression) -> CArExpression): CArExpression {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}

