package exercise

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def -(elem: A): MySet[A]

  def --(anotherSet: MySet[A]): MySet[A] // difference

  def &(anotherSet: MySet[A]): MySet[A] // intersection

  def unary_! : MySet[A]

}

class EmptySet[A] extends MySet[A] {
  def contains(elem: A): Boolean = false

  def +(elem: A): MySet[A] = new NonEmptySet[A](head = elem, tail = this)

  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  def filter(predicate: A => Boolean): MySet[A] = this

  def foreach(f: A => Unit): Unit = ()

  def -(elem: A): MySet[A] = this

  def --(anotherSet: MySet[A]): MySet[A] = this

  def &(anotherSet: MySet[A]): MySet[A] = this

  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}

// all elements of type A which satisfy a property
// {x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)

  override def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](head = elem, tail = this)

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  /*
  [1 2 3] ++ [4 5] =
  [2 3] ++ [4 5] + [1] =
  [3] ++ [4 5] + [2] + [2] =
  [] ++ [4 5] + 1 + 2 + 3 =
  [4 5] + 1 + 2 + 3 = [4 5 1 2 3]
   */

  def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))

  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // apply method is contains. intersection = filtering

  // new operator
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}

object MySet {
  /*
  val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
  = buildSet(seq(2,3), [] + 1)
  = buildSet(seq(3), [1] + 2)
  = buildSet(seq(), [1,2] + 3)
  = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s foreach println
  println("\n-----------\n")
  s + 5 ++ MySet(-1) foreach println
  println("\n-----------\n")
  s + 5 ++ MySet(-1, -2) foreach println
  println("\n-----------\n")
  s + 5 ++ MySet(-1, -2) + 3 foreach println
  println("\nmap-----------\n")
  s + 5 ++ MySet(-1, -2) + 3 map (x => x * 10) foreach println
  println("\nflatMap-----------\n")
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, 10 * x)) foreach println
  println("\nfilter-----------\n")
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, 10 * x)) filter (_ % 2 == 0) foreach println
  println("\n-----------\n")


  val negative = !s // s.unary_! all the naturals not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))
  println("\n-----------\n")

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))
  println("\n-----------\n")

  val negativeEven5 = negativeEven + 5 // all the even numbers > 4 + 5
  println(negativeEven5(5))
  println("\n-----------\n")


}