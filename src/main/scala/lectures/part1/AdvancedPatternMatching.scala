package lectures.part1

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description: Unit = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ =>
  }

  /*
    - constants
    - wildcards
    -case classes
    -tuples
    -some special magic like above
   */
  class Person(val name: String, val age: Int)

  // best practice to name this as Person, companion object should have the same name
  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }


  val bob = new Person("bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi my name is $n and I am $a years old."
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"I am a $status"
  }

  println(legalStatus)


  /* Exercise */
  val n = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "even"
    case _ => "no property"
  }

  object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  object singleDigit {
    def unapply(arg: Int): Option[Boolean] =
      if (arg < 10) Some(true)
      else None
  }

  val m = 46
  val o = 9
  val mathPropertyExercise = o match {
    case even(x) => s"The number is even"
    case singleDigit(x) => s"The number is a single digit"
    case _ => s"Wtf is it?"
  }

  println(mathPropertyExercise)


}
