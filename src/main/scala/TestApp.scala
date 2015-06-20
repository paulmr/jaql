package org.stelo.ql

import org.stelo.ql.swing.QL4ScreenComponent

import java.awt.{Color => AWTColor, Graphics2D}
import java.io.{File, FileInputStream}
import scala.swing.SimpleSwingApplication
import scala.swing.MainFrame
import m68k.memory.MemorySpace

object TestApp extends SimpleSwingApplication {
  val mem = new MemorySpace(1024)
  val file = new File("screen_bin")
  val len  = file.length.toInt
  val fin = new FileInputStream(file)
  val fileBytes = Array.fill(len)(0.toByte)
  val readCount =
    fin.read(fileBytes)

  assert(readCount == len, s"Didn't read the whole file ${readCount}")
  for(addr <- (0 until len)) {
    mem.writeByte(addr + 0x20000, fileBytes(addr))
  }

  println(s"Loaded 0x${readCount.toHexString.toUpperCase} bytes")
  lazy val screen =
    QL4ScreenComponent.qlDefault(mem)

  val top = new MainFrame {
    contents = screen
  }

  screen.doScan
}
