/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.finderbots.drivers

import com.google.common.collect.{HashBiMap, BiMap}
import org.apache.log4j.Logger
import org.apache.mahout.math.cf.SimilarityAnalysis
import org.apache.mahout.math.indexeddataset._
import org.apache.mahout.sparkbindings._
import scala.collection.immutable.HashMap

/**
 * Performs cooccurrence analysis on the primary action (purchase) and all secondary actions.
 * indicator 1 = [A'A], where A is all purchase actions - cooccurrence
 * indicator 2 = [A'B], where B is all view actions (view a product detail page) - cross-cooccurrence
 * indicator 3 = [A'C], where C is all category preference actions (clicks on category, or search for category) -
 *   cross-cooccurrence
 * Uses hard coded paths to input and output for example data only. Does everything needed to read in three user
 * interaction data, then calculates cooccurrence indicators and writes text files using. All IO and calculations
 * use Spark and are distributed but are run on a "local" Spark master for ease of debugging.
 */
object CooccurrenceDriver extends App {
  val logger = Logger.getLogger(this.getClass)

  //The primary actions, to which the rest are compared in cross-cooccurrence is the first--purchase
  val ActionInput = Array(
    ("purchase", "data/purchase.csv"),
    ("view", "data/view.csv"),
    ("category", "data/category.csv"))

  val OutputPath = "data/indicators/"

  // may need to change the master to use a cluster or increase executor memory or other Spark context
  // attributes here
  implicit val mc = mahoutSparkContext(masterUrl = "local", appName = "CooccurrenceDriver")


  // gets an array of Scala tuples, Array[(actionName, IndexedDataset)]
  val actions = readActions(ActionInput)

  // strip off names, which only takes and array of IndexedDatasets
  val indicatorMatrices = SimilarityAnalysis.cooccurrencesIDSs(actions.map(a => a._2))

  // zip pair of arrays into array of pairs, reattaching the action names
  val indicatorDescriptions = actions.map(a => a._1).zip(indicatorMatrices)
  writeIndicators(indicatorDescriptions)

  /**
   * Write indicatorMatrices to the output dir in the default format
   */
  def writeIndicators( indicators: Array[(String, IndexedDataset)]) = {
    for (indicator <- indicators ) {
      val indicatorDir = OutputPath + indicator._1
      indicator._2.dfsWrite(
        indicatorDir,
        IndexedDatasetWriteBooleanSchema) // omit LLR strengths and format for search engine indexing
    }
  }

  /**
   * Read files of element tuple and create IndexedDatasets one per action. These share a userID BiMap but have
   * their own itemID BiMaps
   */
  def readActions(actionInput: Array[(String, String)]): Array[(String, IndexedDataset)] = {
    var actions = Array[(String, IndexedDataset)]()

    val userDictionary: BiMap[String, Int] = HashBiMap.create()

    // The first action named in the sequence is the "primary" action and begins to fill up the user dictionary
    for ( actionDescription <- actionInput ) {// grab the path to actions
      val action: IndexedDataset = SparkEngine.indexedDatasetDFSReadElements(
        actionDescription._2,
        schema = DefaultIndexedDatasetElementReadSchema,
        existingRowIDs = userDictionary)
      userDictionary.putAll(action.rowIDs)
      actions = actions :+ (actionDescription._1, action) // put the name in the tuple with the indexedDataset
      logger.info(
        "\n\n Read in action " + actionDescription._1 +", which has " + action.matrix.nrow + " rows\n" +
        " and " + action.matrix.nrow + " columns in it.\n")
    }

    // After all actions are read in the userDictonary will contain every user seen, even if they may not have
    // taken all actions . Now we adjust the row rank of all IndxedDataset's to have this number of rows
    // Note: this is very important or the cooccurrence calc may fail
    val numUsers = userDictionary.size() // one more than the cardinality
    logger.info("\nTotal number of users for all actions = " + numUsers + "\n")
    val resizedNameActionPairs = actions.map { a =>
      val numRows = a._2.matrix.nrow
      val resizedMatrix = a._2.create(a._2.matrix, userDictionary, a._2.columnIDs).newRowCardinality(numUsers)
      logger.info(
        "\n\n " + a._1 + " indicator matrix \n" +
        " number of rows = " + numRows + "\n" +
        " number of columns = " + a._2.matrix.ncol + "\n" +
        " number of rows after resize = " + resizedMatrix.matrix.nrow + "\n")
      (a._1, resizedMatrix)
    }
    logger.info("\n")
    resizedNameActionPairs
  }

}