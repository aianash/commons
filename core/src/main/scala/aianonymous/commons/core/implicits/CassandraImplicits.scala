package aianonymous.commons.core.implicits

import scala.concurrent.ExecutionContext

import akka.util.Timeout

import com.datastax.driver.core.{ResultSetFuture, ResultSet}

import java.util.concurrent.Callable


trait CassandraImplicits {

  implicit class ResultSetFuture2ScalaFuture(rsFuture: ResultSetFuture) {
  def toFuture(implicit ec: ExecutionContext, timeout: Timeout) =
    akka.dispatch.Futures.future(new Callable[ResultSet] {
      def call =
        rsFuture.getUninterruptibly(timeout.duration.length, timeout.duration.unit)
    }, ec)
  }

}