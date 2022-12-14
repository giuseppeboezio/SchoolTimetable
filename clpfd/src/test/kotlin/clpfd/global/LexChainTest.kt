package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

class LexChainTest: BaseTest() {

    @Test
    fun testLexChain() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([[X,Y],[Z,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(1),
                varOf("W") to intOf(1)
            )
        }
    }

    @Test
    fun testLexChainWithInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 10)), 
                lex_chain([[X,Y],[Z,1]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(1)
            )
        }
    }

    @Test
    fun testLexChainNWithInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y) :- 
                ins([X,Y], '..'(1, 10)), 
                lex_chain([[2],[X],[Y]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(2)
            )
        }
    }

    @Test
    fun testInvalidArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex(a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidInnerList() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([a,[Z,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidElemFirstList() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([[a,Y],[Z,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidElemSecondList() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([[X,Y],[a,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }
}