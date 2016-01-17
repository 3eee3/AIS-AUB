import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class ArffData {
	public static void createData() throws IOException{
				//instances are blocks of 100
				//resulting in 150 blocks for each user
				String[][] commands = MainClass.readFiles();
				int [][] masquerade_summary = MainClass.loadSummaryFile();//[0-9999][0-49]
			
				//Create list of all commands
				Collection<String> allCommands = new HashSet<String>();
				for(int iUser=0;iUser<50;iUser++){
					for(int j=0;j<15000;j++){
						allCommands.add(commands[iUser][j]);
					}
				}
				
				//represent commands by ids ranging from 0 - 855 (allCommands.size-1);
				Map<String,Integer> m =new Hashtable<String,Integer>(allCommands.size());
				int id=0;
				for(String command: allCommands){
					m.put(command,new Integer(id));
					id++;
				}
				
				for(int user=0;user<50;user++){
					Writer wr = new FileWriter(new File("src/weka-data/"+"User"+(user+1))+".arff");
					//create Header
					wr.write("@RELATION user"+(user+1)+"\n");
					for(int i=0;i<100;i++){
						wr.write("@ATTRIBUTE cmd"+(i+1)+" {");
						for(int id2=0;id2<allCommands.size()-1;id2++){
							wr.write(id2+",");
						}
						wr.write(allCommands.size()-1+"}\n");
					}
					wr.write("@ATTRIBUTE class {normal,masquerade}\n\n@DATA\n");
					//create training data
					for(int jUser=0;jUser<50;jUser++){
						for(int i=0;i<50;i++){
							for(int j=0;j<100;j++){
								wr.write(m.get(commands[jUser][i*100+j])+",");
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
							wr.write(m.get(commands[user][i*100+j])+",");
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
	public static void createDataNew() throws IOException{
		//instances are blocks of 100
		//resulting in 150 blocks for each user
		String[][] commands = MainClass.readFiles();
		int [][] masquerade_summary = MainClass.loadSummaryFile();//[0-9999][0-49]
	
		//Create list of all commands
		Collection<String> allCommands = new HashSet<String>();
		for(int iUser=0;iUser<50;iUser++){
			for(int j=0;j<15000;j++){
				allCommands.add(commands[iUser][j]);
			}
		}
		
		//represent commands by ids ranging from 0 - 855 (allCommands.size-1);
		Map<String,Integer> m =new Hashtable<String,Integer>(allCommands.size());
		int id=0;
		for(String command: allCommands){
			m.put(command,new Integer(id));
			id++;
		}
		
		for(int user=0;user<50;user++){
			Writer wr = new FileWriter(new File("src/weka-data/"+"User"+(user+1))+".arff");
			//create Header
			wr.write("@RELATION user"+(user+1)+"\n");
			for(int i=0;i<100;i++){
				wr.write("@ATTRIBUTE cmd"+(i+1)+" {");
				for(int id2=0;id2<allCommands.size()-1;id2++){
					wr.write(id2+",");
				}
				wr.write(allCommands.size()-1+"}\n");
			}
			wr.write("@ATTRIBUTE class {normal,masquerade}\n\n@DATA\n");
			//create training data
			for(int jUser=0;jUser<50;jUser++){
				for(int i=0;i<49;i++){
					for(int offset=0;offset<100;offset+=5){
						for(int j=0;j<100;j++){
							wr.write(m.get(commands[jUser][i*100+j+offset])+",");
						}
						if(user==jUser){
							wr.write("normal");
						}else{
							wr.write("masquerade");
						}
						wr.write("\n");
					}
				}
			}
			//create test data
			for(int i=50;i<150;i++){
				for(int j=0;j<100;j++){
					wr.write(m.get(commands[user][i*100+j])+",");
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
}
