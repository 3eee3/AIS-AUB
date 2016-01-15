import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class MainClass {
	
	public static String[][] readFiles() throws FileNotFoundException{
		String[][] usercommands=new String[50][15000];
		for(int i=0;i<50;i++){
			Scanner sc = new Scanner(new File("src/masquerade-data/"+"User"+(i+1)),"UTF-8");
			int index=0;
			while(sc.hasNext()){
				usercommands[i][index]=sc.next();
				index++;
			}
			sc.close();
		}
		return usercommands;
	}
	
	/*
	 * @returns The training set.
	 */
	public static Map<String,CountP1P2>[] trainNaiveBayes(String[][] commands){
		double alpha=0.01d;
		int A;//Number of distinct commands in training set
		
		//Create list of all commands
		Collection<String> allCommands = new HashSet<String>();
		for(int iUser=0;iUser<50;iUser++){
			for(int j=0;j<5000;j++){
				allCommands.add(commands[iUser][j]);
			}
		}
		A=allCommands.size();
		//System.out.println("Distinct commands: size: "+allCommands.size()+ "("+allCommands+")");
		
		//Compute count of each command
		@SuppressWarnings("unchecked")
		Map<String,CountP1P2>[] maps = new Map[50];
		for(int iUser=0;iUser<50;iUser++){
			maps[iUser]=new Hashtable<String,CountP1P2>(A);
			for(int i=0;i<5000;i++){
				CountP1P2 value=maps[iUser].get(commands[iUser][i]);
				if(value==null){
					maps[iUser].put(commands[iUser][i],new CountP1P2(1,null,null));
				}else{
					value.count+=1;
					//dict[iUser].put(commands[iUser][i],value);
				}
			}
			//System.out.println("User "+indUser+":"+maps[indUser]);
		}
		
		//Compute self and non-self probability for each user and command
		for(int iUser=0;iUser<50;iUser++){
			for (String command:allCommands){
			    CountP1P2 value = maps[iUser].get(command);
			    //Compute self probability
			    if(value==null){
			    	maps[iUser].put(command, new CountP1P2(0,alpha/(5000+A*alpha),null));
			    }else{
			    	value.p_self=(value.count+alpha)/(5000+A*alpha);
			    }
			    
			    //Compute non-self probability
			    int sumCount=0;
			    value=maps[iUser].get(command);
			    for(int jUser=0; jUser<50; jUser++){
			    	if(jUser!=iUser){
			    		CountP1P2 jValue = maps[jUser].get(command);
			    		if(jValue!=null){
			    			sumCount+=maps[jUser].get(command).count;
			    		}
			    	}
			    }
			    value.p_non_self=(sumCount+alpha)/(5000*49+alpha*A);
			}
			//System.out.println("User "+iUser+":"+maps[iUser]);
		}
		return maps;
	}
	
	/*
	 * @returns 1 if the block is classified as masquerade
	 *          0 if the block is classified as normal
	 */
	public static int classifyNaiveBayes(String[] block, Map<String,CountP1P2> userTrainingData, double threshold){
		int A = userTrainingData.size();
		double alpha=0.01d;
		//double pBlock=1.0d;
		//double pBlockNonSelf=1.0d;
		double pBlock=0.0d;
		double pBlockNonSelf=0.0d;
		for(String x:block){
			CountP1P2 value = userTrainingData.get(x);
			if(value==null){//case: command did not appear in training Set
				pBlock+=Math.log(alpha/(5000+alpha*A));
				pBlockNonSelf+=Math.log(alpha/(5000*49+alpha*A));
				//pBlock*=(alpha/(5000+alpha*A));
				//pBlockNonSelf*=alpha/(5000*49+alpha*A);
			}else{
				pBlock+=Math.log(value.p_self);
				pBlockNonSelf+=Math.log(value.p_non_self);
				//pBlock*=value.p_self;
				//pBlockNonSelf*=value.p_non_self;
			}
		}
		//System.out.println("pBlock:"+pBlock + " pBlockNonSelf:"+pBlockNonSelf);
		if(pBlock-pBlockNonSelf<threshold){//masquerade
			return 1;
		}else{//no masquerade
			return 0;
		}
	}
	public static void checkClassifier(Map<String,CountP1P2>[] classifier){
		for(int i=0;i<50;i++){
			double sum=0.0d;
			double sum1=0.0d;
			for(CountP1P2 x:classifier[i].values()){
				sum+=x.p_self;
				sum1+=x.p_non_self;
			}
			System.out.println("sum1:"+sum+" sum2:"+sum1);
		}
	}
	
	public static int[][] loadSummaryFile() throws FileNotFoundException{
		int[][] summary=new int[100][50];
			Scanner sc = new Scanner(new File("src/masquerade-data/masquerade_summary.txt"),"ASCII");
			int iindex=0;
			int jindex=0;
			while(sc.hasNext()){
				if(jindex>=50){
					iindex++;
					jindex=0;
				}
				summary[iindex][jindex]=sc.nextInt();
				jindex++;
			}
		sc.close();
		return summary;
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		double threshold=-100;
		//rows correspond to blocks, columns to users
		int [][] classifiedBlocks = new int [100][50];
		//load schonlau et al masquerade data
		String[][] commands = readFiles();
		Map<String,CountP1P2>[] trainingData;
		//train
		trainingData=trainNaiveBayes(commands);
		//checkClassifier(trainingData);
		//classify
		for(int iUser=0;iUser<50;iUser++){
			for(int iBlock=0;iBlock<100;iBlock++){
				String[] block =Arrays.copyOfRange(commands[iUser], 5000+iBlock*100, 5000+iBlock*100+100);
				classifiedBlocks[iBlock][iUser] = classifyNaiveBayes(block,trainingData[iUser],threshold);
			}
		}
		//evaluate performance
		int [][] masquerade_summary = loadSummaryFile();
		Statistics st = new Statistics(classifiedBlocks,masquerade_summary);
		System.out.println("threshold:"+threshold+ " hitrate:"+st.hitRate+" falsepositiverate:"+st.falsePositiveRate);
	}

}
