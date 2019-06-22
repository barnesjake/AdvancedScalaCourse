package lectures.part1as

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


  // infix patterns
  case class Or[A, B](a: A, b: B) //Either
  val either = Or(2, "two")
  val humanDescription  = either match {
//    case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) =>"starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  // custom return types for unapply
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person is called $n"
    case _ => "something else liek an alien of something"
  })


}
