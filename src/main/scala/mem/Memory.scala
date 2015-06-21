package org.stelo.ql.mem

import m68k.memory.AddressSpace

/**
  * An implementation of AddressSpace that actually combines various
  * different other address spaces and gives them names and other
  * properties (such as read-only etc).
  */

case class Segment(name: String,
                   contents: AddressSpace,
                   startAddr: Int,
                   writable: Boolean) {
  lazy val endAddr = startAddr + contents.size
}

class SegmentedMemory(segs: Vector[Segment] = Vector.empty) extends AddressSpace {

  def addSegment(name: String,
                 contents: AddressSpace,
                 startAddr: Int,
                 writable: Boolean = true) = {
    val overlap = findSeg(startAddr).orElse(findSeg(startAddr + contents.size))
    assert((startAddr & 3) == 0, s"Start addr should be long word aligned $startAddr")
    assert(overlap.isEmpty,
           s"Adding segment ($startAddr => ${startAddr + contents.size})" +
             s"that overlaps with existing segment (${overlap.get.startAddr} => ${overlap.get.endAddr}")
    new SegmentedMemory(segs :+ Segment(name, contents, startAddr, writable))
  }

  def size = segs.map(_.contents.size).reduce(_ + _)

  // not actually sure what this does ...
  def reset = ()

  import Math.{max, min}

  private def findSeg(addr: Int): Option[Segment] =
    segs.find(seg => (seg.startAddr <= addr) && (seg.endAddr > addr))

  // perform op with address adjusted to seg offset
  private def segOffVal[A](addr: Int, needsWrite: Boolean = false)(f: (Int, AddressSpace) => A): A = {
    val seg = findSeg(addr).get
    if(needsWrite && (!seg.writable))
      throw new IllegalArgumentException("can't write to readonly segment")
    f(addr - seg.startAddr, seg.contents)
  }

  private def segOff(addr: Int, needsWrite: Boolean = false)(f: (Int, AddressSpace) => Unit): Unit =
    segOffVal[Unit](addr, needsWrite)(f)

  def getEndAddress: Int = segs.map(_.endAddr).reduce(max(_, _))
  def getStartAddress: Int = segs.map(_.startAddr).reduce(min(_, _))
  def internalReadByte(addr: Int) = readByte(addr)
  def internalReadLong(addr: Int) = readLong(addr)
  def internalReadWord(addr: Int) = readWord(addr)
  def internalWriteByte(addr: Int, value: Int) = writeByte(addr, value)
  def internalWriteLong(addr: Int, value: Int) = writeLong(addr, value)
  def internalWriteWord(addr: Int, value: Int) = writeWord(addr, value)
  def readByte(addr: Int): Int =
    segOffVal(addr, true)((addr, seg) => (seg.readByte(addr)))
  def readLong(addr: Int): Int =
    segOffVal(addr, true)((addr, seg) => (seg.readLong(addr)))
  def readWord(addr: Int): Int =
    segOffVal(addr, true)((addr, seg) => (seg.readWord(addr)))
  def writeByte(addr: Int, value: Int): Unit =
    segOff(addr, true)((addr, seg) =>
      (seg.writeByte(addr, value)))
  def writeLong(addr: Int, value: Int): Unit =
    segOff(addr, true)((addr, seg) => (seg.writeLong(addr, value)))
  def writeWord(addr: Int, value: Int): Unit =
    segOff(addr, true)((addr, seg) => (seg.writeWord(addr, value)))
}
