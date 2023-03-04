/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

object Resource {

  def withResource[A <: Closable, B](resource: A)(f: A => B): B =
    try f(resource) finally resource.close()

  type Closable = { def close(): Unit }
}
