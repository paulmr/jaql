package org.stelo.ql.dev

import m68k.memory.AddressSpace

/* An IO device works by accessing an area of memory and acting
 * accordingly */

trait MemDevice {
  val mem: AddressSpace
  val bottom: Int
  val top: Int

  private class MemIterator(inc: Int, getter: (Int) => (Int)) extends Iterator[Int] {
    var addr = bottom
    def hasNext = addr < top
    def next = {
      val res = getter(addr)
      addr += inc
      res
    }
  }

  def memSize = top - bottom
  def offsetWord(off: Int) = mem.readWord(bottom + off)

  // iterate over all the memory, by words
  def iteratorWord: Iterator[Int] =
    new MemIterator(2, (addr) => mem.readWord(addr))
}
