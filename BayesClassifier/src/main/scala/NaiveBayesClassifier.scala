
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib._
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD
import java.io._
import scala.io.Source


object NaiveBayesClassifier {

  def main(args:Array[String]) : Unit = {
    import org.apache.spark.SparkConf
    val sparkConf = new SparkConf().setAppName("NaiveBayesClassifier").setMaster("local[1]")
    val sc = SparkContext.getOrCreate(sparkConf)

    val datapre = sc.textFile("/home/data/binary.csv")
    val header = datapre.first()
    val parseddatapre = datapre.filter(row => row != header).map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).toDouble, parts(2).toDouble, parts(3).toDouble))
    }


    val splits = parseddatapre.randomSplit(Array(0.7, 0.3), seed = 17L)
    val training = splits(0)
    val test = splits(1)
    val model = NaiveBayes.train(training)
    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    //println("the accuracy is " + accuracy)

    val metrics = new BinaryClassificationMetrics(predictionAndLabel)
    val roc1 = metrics.roc

    //println("the ROC is " + roc1.collect())

    val auc1 = metrics.areaUnderROC

    //println("the AUC is " + auc1)

    val metrics2 = new MulticlassMetrics(predictionAndLabel)
    val confmatrix = metrics2.confusionMatrix
    val truepositive = confmatrix.apply(0,0);
    val falsepositive = confmatrix.apply(0,1);
    println("======================================NaiveB Bayes Classifier====================")
    println("total train data ( 70% ) : " + training.count())
    println("total test data ( 30% ) : " + test.count())
    println("seed : " + "17")
    println("AUC =  " + auc1)
    println("ACCURACY =" + accuracy)
    println("PreCISION =  " + truepositive/ (falsepositive+truepositive))


    val bw=new BufferedWriter(new FileWriter("/home/2018/spring/nyu/6513/sy2160/P2/result/NaiveBayes.txt"))
    bw.append("total train data ( 70% ) : " + training.count()+"\n")
    bw.append("total test data ( 30% ) : " + test.count()+"\n")
    bw.append("seed : " + "17"+"\n")
    bw.append("AUC =  " + auc1+"\n")
    bw.append("ACCURACY =" + accuracy+"\n")
    bw.append("PreCISION =  " + truepositive/ (falsepositive+truepositive)+"\n")
    bw.close()


  }

}
