/*
 * Class used to evaluate performance of Naive Bayes classifier//
 * Computes TP, FP, TN, FN
 * Computes true positive rate (hit rate), false positive rate
 * 
 */
public class Statistics {
	int TP=0,FP=0,TN=0,FN=0;
	double hitRate;
	double falsePositiveRate;
	double trueNegativeRate;
	double falseNegativeRate;
	public Statistics(int[][] detected, int[][] real){
		for(int i=0;i<detected.length;i++){
			for(int j=0;j<detected[0].length;j++){
				if(detected[i][j]==1 && real[i][j]==1){
					TP++;
				}else if(detected[i][j]==0 && real[i][j]==0){
					TN++;
				}else if(detected[i][j]==1 && real[i][j]==0){
					FP++;
				}else if(detected[i][j]==0 && real[i][j]==1){
					FN++;
				}else{
					System.err.println("Wrong array entries for classified blocks or masquerade_summary. Should be 0 or 1");
				}
			}
		}
		hitRate = (double) TP / (TP+FN);
		falsePositiveRate = (double) FP / (FP+TN);
		trueNegativeRate = (double) TN / (TN+FP);
		falseNegativeRate = 1 - hitRate;
		
				
	}
}
