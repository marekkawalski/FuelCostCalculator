package model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class FuelCostCalculatorTest {
    private var listOfDistances = ArrayList<Distance?>()
    private var listOfCars = ArrayList<Car?>()
    private lateinit var fuelCostCalculator: FuelCostCalculator

    @BeforeEach
    fun setUp() {
        fuelCostCalculator = FuelCostCalculator(listOfDistances, listOfCars)

    }

    @ParameterizedTest
    @MethodSource("carsProvider")
    fun `test calculating total cost of fuel based on average fuel consumption`(
        listOfCars: ArrayList<Car?>,
        listOfDistances: ArrayList<Distance?>,
        expectedOutput: Double
    ) {

        fuelCostCalculator.listOfCars = listOfCars
        fuelCostCalculator.listOfDistances = listOfDistances

        fuelCostCalculator.calculateTotalCostOfFuel()
        this.listOfCars.forEach { car ->
            assertEquals(
                car?.totalFuelCost,
                expectedOutput,
                "Total fuel cost wasn't calculated properly!"
            )
        }
    }

    private companion object {
        @JvmStatic
        fun carsProvider(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    arrayListOf(Car("Audi", 10.00, 10.00)),
                    arrayListOf(Distance("to Warsaw", 100.00, 0)),
                    100.00
                ),
                //@todo
                arguments(
                    arrayListOf(
                        Car("Mercedes", 8.6, 8.30),
                        Car("Volvo", 7.55, 7.54),
                        Car("BMW", 12.34, 15.66),
                        Car("Audi", 5.67, 14.90),
                        Car("Maclaren", 10.00, 30.94)
                    ),
                    arrayListOf(Distance("to Warsaw", 100.00, 0)),
                    100.00
                ),
                arguments(
                    arrayListOf(
                        Car("Mercedes", 8.6, 8.30),
                        Car("Volvo", 7.55, 7.54),
                        Car("BMW", 12.34, 15.66),
                        Car("Audi", 5.67, 14.90),
                        Car("Maclaren", 10.00, 30.94)
                    ),
                    arrayListOf(
                        Distance("to Warsaw", 100.00, 0),
                        Distance("to Warsaw", 100.00, 1),
                        Distance("to Warsaw", 100.00, 2),
                        Distance("to Warsaw", 100.00, 3)
                    ),
                    100.00
                )

            )
        }
    }
}