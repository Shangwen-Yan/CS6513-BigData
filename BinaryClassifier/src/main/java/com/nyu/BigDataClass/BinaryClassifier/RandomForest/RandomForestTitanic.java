package com.nyu.BigDataClass.BinaryClassifier.RandomForest;

import org.apache.log4j.LogManager;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import scala.Tuple2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RandomForestTitanic {
    // a regular expression which matches commas but not commas within double quotations
    public static final String COMMA_DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void main(String[] args) throws IOException {
        LogManager.getLogger("org").setLevel(org.apache.log4j.Level.OFF);
        SparkConf sparkConf = new SparkConf().setAppName("RandomForestTitanic").setMaster("local[1]");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        //JavaRDD<String> testData = jsc.textFile("./RandomForest/test.csv");
        //JavaRDD<String> trainData = jsc.textFile("./RandomForest/train.csv");
        
        //JavaRDD<String> oriData = jsc.textFile("/Users/Lovely-white/data/binary.csv");
        JavaRDD<String> oriData = jsc.textFile("/home/data/binary.csv");
        JavaRDD<String>[] split = oriData.randomSplit(new double[] {0.7, 0.3}, 17);
        JavaRDD<String> trainData = split[0];
        JavaRDD<String> testData = split[1];
        
        JavaRDD<LabeledPoint> trainPoints = trainData.filter(l -> !"admit".equals(l.split(COMMA_DELIMITER)[0])).map(line -> {
            String[] params = line.split(COMMA_DELIMITER);
            double label = Double.valueOf(params[0]);
            double[] vector = new double[3];
            vector[0] = Double.valueOf(params[1]);
            vector[1] = Double.valueOf(params[2]);
            vector[2] = Double.valueOf(params[3]);
            //vector[3] = Double.valueOf(params[4]);
            return new LabeledPoint(label, new DenseVector(vector));
        });

        JavaRDD<LabeledPoint> testPoints = testData.filter(l -> !"admit".equals(l.split(COMMA_DELIMITER)[0])).map(line -> {
            String[] params = line.split(COMMA_DELIMITER);
            double label = Double.valueOf(params[0]);
            double[] vector = new double[3];
            vector[0] = Double.valueOf(params[1]);
            vector[1] = Double.valueOf(params[2]);
            vector[2] = Double.valueOf(params[3]);
            //vector[3] = Double.valueOf(params[4]);
            return new LabeledPoint(label, new DenseVector(vector));
        });
        System.out.println("================Random Forest Classifier===================");
        System.out.println("total train data ( 70% ) :" + trainPoints.count());
        System.out.println("total test ( 30% ) :" + testPoints.count());
        System.out.println("seed:" + "17");
        // Train a RandomForest model
        // Empty categoricalFeaturesInfo indicates all features are continuous
        Integer numClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        Integer numTrees = 5; // Use more in practice.
        String featureSubsetStrategy = "auto"; // Let the algorithm choose.
        String impurity = "gini";
        Integer maxDepth = 4;
        Integer maxBins = 32;
        Integer seed = 12345;

        RandomForestModel model = RandomForest.trainClassifier(trainPoints, numClasses,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins,
                seed);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Object, Object> predictionAndLabel =
                testPoints.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        double testErr =
                predictionAndLabel.filter(pl -> !(pl._1()).equals(pl._2())).count() / (double) testData.count();
        //System.out.println("Test Error: " + testErr);

        //System.out.println("Learned classification forest model:\n" + model.toDebugString());

        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(predictionAndLabel.rdd());


        long truePositive = predictionAndLabel.filter(pl -> (pl._1()).equals(pl._2()) && pl._1().equals(1d)).count();
        //System.out.println("True Positive: " + truePositive);

        long trueNegative = predictionAndLabel.filter(pl -> (pl._1()).equals(pl._2()) && pl._1().equals(0d)).count();
        //System.out.println("True Negative: " + trueNegative);

        long falsePositive = predictionAndLabel.filter(pl -> !(pl._1()).equals(pl._2()) && pl._2().equals(0d)).count();
        //System.out.println("False Positive: " + falsePositive);

        long falseNegative = predictionAndLabel.filter(pl -> !(pl._1()).equals(pl._2()) && pl._2().equals(1d)).count();
        //System.out.println("False Negative:" + falseNegative);

        /*
        System.out.println("\nConfusion Matrix:");
        System.out.println("n: " + testData.count() + "   0" + "   1");
        System.out.println("0: " + "   " + trueNegative + "   " + falseNegative);
        System.out.println("1: " + "   " + falsePositive + "   " + truePositive + "\n");
	*/
        // AUPRC
        //System.out.println("Area under precision-recall curve = " + metrics.areaUnderPR());

        // AUROC
        //System.out.println("Area under ROC = " + metrics.areaUnderROC());
        System.out.println("AUC = " + metrics.areaUnderROC());
        System.out.println("ACCURACY = " + (1-testErr));
        double PreCISION =  truePositive/(1.0*truePositive+falsePositive);
        System.out.println("PreCISION = " + PreCISION);
        
        File file = new File("/home/2018/spring/nyu/6513/sy2160/P2/result/RandomForest.txt");
        FileOutputStream fos = new FileOutputStream(file);
        String content = "total train data ( 70% ) :" + trainData.count()+"\n";
        content += "total test ( 30% ) :" + testData.count() +"\n";
        content += "seed:" + "17"+"\n";
        content += "AUC = " + metrics.areaUnderROC()+"\n";
        content += "ACCURACY = " + (1-testErr)+"\n";
        content += "PreCISION = " + PreCISION+"\n";
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        jsc.stop();
    }
}
