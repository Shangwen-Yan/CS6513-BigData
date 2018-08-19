package com.nyu.bd.LogisticRegression;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.rdd.RDD;
import java.io.IOException;

public class TitanicClassifier_5F {
	public static final String COMMA_DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	public static void main(String[] args) throws IOException {
        SparkConf conf = new SparkConf().setAppName("Java Ridg Regression").setMaster("local[1]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> oriData = sc.textFile("/home/data/titanic-train.csv");
     
        JavaRDD<String>[] split = oriData.randomSplit(new double[] {0.8, 0.2}, 17);
        JavaRDD<String> trainData = split[0];
        JavaRDD<String> testData = split[1];

        int minPartition = 1;
 
        JavaRDD<LabeledPoint> training = trainData.filter(l -> !"PassengerId".equals(l.split(COMMA_DELIMITER)[0])) 
                .map(new Function<String, LabeledPoint>() {
                    public LabeledPoint call(String line) throws Exception {
                        String[] parts = line.split(",");
                        if(parts[6]=="" || parts[6]== null || parts[6].isEmpty()) {
                        		return null;
                        }else {
                        	return new LabeledPoint(Double.parseDouble(parts[1]),
                                    Vectors.dense(Double.parseDouble(parts[2]),
                                            parts[5].equals("male")?1.0:-1.0,
                                            Double.parseDouble(parts[6])));
                        }
                        
                    }
                });
        training = training.filter(l -> !(l==null));

        JavaRDD<LabeledPoint> test = testData.filter(l -> !"PassengerId".equals(l.split(COMMA_DELIMITER)[0]))
                .map(new Function<String, LabeledPoint>() {
                    public LabeledPoint call(String line) throws Exception {
                        String[] parts = line.split(",");
	                    if(parts[6]=="" || parts[6]== null || parts[6].isEmpty()) {
	                    		return null;
	                    }else {
	                    	return new LabeledPoint(Double.parseDouble(parts[1]),
	                                Vectors.dense(Double.parseDouble(parts[2]),
	                                        parts[5].equals("male")?1.0:-1.0,
	                                        Double.parseDouble(parts[6])));
	                                        //Double.parseDouble(parts[4])));
	                    }
                    }
                });
        test = test.filter(l -> !(l==null));

        // Run training algorithm to build the model.
        LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
                .run(training.rdd());

        // Clear the prediction threshold so the model will return probabilities
        model.clearThreshold();

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = test.map(
                new Function<LabeledPoint, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(LabeledPoint p) {
                        Double prediction = (double)Math.round(model.predict(p.features()));
                        return new Tuple2<Object, Object>(prediction, p.label());
                    }
                }
        );

        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(predictionAndLabels.rdd());
        MulticlassMetrics metrics2 = new MulticlassMetrics(predictionAndLabels.rdd());
        JavaRDD<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD();
        JavaRDD<?> recall = metrics.recallByThreshold().toJavaRDD();
        JavaRDD<?> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
        JavaRDD<?> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD();
        JavaRDD<?> prc = metrics.pr().toJavaRDD();
        JavaRDD<Double> thresholds = precision.map(t -> Double.parseDouble(t._1().toString()));
        JavaRDD<?> roc = metrics.roc().toJavaRDD();
  

        File file = new File("/home/2018/spring/nyu/6513/sy2160/Finals/P3/result/5_Fold.txt");
        FileOutputStream fos = new FileOutputStream(file);
        String content = "total train data / fold:" + training.count()+"\n";
        content += "total test / fold:" + test.count() +"\n";
        content += "seed:" + "17"+"\n";
        content += "AUC = " + metrics.areaUnderROC()+"\n";
        content += "ACCURACY = " + metrics2.accuracy()+"\n";
        content += "PreCISION = " + metrics.areaUnderPR()+"\n";
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        sc.stop();


		
		
        
        
	}
}
