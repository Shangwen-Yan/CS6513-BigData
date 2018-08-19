package com.nyu.BigDataClass.sparkML;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class SparkRDD {
	public static void main(String[] args) {
		String inputFile = "./wordCountSource.txt";
		String outputFile = "./Output2";
		SparkConf conf = new SparkConf().setAppName("wordCount").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> input = sc.textFile(inputFile, 10);
		JavaRDD<String> words = input.flatMap(l -> Arrays.asList(l.split(" ")).iterator());
		JavaPairRDD<String,Integer> counts = words.mapToPair(
				new PairFunction<String, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(String x) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2(x, 1);
					}
				}
				).reduceByKey(new Function2<Integer,Integer,Integer>() {
					@Override
					public Integer call(Integer x, Integer y) throws Exception {
						// TODO Auto-generated method stub
						return x+y;
					}

				});
		counts.saveAsTextFile(outputFile);
	}
}
