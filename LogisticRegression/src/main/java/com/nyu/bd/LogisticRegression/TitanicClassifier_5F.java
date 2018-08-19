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
        JavaRDD<LabeledPoint> oriLP = oriData.filter(l -> !"PassengerId".equals(l.split(COMMA_DELIMITER)[0])) 
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
        oriLP = oriLP.filter(l -> !(l==null));
        
        double truePositive=0,trueNegative=0,falsePositive=0,falseNegative = 0,auc = 0;
        int ntrain = 0;
        int ntest = 0;
        long n = 5;
        
        
        for(int i = 0; i < n; i++) {
        		JavaRDD<LabeledPoint>[] split = oriLP.randomSplit(new double[] {0.8, 0.2}, 17);
            JavaRDD<LabeledPoint> test = split[1];
            JavaRDD<LabeledPoint> training = split[0];
	

	        LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
	                .run(training.rdd());
	        model.clearThreshold();
	        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = test.map(
	                new Function<LabeledPoint, Tuple2<Object, Object>>() {
	                    public Tuple2<Object, Object> call(LabeledPoint p) {
	                        Double prediction = (double)Math.round(model.predict(p.features()));
	                        return new Tuple2<Object, Object>(prediction, p.label());
	                    }
	                }
	        );
	
	        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
	        
	        truePositive += predictionAndLabels.filter(pl -> (pl._1()).equals(pl._2()) && pl._1().equals(1d)).count();
	        trueNegative += predictionAndLabels.filter(pl -> (pl._1()).equals(pl._2()) && pl._1().equals(0d)).count();
	        falsePositive += predictionAndLabels.filter(pl -> !(pl._1()).equals(pl._2()) && pl._2().equals(0d)).count();
	        falseNegative += predictionAndLabels.filter(pl -> !(pl._1()).equals(pl._2()) && pl._2().equals(1d)).count();

	        auc += metrics.areaUnderROC();
	        ntrain = (int) training.count();
	        ntest = (int)test.count();

        }
        
        
        double tpr = truePositive/(truePositive+falseNegative);
        double tnr = trueNegative/(trueNegative+falsePositive);
        double fpr = falsePositive/(falsePositive+trueNegative);
        double fnr = falseNegative/(falseNegative+truePositive);
        double accuracy = (truePositive+trueNegative)/(truePositive+trueNegative+falsePositive+falseNegative);
        double precision1 = truePositive/(truePositive+falsePositive);
        auc = auc/n;
  

        File file = new File("/home/2018/spring/nyu/6513/sy2160/Finals/P3/result/5_Fold.txt");
        FileOutputStream fos = new FileOutputStream(file);
        String content = "train data per fold:"+ntrain +"\n";
        content += "test data per fold:"+ntest+"\n";
        content += "seed:"+"17"+"\n";
        content += "auc: " + auc+"\n";
        content += "accuracy: " + accuracy+"\n";
        content += "precision: " + precision1+"\n";
        content += "tpr: "+tpr+"\n";
        content += "tnr: "+tnr+"\n";
        content += "fpr: "+fpr+"\n";
        content += "fnr: "+fnr+"\n";
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        sc.stop();
 
        
        
        
	}
}
