package org.stelo.ql.mem

import m68k.memory.MemorySpace
import org.scalatest._

class SegmentedMemorySpec extends FlatSpec with ShouldMatchers {

  val segSize = 1024
  val baseAddr = 100
  val mem1 = new MemorySpace(segSize)
  val mem2 = new MemorySpace(segSize)
  val segmented = (new SegmentedMemory)
    .addSegment("test1", mem1, baseAddr)
    .addSegment("test2", mem2, baseAddr + (segSize * 1024))

  "A Segmented Memory instance" should "combine its segment to work out its size" in {
    segmented.size should equal (segSize * 1024 * 2) // convert to kb
  }

  it should "report it's start/end addr correctly" in {
    segmented.getStartAddress should equal (baseAddr)
    segmented.getEndAddress should equal (baseAddr + (segSize * 1024) * 2)
  }

  it should "write to the correct segment" in {
    segmented.writeLong(104, 0xDEADBEEF)
    mem1.readLong(4) should equal (0xDEADBEEF)
    segmented.writeLong(baseAddr + (segSize * 1024) + 4, 0xDEADBEEF)
    mem2.readLong(4) should equal (0xDEADBEEF)
  }

  it should "read from the correct segment" in {
    mem2.writeByte(300, 0xFF)
    segmented.readByte(baseAddr + (segSize * 1024) + 300) should equal (0xFF)
  }

}
