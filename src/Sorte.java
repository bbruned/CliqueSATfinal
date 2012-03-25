import java.util.Vector;


public class Sorte implements Comparable<Sorte> {
	public String Nom;
	public Vector<Processus> Processus;
	public Sorte(String nom) {
		super();
		Processus=new Vector<Processus>();
		Nom = nom;
	}
	
	//définition d'un ordre sur les sortes
	public int compareTo(Sorte n) {
		Integer a=0;
		Integer b=0;
		for(Processus p:this.Processus){
			a+=p.Voisins.size();
		}
		for(Processus p:n.Processus){
			b+=p.Voisins.size();
		}
		return 
	      b.compareTo(a);
	  }
}
