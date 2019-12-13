package exercise

object Monads extends App {

  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
    def use: A = internalValue
  }
  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("today i don't feel like doing anything")
    42
  }

//  println(lazyInstance.use)
  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })
  flatMappedInstance.use
  flatMappedInstance2.use

  /*
  left-identity
  unit.flatMap(f) = f(v)
  Lazy(v).flatMap(f) = f(v)

  right-identity
  l.flatMap(unit) = l
  Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

  associativity: l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
  Lazy(v).flatMap(f).flatMap(g) = f(x).flatMap(g)
  Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */
  //2 : map and flatten in terms of flatMap
  /*
  Monad[T] {
  def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

  def map
   */


}
