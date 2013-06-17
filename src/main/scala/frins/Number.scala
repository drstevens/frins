package frins

class Number[T](val value:T, val units: UnitT)(implicit num: Fractional[T]) {

  // ----

  override def toString() = value.toString + " " + cleanUnits.foldLeft("")
                              { case (acc, (k,v)) => acc + k + "^" + v + " "}

  override def equals(that: Any) = that match {
    case that: Number[T] => value == that.value && units == that.units
    case _            => false
  }

  override def hashCode = 41 * units.hashCode() + value.hashCode()

  // ----
  // Helper functions, where do these actually go?

  def cleanUnits() = units filter { _._2 != 0 }

  def mergeUnits (f: (Int, Int) => Int) (us: UnitT) =
    units ++ us.map { case (k, v) => k -> f(units.getOrElse(k, 0), v) }

  val addUnits = mergeUnits(_+_) _
  val subUnits = mergeUnits(_-_) _

  def enforceUnits(n: Number[T]) =
    if (units != n.units)
      throw new IllegalArgumentException("units doesn't match")

  // ----

  def +(that: Number[T]) = {
    enforceUnits(that)
    new Number(num.plus(value, that.value), units)
  }

  def -(that: Number[T]) = {
    enforceUnits(that)
    new Number(num.minus(value, that.value), units)
  }

  def *(that: Number[T]) =
    new Number(num.times(value, that.value), addUnits(that.units))

  def /(that: Number[T]) =
    new Number(num.div(value, that.value), subUnits(that.units))

  def ==(that: Number[T]) = {
    enforceUnits(that)
    num.equiv(value, that.value)
  }

  def >=(that: Number[T]) = {
    enforceUnits(that)
    num.gteq(value, that.value)
  }

  def >(that: Number[T]) = {
    enforceUnits(that)
    num.gt(value, that.value)
  }

  def <(that: Number[T]) = {
    enforceUnits(that)
    num.lt(value, that.value)
  }

  def <=(that: Number[T]) = {
    enforceUnits(that)
    num.lteq(value, that.value)
  }

}

object Number{
  def apply(): Number[BigDecimal] = apply(0)
  // TODO; instead of Map, do we want (String,Int)* here?
  def apply(v: BigDecimal): Number[BigDecimal] = apply(v, Map())
  def apply(units: UnitT): Number[BigDecimal] = apply(0, units)
  def apply(v: BigDecimal, units: UnitT): Number[BigDecimal] = new Number(v, units)

  // TODO; where does this go?
  implicit def bigdecToNumber(b: BigDecimal) = apply(b)
  implicit def doubleToNumber(d: Double) = apply(d)
  implicit def intToNumber(i: Int) = apply(i)

}
