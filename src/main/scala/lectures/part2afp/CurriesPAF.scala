package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  // METHOD! - methods are instances of classes
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add4: Int => Int = curriedAdder(4)
  println(add4(5))

  // lifting - transforming a method to a function = ETA-EXPANSION

  // functions != methods (JVM limitation - methods are part of instance of class and limited to it)
  def inc(x: Int) = x + 1
  List(1,2,3).map(inc) // ETA-expansion, compiler turns inc method into a function
//  List(1,2,3).map(x => inc(x)) compiler converts to this


  // Partial function applications
  val add5 = curriedAdder(5) _ // _ tells compiler to convert to Int => Int

  // EXCERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many different implementations of add7 using the above

  val add7_1 = simpleAddFunction(7, _)
  val add7_2 = simpleAddMethod(7, _)
  val add7_3 = curriedAddMethod(7) _                // PAF
  val add7_4 = curriedAddMethod(7)(_)               // PAF = alternative syntax
  val add7_5 = (x: Int) => simpleAddFunction(7, x)
  val add7_6 = simpleAddFunction.curried(7)
  val add7_8 = simpleAddMethod(7, _: Int)           // alternative syntax for turning methods into function values
                        // y => simpleAddMethod(7, y)
  val add7_9 = simpleAddFunction(7, _: Int)

  println(s"1 + 7 = ${add7_1(1)}")
  println(s"2 + 7 = ${add7_2(2)}")
  println(s"3 + 7 = ${add7_3(3)}")
  println(s"4 + 7 = ${add7_4(4)}")
  println(s"5 + 7 = ${add7_5(5)}")
  println(s"6 + 7 = ${add7_6(6)}")

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I am ", _, ", how are you?") // x: String => concatenator(hello, x, howareyou)
  println(insertName("Bob Ross"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x,y) => concatenator("Hello," x, y)
  println(fillInTheBlanks("Bob Ross,", " Talent is a pursued interest"))

  // EXERCISES
  /*
      1. Process a list of numbers and return their string representations with different formats
      Use the %4.2f, %8.6f and %14.12f with a curried formatter function
   */

  println("--------------------------\nExercises\n--------------------------")
  println("\n1.\n")

  val listOfNumbers: List[Double] = List(1.0101, 2.02020202020202, 3.33333333333333314, 4.133356789765, 5.54345645676545654565456754)
  def curriedFormatter(formatter: String)(number: Double): String = formatter.format(number)
  val twoDecimalPlaces = curriedFormatter("%4.2f") _ // lift
  val sixDecimalPlaces = curriedFormatter("%8.6f") _
  val twelveDecimalPlaces = curriedFormatter("%14.12f") _

  println(listOfNumbers.map(twoDecimalPlaces(_)))
  println(listOfNumbers.map(sixDecimalPlaces(_)))
  println(listOfNumbers.map(twelveDecimalPlaces(_)))

  /*
      2. difference between
      - functions vs methods
      - parameters: by-name vs 0-lambda

      function vs methods
      Function is an object - with an apply method

   */

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
      calling byName and byFunction
      - int
      - method
      parenMethod
      - lambda
      PAF
   */

  byName(42) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok ==> be careful, byName(parenMethod())
  //  byName(() => 42) // not ok
  byName((() => 42)()) // ok
  //  byName(parenMethod _) // not ok

  //  byFunction(45) // not ok
  //  byFunction(method)// not ok, compile does not do eta-expansion here
  byFunction(parenMethod) // ok, compile does eta-expansion
  byFunction(() => 42) // works
  byFunction(parenMethod _) // also works, but warning - unnecessary

}
