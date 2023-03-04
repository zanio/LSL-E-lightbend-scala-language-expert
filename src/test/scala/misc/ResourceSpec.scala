/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ResourceSpec extends AnyWordSpec with Matchers {

  "Calling withResource" should {
    "return the correct result and close the resource" in {
      import java.nio.file.Files.newInputStream
      import java.nio.file.Paths

      import Resource._

      import scala.io.Source
      val in = newInputStream(Paths.get("src/test/scala/misc/ResourceSpec.scala"))
      assert(withResource(in)(resource => Source.fromInputStream(resource).getLines().size) > 0)
    }
  } 
}
