package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test

class CumulativeOneTest: BaseTest() {

    @Test
    fun testCumulativeAllDurationVariables() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1, H2], 1),
                ins([D1, D2], 2),
                cumulative([task(S1,D1,E1,H1, _), task(S2,D2,E2,H2,_)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }
}