package model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.roundToInt

/**
 * Class where tests on model are performed.
 * @author Marek Kawalski
 * @version 1.2
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
     * Method sets new model as well as its properties for each test.
     *
     */
    @BeforeEach
    fun setUp() {
        listOfDistances = ArrayList()
        listOfCars = ArrayList()
        fuelCostCalculator = FuelCostCalculator(listOfDistances, listOfCars)
    }

    /**
     * Method tests calculating total cost of fuel based on average fuel consumption
     *
     * @param listOfCars list of all cars
     * @param listOfDistances list of all distance
     * @param arrayOfExpectedOutput array of expected output
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

    /**
     * Method tests calculating total distance covered by a passenger
     *
     * @param person whose distance is to be calculated
     * @param listOfPassengersSelectedDistances list of distances on which the passenger was present
     * @param expectedOutput expected output
     */
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

    /**
     * Method tests calculating passenger total fuel cost
     *
     * @param person passenger for whom cost is to be calculated
     * @param listOfDistances list of all distances
     * @param listOfCars list of all cars
     * @param expectedOutput expected output
     */
    @ParameterizedTest
    @MethodSource("passenger, cars and listOfPassengersSelectedDistances provider")
    fun `test calculating passenger total fuel cost`(
        person: Person,
        listOfDistances: ArrayList<Distance?>,
        listOfCars: ArrayList<Car?>,
        expectedOutput: Double
    ) {
        fuelCostCalculator.listOfCars = listOfCars
        fuelCostCalculator.listOfDistances = listOfDistances

        val output = fuelCostCalculator.calculatePassengerTotalCost(person)
        assertEquals(
            expectedOutput,
            output,
            "${person.name} total fuel cost wasn't calculated properly!"
        )
    }

    /**
     * Method tests if exception is thrown when trying to divide by zero.
     *
     */
    @Test
    fun `test of exception when dividing by zero, should throw an exception`() {
        val person = Person("Marek", 0)
        person.listOfPassengersSelectedDistances = arrayListOf(
            Distance(
                "to Warsaw", 100.00, 0, arrayListOf(
                    Car("Mercedes", 8.6, 8.30),
                ), 0
            )
        )
        try {
            fuelCostCalculator.calculatePassengerTotalCost(person)
            fail("An exception should be thrown!")
        } catch (ex: FuelCostCalculatorException) {
        }
    }

    /**
     * Method tests if exception is thrown when dividing by double value. It should not throw an exception.
     *
     */
    @Test
    fun `test of exception when dividing by positive value, should not throw an exception`() {
        val person = Person("Marek", 0)
        this.fuelCostCalculator.listOfDistances = arrayListOf(
            Distance(
                "to Warsaw", 100.00, 10, arrayListOf(
                    Car("Mercedes", 8.6, 8.30),
                ), 0
            )
        )
        person.listOfPassengersSelectedDistances = arrayListOf(
            Distance(
                "to Warsaw", 100.00, 10, arrayListOf(
                    Car("Mercedes", 8.6, 8.30),
                ), 0
            )
        )
        try {
            fuelCostCalculator.calculatePassengerTotalCost(person)
        } catch (ex: FuelCostCalculatorException) {
            fail("An exception should not be thrown!")

        }
    }

    /**
     * Data provider companion object for FuelCostCalculatorTest
     *
     */
    private companion object {
        /**
         * Data provider object for 'test calculating total cost of fuel based on average fuel consumption` method
         *
         * @return stream of arguments which consist of: list of cars, list of distances, list of answers
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
         * Data provider for calculating total distance covered by a passenger
         *
         * @return stream of arguments, where each argument consists of: passenger, list of distances, correct value
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
         * Data provides for method which calculates total cost of fuel for a passenger
         *
         * @return stream of arguments, where each argument represents: passenger, list of distances,list of cars, a correct answer
         */
        @JvmStatic
        fun `passenger, cars and listOfPassengersSelectedDistances provider`(): Stream<Arguments> {
            val mercedes = Car("Mercedes", 140.00)
            val volvo = Car("Volvo", 170.00)
            val audi = Car("Audi", 180.00)
            val bmw = Car("BMW", 184.00)

            val distanceToWarsaw = Distance(
                "to Warsaw", 100.00, 4, arrayListOf(
                    mercedes,
                    volvo
                ), 0
            )
            val distanceToLondon = Distance(
                "to London", 330.00, 5, arrayListOf(
                    volvo,
                ), 1
            )
            val distanceToParis = Distance(
                "to Paris", 400.00, 3, arrayListOf(
                    audi, bmw, mercedes
                ), 2
            )
            val distanceToGlasgow = Distance(
                "to Glasgow", 500.00, 8, arrayListOf(
                    bmw, volvo
                ), 3
            )
            val distanceToVienna = Distance(
                "to Vienna", 440.00, 7, arrayListOf(
                    bmw
                ), 4
            )

            return Stream.of(
                arguments(
                    Person(
                        "Marek", arrayListOf(
                            distanceToWarsaw,
                            distanceToGlasgow,
                            distanceToVienna
                        ), 0
                    ),
                    arrayListOf(
                        distanceToWarsaw,
                        distanceToLondon,
                        distanceToParis,
                        distanceToGlasgow,
                        distanceToVienna
                    ),
                    arrayListOf(
                        mercedes,
                        volvo,
                        audi,
                        bmw
                    ),
                    (
                            (
                                    ((mercedes.totalFuelCost + volvo.totalFuelCost + audi.totalFuelCost + bmw.totalFuelCost)
                                            * distanceToWarsaw.distance * distanceToWarsaw.listOfCars.count())
                                            / (3170.00
                                            * distanceToWarsaw.passengersCount)
                                            +
                                            ((mercedes.totalFuelCost + volvo.totalFuelCost + audi.totalFuelCost + bmw.totalFuelCost)
                                                    * distanceToGlasgow.distance * distanceToGlasgow.listOfCars.count())
                                            / (3170.00
                                            * distanceToGlasgow.passengersCount)
                                            +
                                            ((mercedes.totalFuelCost + volvo.totalFuelCost + audi.totalFuelCost + bmw.totalFuelCost)
                                                    * distanceToVienna.distance * distanceToVienna.listOfCars.count())
                                            / (3170.00
                                            * distanceToVienna.passengersCount)
                                    )
                            )
                )
            )
        }
    }
}