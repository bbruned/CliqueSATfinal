import java.util.Vector;

public class Processus {
	public int Num;
	public int indice;
	public String Sorte;
	public Vector<Processus> Voisins;
	public Processus(String sorte,int num,int indice) {
		super();
		Num=num;
		this.indice=indice;
		Voisins=new Vector<Processus>();
		Sorte = sorte;
	}
	
}
