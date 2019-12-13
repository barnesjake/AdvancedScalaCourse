package lectures.part2afp

object Monads extends App {

  // my own Try monad

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

/*
    left-identity
    unit.flatMap(f) => f(x)
    Attempt(x).flatMap(f) => f(x)  // Success case
    Success.flatMap(f) => f(x) // proven.

    right-identity
    attempt.flatMap(unit) => attempt
    Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
    Failure(e).flatMap(...) = Failure(e)

    associativity
    attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
    Failure(e).flatMap(f).flatMap(g) = Failure(e)
    Failure(e).flatMap(x => f(x).flatMap(g)) = Failure(e)

    Success(v).flatMap(f).flatMap(g) =
      f(v).flatMap(g) OR Failure(e)
    Success(x).flatMap(x => f(x).flatMap(g)) =
      f(v).flatMap(g) OR Failure(e)
 */

  val attempt = Attempt {
    throw new RuntimeException("My monad")
  }
  println(attempt)
}
