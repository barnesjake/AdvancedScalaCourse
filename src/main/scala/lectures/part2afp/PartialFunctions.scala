package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] ==== Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // {1, 2, 5} => Int
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value

  println(aPartialFunction(2))
  //  println(aPartialFunction(57273))

  // PF utilities
  println(aPartialFunction.isDefinedAt(67)) // a test to see if args will crash or not

  // lift -- stops crashing if arg is not valid
  val lifted = aPartialFunction.lift // Int => Option[A]
  println(lifted(2))
  println(lifted(98))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // pF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
//    case 5 => 1000
  }

  println(aMappedList)

  /*
    Note: Partial Functions can only have ONE parameter type
   */

  /**
    * Exercises:
    * 1 - construct a PF instance (anonymous class)
    * 2 - dumb chatbot as a PF
    */

  val exercisePartialFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 43
      case 3 => 44
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5
  }

  val chatBot: PartialFunction[String, String] = {
    case "hello" => "Hi"
    case "bye" => "Good Bye!"
  }

//  scala.io.Source.stdin.getLines().foreach(line => println(s"you said $line"))
//  scala.io.Source.stdin.getLines().foreach(line => println(s"${chatBot(line)}"))
  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)
}
