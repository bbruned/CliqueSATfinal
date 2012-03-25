import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.varselector.MostConstrained;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Configuration;


public class Clique {
	public Vector<Sorte> Sortes;
	public Map<String,Integer> Numerotation;
	public Vector<Sorte> Cliques;
	public int Dim;
	
	public void LoadData(String fichier) throws IOException{
		try{
			Reader reader = new FileReader(fichier+".ph");
			// Prise en compte d'une ligne
			BufferedReader in = new BufferedReader(reader);
			String ligne = in.readLine();
			//lecture des sortes
			while ((ligne != null)&&(ligne.startsWith("process"))){
				// traitement de la ligne courante: decouper les mots separes par des delimiteurs
				String[] temp=ligne.split(" "); 
				Sorte s =new Sorte(temp[1]);
				int nb=Integer.parseInt(temp[2]);
		        for (int i=0; i<nb+1; i++){ 
		        	s.Processus.add(new Processus(temp[1],Dim,i));
		        	Dim++;
		        }
		        Sortes.add(s);
		        Numerotation.put(temp[1], Sortes.size()-1);
		        // Lecture de la prochaine ligne 
		        ligne = in.readLine();
			}
			//lecture des arcs
			ligne=in.readLine();
			while ((ligne != null)&&(!ligne.equals(""))){
				// traitement de la ligne courante: decouper les mots separes par des delimiteurs
				String[] temp=ligne.split(" ");
				int n=Integer.parseInt(temp[1]);
				Processus p=Sortes.get(Numerotation.get(temp[0])).Processus.get(n);
				n=Integer.parseInt(temp[4]);
				Processus voisin=Sortes.get(Numerotation.get(temp[3])).Processus.get(n);
				p.Voisins.add(voisin);
				voisin.Voisins.add(p);
				// Lecture de la prochaine ligne
				ligne = in.readLine();
		    }
			
		}
		catch (Exception e){
			System.out.println(e.toString());
		}		
	}
	public Clique() {
		Sortes=new Vector<Sorte>();
		Numerotation=new HashMap<String,Integer>();
		Cliques=new Vector<Sorte>();
		Dim=0;
	}
	public void Solve(){
		
		//tri des sortes selon le nombre de relations
		Collections.sort(this.Sortes);
		int nb=0;
		for(Sorte s:Sortes)
			for(Processus p:s.Processus){
				p.Num=nb;
				nb++;
			}
				
		//1- Create the model
		CPModel m = new CPModel();
		
		//2- Create the variables
		IntegerVariable[] graph = Choco.makeIntVarArray("clique",Dim);
		
		//3- Post constraints
		
		//variables binaires
		for(int i=0;i<Dim;i++){
			m.addConstraint(Choco.leq(graph[i],1));
			m.addConstraint(Choco.leq(0,graph[i]));
		}
		
		//un seul sommet par sortes
		IntegerExpressionVariable  expression;
		for (Sorte s:Sortes){
			expression=Choco.plus(graph[s.Processus.get(0).Num],0);
			for(int i=1;i<s.Processus.size();i++){
				expression=Choco.plus(graph[s.Processus.get(i).Num],expression);
			}
			m.addConstraint(Choco.eq(expression,1));
		}
		
		//exclusion d'une clique de deux sommets reliés
		for (Sorte s:Sortes) {
			for (Processus p:s.Processus) {
				for(Processus voisin:p.Voisins){
					if(p.Num<=voisin.Num){
						m.addConstraint(Choco.leq(Choco.plus(graph[p.Num], graph[voisin.Num]),1));
					}
				}
			}
		}
		
		//4- Create the solver
		CPSolver s = new CPSolver();
		s.read(m);
		
		//définition des stratégies du backtracking
		//s.setVarIntSelector(new RandomIntVarSelector(s));
		//s.setVarIntSelector(new MostConstrained(s));
		
		//temps maximum de résolution 
		int timelimit=30000;
		s.setTimeLimit(timelimit);
		
		//borne supérieure du nombre de solutions à trouver
		int solmax=100000;
		
		s.solve();
		
		if (s.isFeasible()==Boolean.TRUE)do {
	        
			// Exploitation de cette solution
			
			Sorte sol= new Sorte("clique"); 
			for(Sorte sorte:Sortes){
				for(Processus p:sorte.Processus){
					int n=s.getIntVar(p.Num).getVal();
					if (n==1){
						sol.Processus.add(p);
					}
				}
			}
			Cliques.add(sol);		
			
			//vérification des contraintes d'exclusion de deux sommets d'une clique
			
			/*for(Processus proc:sol.Processus){
				for(Processus voisin:proc.Voisins){
					if (s.getIntVar(voisin.Num).getVal()+s.getIntVar(proc.Num).getVal()==2){
						System.out.println(proc.Num+" "+voisin.Num);						
					}
				}
			}*/
			
	     // On relance la recherche d'une solution 
		} while ((s.nextSolution() == Boolean.TRUE)&&(s.getSolutionCount()<solmax));
		
		//nombre de solution trouvées+temps de résolution 
		System.out.println("Nombre de solutions trouvées : "+s.getSolutionCount());
		System.out.println("Temps de résolution"+" : "+s.getTimeCount()+" ms");
		System.out.println("Nombre de noeuds"+" : "+s.getNodeCount());
		
		
		//vérification des cliques
		
		/*if (!Cliques.isEmpty()){
			for(Sorte sol:Cliques){
				for(Processus p:sol.Processus){
					for(Processus voisin :p.Voisins){
						if(sol.Processus.contains(voisin)){
							System.out.println("c'est pas une clique");
							System.out.println(sol.Processus.indexOf(p)+" "+sol.Processus.indexOf(voisin));
						}
					}
				}
			}
		}*/
	}
	
	//sauvegarde des cliques trouvées dans un fichier texte
	public void Save(String fichier) throws IOException{
		Writer writer = new FileWriter(fichier+".txt"); 
		Vector<String> affiche=new Vector<String>();
		if (!Cliques.isEmpty()){
			writer.write(Cliques.size()+" cliques"+" : "+"\n");
			// affichage de toutes les cliques
			for(Sorte s: Cliques)	{  
				writer.write("["+" ");
				for(Processus p:s.Processus){
					affiche.add(p.Sorte+"_"+p.indice+" ");
				}
				//tri alphabétique
				Collections.sort(affiche);
				for(String st:affiche){
					writer.write(st);
				}
				affiche=new Vector<String>();
				writer.write("]"+"\n");
			}
		}
		else{
			writer.write("aucune clique"+"\n");
		}			
		writer.close();
	}
}
