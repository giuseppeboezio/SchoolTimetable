package clpVarCreation

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExpressionParserTest {

    private val LOWER_BOUND = 1
    private val UPPER_BOUND = 10
    private val varNames = listOf("X", "Y", "Z")

    private val model = Model().let {
        val chocoModel = it
        varNames.map { chocoModel.intVar(it, LOWER_BOUND, UPPER_BOUND) }
        chocoModel
    }
    private val chocoVars = model.vars
    private val tuPrologVars = varNames.map { Var.of(it) }

    private val mapVars = (tuPrologVars zip chocoVars).toMap()
    private val expressionParser = ExpressionParser(model, mapVars)


    @Test
    fun testVisitVar() {

        // variable X is used
        val variable = tuPrologVars[0]
        val arithmeticExpr = variable.accept(expressionParser)
        // check whether the expression is a variable
        assertTrue(arithmeticExpr.isExpressionLeaf)

    }

    @Test
    fun testVisitInteger() {

        val integer = Integer.of(2)
        val arithmeticExpr = integer.accept(expressionParser)
        // if the expression is an integer it has no children
        assertEquals(arithmeticExpr.noChild, 0)

    }

    @Test
    fun testVisitStructPlus() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("+", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructAbs() {

        val variable = tuPrologVars[0]
        val struct = Struct.of("abs", variable)
        val arithmeticExpr = struct.accept(expressionParser)
        // The only child of the expression is the argument of abs
        assertEquals(arithmeticExpr.noChild, 1)

    }

    @Test
    fun testVisitStruct() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val varZ = tuPrologVars[2]

        // +(*(X,Y),div(Z,2))
        val firstArgument = Struct.of("*", varX, varY)
        val secondArgument = Struct.of("div", varZ, Integer.of(2))
        val struct = Struct.of("+", firstArgument , secondArgument)

        val arithmeticExpr = struct.accept(expressionParser)
        assertEquals(arithmeticExpr.noChild, 2)

    }

}