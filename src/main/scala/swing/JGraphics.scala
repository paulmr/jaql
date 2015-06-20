package org.stelo.ql.swing

import java.awt.{Color => AWTColor}
import java.awt.image.BufferedImage
import org.stelo.ql.dev.screen._

trait JavaGraphics extends CanDisplayGraphics {
  // this image represents the current state of the graphical screen
  protected val image: BufferedImage

  def makeRGB(col: QLColour.Value) = col match {
    case QLColour.BLACK => 0x000000
    case QLColour.RED   => 0xFF0000
    case QLColour.GREEN => 0x00FF00
    case QLColour.WHITE => 0xFFFFFF
  }

  def setPixel(x: Int, y: Int, col: QLColour.Value) = {
    image.setRGB(x, y, makeRGB(col))
  }
}
