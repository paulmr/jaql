package org.stelo.ql.dev.screen

import m68k.memory.AddressSpace
import org.stelo.ql.dev.MemDevice

/* it's not guaranteed that every device can actually display these
 * colours, but they should at least be able to handle the colour in
 * some way (even if that means they ignore it) */

object QLColour extends Enumeration {
  val BLACK = Value("BLACK")
  val RED   = Value("RED")
  val GREEN = Value("GREEN")
  val WHITE = Value("WHITE")
}

trait Screen {
  val width: Int
  val height: Int

  /* this should update the screen (or whatever it is that this screen will actually do) */
  def doScan: Unit

}

trait CanDisplayGraphics {
  def setPixel(x: Int, y: Int, col: QLColour.Value)
}

/* now we implement the QL screen */

/* first the 4-colour screen */

trait QL4Screen extends Screen
    with CanDisplayGraphics
    with MemDevice {

  import QLColour._

  /* parse two bits and return a colour */
  def makeColour(red: Boolean, green: Boolean): QLColour.Value = {
    val res = (red, green) match {
      case (false, false) => BLACK
      case (true,  false) => RED
      case (false, true)  => GREEN
      case (true,  true)  => WHITE
    }
    res
  }

  def doScan = {
    // each line is (width / 8) words long
    val lines = iteratorWord.grouped(width / 8)
    for {
      (line,  y) <- lines.zipWithIndex
      (word,  x) <- line.zipWithIndex
    } {
      // each word represents eight pixels
      // create a tuple of (green, red)
      (0 to 7) foreach { n =>
        val mask = 0x80 >> n
        val green = ((mask << 8) & word) > 0
        val red = (mask & word) > 0
        val colour = makeColour(red, green)
        setPixel((x * 8) + n, y, colour)
      }
    }
  }
}
