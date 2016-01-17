import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.pmml.consumer.Regression;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class WekaClassifier {
	
	public static void createArffData() throws IOException{
		//instances are blocks of 100
		//resulting in 150 blocks for each user
		String[][] commands = MainClass.readFiles();
		int [][] masquerade_summary = MainClass.loadSummaryFile();//[0-9999][0-49]
	
		for(int user=0;user<50;user++){
			Writer wr = new FileWriter(new File("src/weka-data/"+"User"+(user+1))+".arff");
			//create Header
			wr.write("@RELATION user"+(user+1)+"\n");
			for(int i=0;i<100;i++){
				wr.write("@ATTRIBUTE cmd"+(i+1)+" STRING\n");
			}
			wr.write("@ATTRIBUTE class {normal,masquerade}\n\n@DATA\n");
			//create training data
			for(int jUser=0;jUser<50;jUser++){
				for(int i=0;i<50;i++){
					for(int j=0;j<100;j++){
						wr.write(commands[jUser][i*100+j]+",");
					}
					if(user==jUser){
						wr.write("normal");
					}else{
						wr.write("masquerade");
					}
					wr.write("\n");
				}
			}
			//create test data
			for(int i=50;i<150;i++){
				for(int j=0;j<100;j++){
					wr.write(commands[user][i*100+j]+",");
				}
				if(masquerade_summary[i-50][user]==0){
					wr.write("normal");
				}else{
					wr.write("masquerade");
				}
				wr.write("\n");
			}
			wr.flush();
			wr.close();
		}	
		System.out.println("Created arff files");
	}
	
	public static BufferedReader readDataFile(String filename){
		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
		return inputReader;
	}
	
	public static void main(String[] args) throws Exception{
		//create arff data out of the schonlau data
		//createArffData();
		//ArffData.createData();
		ArffData.createDataNew();
		
		int [][] classifiedBlocks = new int [100][50];
		
		for(int iUser=0;iUser<50;iUser++){
			System.out.println("-------------User"+(iUser+1)+"---------------");
			// load data
			BufferedReader reader = readDataFile("src/weka-data/User"+(iUser+1)+".arff");
			System.out.println("Loaded arff data");
			
			//create traindata and testdata
			Instances data = new Instances(reader); //Instances is List of types <Instance>
			Instances traindata = new Instances(data, 0, 49*50*20);
			Instances testdata = new Instances(data, 49*50*20, 100);
			
			//The last attribute is the Class (type numeric, 0 for normal block, 1 for masquerade block)
			traindata.setClassIndex(traindata.numAttributes() - 1);
			testdata.setClassIndex(testdata.numAttributes()-1);
			
			//train Classifier
			String[] options = weka.core.Utils.splitOptions("-R 1 2 3 20");
			//BayesNet classifier = new BayesNet(); //(97-54)
			//System.out.println(classifier.globalInfo());
			//RandomForest classifier = new RandomForest();//out of memory
			NaiveBayes classifier = new NaiveBayes(); //(97-64)
			
			//classifier.setOptions(options);
			classifier.buildClassifier(traindata);
			System.out.println("trained classfier");
			//testing output
			for(int i=0;i<100;i++){   
				classifiedBlocks[i][iUser] = (int) classifier.classifyInstance(testdata.instance(i));
				/*
				System.out.print("ID: " + testdata.instance(i).value(0));
				System.out.print(", actual: " + testdata.classAttribute().value((int) testdata.instance(i).classValue()));
				System.out.println(", predicted: " + testdata.classAttribute().value(classifiedBlocks[i][iUser]));
				*/
			}
			
			/*
			// evaluate classifier and print some statistics
			 Evaluation eval = new Evaluation(traindata);
			 eval.evaluateModel(classifier, testdata);
			 System.out.println(eval.toSummaryString("\nResults\n======\n", false));*/
		}
		//evaluate performance
		int [][] masquerade_summary = MainClass.loadSummaryFile();
		Statistics st = new Statistics(classifiedBlocks,masquerade_summary);
		System.out.println("threshold:unknown+"+ " hitrate:"+st.hitRate+" falsepositiverate:"+st.falsePositiveRate);
	 }
}
