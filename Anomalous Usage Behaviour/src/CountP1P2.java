public class CountP1P2 {
	public int count;
	public Double p_self;
	public Double p_non_self;
	
	public CountP1P2(int count, Double p_self, Double p_non_self){
		this.count=count;
		this.p_self=p_self;
		this.p_non_self=p_non_self;
	}
	
	public String toString(){
		return ("("+count+","+p_self+","+p_non_self+")");
	}
}