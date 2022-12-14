package clpqr.utils

import clpqr.Dummy
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.Variable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExpressionEvaluatorTest {

    private val evaluator = ExpressionEvaluator(mapOf(), Substitution.empty(), Dummy.context, Dummy.signature)

    private fun evaluateExpression(struct: Term, evaluator: ExpressionEvaluator<Variable>): Double =
        struct.accept(evaluator)

    @Test
    fun testSumOfNumbers(){

        val expression = Struct.of("+",Integer.of(10), Integer.of(5))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 15.0)
    }

    @Test
    fun testDiffOfNumbers(){

        val expression = Struct.of("-",Integer.of(10), Integer.of(5))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 5.0)
    }

    @Test
    fun testPowOfNumbers(){

        val expression = Struct.of("^",Integer.of(10), Integer.of(2))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 100.0)
    }

    @Test
    fun testDivOfNumbers(){

        val expression = Struct.of("/",Integer.of(10), Integer.of(2))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 5.0)
    }

    @Test
    fun testMinOfNumbers(){

        val expression = Struct.of("min",Integer.of(10), Integer.of(2))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 2.0)
    }

    @Test
    fun testMaxOfNumbers(){

        val expression = Struct.of("max",Integer.of(10), Integer.of(2))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 10.0)
    }

    @Test
    fun testOppositeNumber(){

        val expression = Struct.of("-",Integer.of(10))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, -10.0)
    }

    @Test
    fun testSin(){

        val expression = Struct.of("sin",Integer.of(0))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 0.0)
    }

    @Test
    fun testCos(){

        val expression = Struct.of("cos",Integer.of(0))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 1.0)
    }

    @Test
    fun testTan(){

        val expression = Struct.of("tan",Integer.of(0))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 0.0)
    }

    @Test
    fun testExp(){

        val expression = Struct.of("exp",Integer.of(0))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 1.0)
    }

    @Test
    fun testPow(){

        val expression = Struct.of("pow",Integer.of(5))
        val value = evaluateExpression(expression, evaluator)
        assertEquals(value, 25.0)
    }

    @Test
    fun testSumVariableAndDouble(){

        val model = Model()
        val prologVar = Var.of("x")
        val chocoVar = model.realVar("x", 8.5)
        val varsMap = mapOf(prologVar to chocoVar)
        val expression = Struct.of("+", prologVar, Real.of(1.5))
        val parser = ExpressionEvaluator(
            varsMap, Substitution.empty(), Dummy.context, Dummy.signature
        )
        val value = expression.accept(parser)

        assertEquals(value, 10.0)
    }

    @Test
    fun testNestedOperators(){

        val model = Model()
        val prologVar = Var.of("x")
        val chocoVar = model.realVar("x", 8.0)
        val varsMap = mapOf(prologVar to chocoVar)
        val expression = Struct.of(
            "*",
            Struct.of("abs", Integer.of(-3)),
            Struct.of("pow", Real.of(2.0))
        )
        val parser = ExpressionEvaluator(
            varsMap, Substitution.empty(), Dummy.context, Dummy.signature
        )
        val value = expression.accept(parser)

        assertEquals(value, 12.0)
    }

    @Test
    fun testExpressionWithArcTan(){

        val expression = Struct.of("arctan",Integer.of(10))
        assertThrows<TypeError> {
            evaluateExpression(expression, evaluator)
        }
    }

    @Test
    fun testNotExistingVariable(){

        val expression = Var.of("X")
        assertThrows<DomainError> {
            evaluateExpression(expression, evaluator)
        }
    }
}