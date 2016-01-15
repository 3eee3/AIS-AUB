import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
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
		
		// load data
		BufferedReader reader = readDataFile("src/weka-data/User6.arff");
		System.out.println("Loaded arff data");
		
		//create traindata and testdata
		Instances data = new Instances(reader); //Instances is List of types <Instance>
		Instances traindata = new Instances(data, 0, 2500);
		Instances testdata = new Instances(data, 2500, 100);
		 
		//The last attribute is the Class (type numeric, 0 for normal block, 1 for masquerade block)
		traindata.setClassIndex(traindata.numAttributes() - 1);
		testdata.setClassIndex(testdata.numAttributes()-1);
		
		//train Classifier
		NaiveBayes classifier = new NaiveBayes();
		System.out.println("options: "+classifier.getOptions());
		classifier.buildClassifier(traindata);
		System.out.println("trained classfier");
		
		// evaluate classifier and print some statistics
		 Evaluation eval = new Evaluation(traindata);
		 eval.evaluateModel(classifier, testdata);
		 System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	 }
}
