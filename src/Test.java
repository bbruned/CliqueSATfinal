import java.io.IOException;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		Clique c=new Clique();
		//c.LoadData("egfr20_flat");
		//c.LoadData("tgf_CADBIOM_flat");
		c.LoadData("tcrsig94_flat");
		//c.LoadData("metazoan");
		//c.LoadData("ERBB_G1-S");
		//c.Solve();
		c.Solve();
		//System.out.println("hello world");
		c.Save("cliques");
	}

}
