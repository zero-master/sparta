/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.sparkta.plugin.output.print

import java.io.{Serializable => JSerializable}

import com.stratio.sparkta.sdk.TypeOp._
import com.stratio.sparkta.sdk.WriteOp.WriteOp
import com.stratio.sparkta.sdk._
import org.apache.spark.sql._
import scala.util.Try

class PrintOutput(keyName : String,
                  properties: Map[String, JSerializable],
                  operationType : Option[Map[String, (WriteOp, TypeOp)]],
                  sqlContext : SQLContext)
  extends Output(keyName, properties, operationType, sqlContext) {

  override val supportedWriteOps = Seq(WriteOp.Inc, WriteOp.Set, WriteOp.Max, WriteOp.Min)

  override val multiplexer = Try(
    properties("multiplexer").asInstanceOf[String].toLowerCase().toBoolean).getOrElse(false)

  override def timeBucket: String = Try(properties("timeDimension").asInstanceOf[String]).getOrElse("")

  override val granularity = Try(properties("granularity").asInstanceOf[String]).getOrElse("")

  override def upsert(dataFrame : DataFrame): Unit = {
    dataFrame.printSchema()
    dataFrame.foreach(println)
    val select = dataFrame.select("precision3","hastags")
    select.printSchema()
    select.foreach(println)
    println("COUNT : "  +  dataFrame.count())
  }

  override def upsert(metricOperations: Iterator[UpdateMetricOperation]): Unit = {
    metricOperations.foreach(metricOp => println(metricOp.toString))
  }
}
