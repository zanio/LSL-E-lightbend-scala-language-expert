package com.lightbend.training.scalatrain

object ChainingImplicitParameters {

  // Our type class
  trait Show[A] {
    def show(item: A): String
  }

  // Our type class glue
  implicit class ShowOps[A : Show](val a: A) {
    // Observe that we call Show[A].apply so that we don't have to
    // use the long winded implicitly[Show[A]]
    def show: String = Show[A].show(a)
  }

  object Show {
    // Provide Show[A] as a shortcut for implicitly[Show[A]]
    def apply[A : Show]: Show[A] = implicitly[Show[A]]

    // The Show type-class instances
    implicit val timeShow: Show[Time] = new Show[Time] {
      def show(t: Time): String = s"Time: ${t.toString}"
    }

    implicit val stationShow: Show[Station] = new Show[Station] {
      def show(t: Station): String = s"Station: ${t.name}"
    }

    implicit val intShow: Show[Int] = new Show[Int] {
      def show(i: Int): String = s"Int: $i"
    }

    implicit val stringShow: Show[String] = new Show[String] {
      def show(i: String): String = s"String: $i"
    }

    implicit val doubleShow: Show[Double] = new Show[Double] {
      def show(d: Double): String = s"Double: $d"
    }

    implicit def seqShow[V](implicit printer: Show[V]): Show[Seq[V]] = {
      new Show[Seq[V]] {
        def show(seq: Seq[V]): String =
          seq.map(item =>
            printer.show(item)).mkString("[ ", ", ", " ]"
          )
      }
    }

    implicit def scheduleItemshow(implicit pt:Show[Time], ps: Show[Station]): Show[(Time, Station)] =
      new Show[(Time, Station)] {
        def show(stop: (Time, Station)): String = s"""{ ${ps.show(stop._2)}", "${pt.show(stop._1)} }""""
      }

    implicit val trainShow: Show[Train] =
      new Show[Train] {
        def show(train: Train): String = {
          val schedulePrinter = implicitly[Show[Seq[(Time, Station)]]]
          s"""Train : { "TrainInfo" :  "${train.info}", "Schedule" : ${schedulePrinter.show(train.schedule)} }""".stripMargin
        }
      }

    implicit def tuple2Show[A : Show, B : Show]: Show[Tuple2[A, B]] = new Show[Tuple2[A, B]] {
      def show(tuple: Tuple2[A, B]): String =
        s"( ${Show[A].show(tuple._1)}, ${Show[B].show(tuple._2)} )"
    }

    implicit def tuple3Show[A1 : Show, A2 : Show, A3 : Show]: Show[Tuple3[A1, A2, A3]] = new Show[Tuple3[A1, A2, A3]] {
      def show(tuple: Tuple3[A1, A2, A3]): String =
        s"( ${Show[A1].show(tuple._1)}, ${Show[A2].show(tuple._2)}, ${Show[A3].show(tuple._3)} )"
    }

    implicit def tuple4Show[A1 : Show, A2 : Show, A3 : Show, A4 : Show]: Show[Tuple4[A1, A2, A3, A4]] = new Show[Tuple4[A1, A2, A3, A4]] {
      def show(tuple: Tuple4[A1, A2, A3, A4]): String =
        s"( ${Show[A1].show(tuple._1)}, ${Show[A2].show(tuple._2)}, ${Show[A3].show(tuple._3)}, ${Show[A4].show(tuple._4)} )"
    }
  }
}
