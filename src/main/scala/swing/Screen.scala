package org.stelo.ql.swing

import scala.swing.Swing._
import org.stelo.ql.dev.screen.{QLColour, QL4Screen}

import m68k.memory.AddressSpace

import java.awt.{Color => AWTColor, Graphics2D}
import java.awt.image.{IndexColorModel, BufferedImage}
import scala.swing.Panel

/* Swing implementations of various screens etc */

object ColorModels {
  private def bytes(ints: Int*): Array[Byte] =
    ints.map(_.toByte).toArray

  val ql4ColourModel = new IndexColorModel(
    2, // bits
    4, // = four colours
       //       B  R    G    W
    bytes(0, 255, 0,   255), // red
    bytes(0, 0  , 255, 255), // green
    bytes(0, 0  , 0,   255)
  )
}

class QL4ScreenComponent(val width: Int, val height: Int,
                     val mem: AddressSpace,
                     val bottom: Int, val top: Int)
    extends Panel
    with QL4Screen
    with JavaGraphics {

  preferredSize = (width, height)

  protected val image = new BufferedImage(
    width, height, BufferedImage.TYPE_BYTE_BINARY, ColorModels.ql4ColourModel
  )

  override def paintComponent(g: Graphics2D) = {
    super.paintComponent(g)
    g.drawImage(image, 0, 0, AWTColor.BLACK, null)
  }
}

object QL4ScreenComponent {
  def qlDefault(mem: AddressSpace) = new QL4ScreenComponent(
    width = 512, height = 256, mem = mem, bottom = 0x20000, top = 0x28000
  )
}
