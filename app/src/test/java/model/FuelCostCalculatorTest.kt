package model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.roundToInt

/**
 * Class where tests on model are performed.
 * @author Marek Kawalski
 */
class FuelCostCalculatorTest {
    /**
     *@property listOfDistances List of distances
     */
    private lateinit var listOfDistances: ArrayList<Distance?>

    /**
     *@property listOfCars List of cars
     */
    private lateinit var listOfCars: ArrayList<Car?>

    /**
     *@property fuelCostCalculator instance of class where calculations are made
     */
    private lateinit var fuelCostCalculator: FuelCostCalculator

    /**
     * Set up
     *
     */
    @BeforeEach
    fun setUp() {
        listOfDistances = ArrayList()
        listOfCars = ArrayList()
        fuelCostCalculator = FuelCostCalculator(listOfDistances, listOfCars)
    }

    /**
     * Test calculating total cost of fuel based on average fuel consumption
     *
     * @param listOfCars
     * @param listOfDistances
     * @param arrayOfExpectedOutput
     */
    @ParameterizedTest
    @MethodSource("listOfCars and listOfDistances provider")
    fun `test calculating cars total cost of fuel based on average fuel consumption`(
        listOfCars: ArrayList<Car?>,
        listOfDistances: ArrayList<Distance?>,
        arrayOfExpectedOutput: DoubleArray,
    ) {
        //assign list of cars
        fuelCostCalculator.listOfCars = listOfCars
        //assign list of distances
        fuelCostCalculator.listOfDistances = listOfDistances

        //assign all distances to all cars
        fuelCostCalculator.listOfDistances?.forEach { distance ->
            distance?.listOfCars = listOfCars
        }

        //invoke function that calculates total fuel cost based on average fuel consumption
        fuelCostCalculator.calculateTotalCostOfFuel()

        //check if results are as expected
        fuelCostCalculator.listOfCars?.withIndex()?.forEach { (i, car) ->
            assertEquals(
                ((arrayOfExpectedOutput[i] * 100.0).roundToInt() / 100.0),
                ((car?.totalFuelCost?.times(100.0))?.roundToInt()?.div(100.0)),
                "${car?.carName} total fuel cost wasn't calculated properly!"
            )
        }
    }


    @ParameterizedTest
    @MethodSource("passenger and distances provider")
    fun `test calculating total distance covered by a passenger`(
        person: Person,
        listOfPassengersSelectedDistances: ArrayList<Distance>,
        expectedOutput: Double
    ) {
        person.listOfPassengersSelectedDistances = listOfPassengersSelectedDistances
        val output = fuelCostCalculator.calculatePassengerTotalDistance(person)
        assertEquals(
            expectedOutput,
            output,
            "${person.name} total distance wasn't calculated properly!"
        )
    }

    @ParameterizedTest
    @MethodSource("passenger, cars and listOfPassengersSelectedDistances provider")
    fun `test calculating passenger total fuel cost`(
        person: Person,
        listOfPassengersSelectedDistances: ArrayList<Distance>,
        listOfDistances: ArrayList<Distance?>,
        listOfCars: ArrayList<Car?>,
        expectedOutput: Double
    ) {
        person.listOfPassengersSelectedDistances = listOfPassengersSelectedDistances
        fuelCostCalculator.listOfCars = listOfCars
        fuelCostCalculator.listOfDistances = listOfDistances

        for ((i, distance) in listOfDistances.withIndex()) {
            for (car in listOfCars) {
                if (i % 2 == 0) {
                    distance?.listOfCars?.add(car)
                    distance?.passengersCount = i + 1
                }
            }
        }

        val output = fuelCostCalculator.calculatePassengerTotalCost(person)
        assertEquals(
            expectedOutput,
            output,
            "${person.name} total fuel cost wasn't calculated properly!"
        )
    }


    /**
     * Data provider companion object for FuelCostCalculatorTest
     *
     */
    private companion object {
        /**
         * Data provider object for 'test calculating total cost of fuel based on average fuel consumption` method
         *
         * @return
         */
        @JvmStatic
        fun `listOfCars and listOfDistances provider`(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    arrayListOf(Car("Audi", 10.00, 10.00)),
                    arrayListOf(Distance("to Warsaw", 100.00, 0)),
                    doubleArrayOf(100.00)
                ),
                arguments(
                    arrayListOf(
                        Car("Mercedes", 8.6, 8.30),
                        Car("Volvo", 7.55, 7.54),
                        Car("BMW", 12.34, 15.66),
                        Car("Audi", 5.67, 14.90),
                        Car("Maclaren", 10.00, 30.94)
                    ),
                    arrayListOf(Distance("to London", 100.00, 0)),
                    doubleArrayOf(
                        8.30 * 8.60 * 500 / 100,
                        7.55 * 7.54 * 500 / 100,
                        12.34 * 15.66 * 500 / 100,
                        5.67 * 14.90 * 500 / 100,
                        10.00 * 30.94 * 500 / 100
                    )
                ),
                arguments(
                    arrayListOf(
                        Car("Mercedes", 8.60, 8.30),
                        Car("Volvo", 7.55, 7.54),
                        Car("BMW", 12.34, 15.66),
                        Car("Audi", 5.67, 14.90),
                        Car("Maclaren", 10.00, 30.94)
                    ),
                    arrayListOf(
                        Distance("to London", 550.00, 0),
                        Distance("trip in London", 56.00, 1),
                        Distance("second trip in London", 105.00, 2),
                        Distance("Back home", 698.00, 3)
                    ),
                    doubleArrayOf(
                        8.30 * 8.60 * (550.00 + 56.00 + 105.00 + 698.00) * 5 / 100.00,
                        7.55 * 7.54 * (550.00 + 56.00 + 105.00 + 698.00) * 5 / 100.00,
                        12.34 * 15.66 * (550.00 + 56.00 + 105.00 + 698.00) * 5 / 100.00,
                        5.67 * 14.90 * (550.00 + 56.00 + 105.00 + 698.00) * 5 / 100.00,
                        10 * 30.94 * (550.00 + 56.00 + 105.00 + 698.00) * 5 / 100.00,
                    )
                )
            )
        }

        /**
         * Passenger and distances provider
         *
         * @return
         */
        @JvmStatic
        fun `passenger and distances provider`(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    Person("Marek", 0),
                    arrayListOf(Distance("to Warsaw", 100.00, 0)),
                    100.00
                ),
                arguments(
                    Person("Paulina", 0),
                    arrayListOf(
                        Distance("to Warsaw", 130.679, 0),
                        Distance("to Cracow", 16.50546, 1),
                        Distance("to London", 1750.63754, 2),
                        Distance("to Paris", 2421.4234, 3),
                        Distance("to Berlin", 3451.23435, 4),
                        Distance("to Barcelona", 2341.23423, 5)
                    ),
                    (130.679 + 16.50546 + 1750.63754 + 2421.4234 + 3451.23435 + 2341.23423)
                )
            )
        }

        /**
         * Passenger, cars and list of passengers selected distances provider
         *
         * @return
         */
        @JvmStatic
        fun `passenger, cars and listOfPassengersSelectedDistances provider`(): Stream<Arguments> {
            val listOfAllDistances = arrayListOf(Distance("to Warsaw", 100.00, 0))
            val listOfPasssengerDistances = arrayListOf(Distance("to Warsaw", 100.00, 0))
            return Stream.of(
                arguments(
                    Person("Marek", 0),
                    listOfAllDistances,
                    listOfAllDistances,
                    arrayListOf(Car("Mercedes", 100.00)),
                    100
                )
            )
            //@todo
        }

    }
}