package algorithms;

import java.awt.Point;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


public class DefaultTeam {
  private static int numeroDeGraphe = 0 ;

  private boolean estArete(Point a, Point b, int edgeThreshold) {
    return a.distance(b) < edgeThreshold;
  }

  private ArrayList<Point> voisins(Point p, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> result = new ArrayList<Point>();

    for (Point point : points)
      if (estArete(p, point, edgeThreshold) && !point.equals(p))
        result.add(point);

    return result;
  }

  private boolean estEnsembleDominant(ArrayList<Point> ensembleDominant, ArrayList<Point> points, int edgeThreshold) {
    Point p;
    ArrayList<Point> pointsCPY = (ArrayList)points.clone();

    for (int i = 0; i < ensembleDominant.size(); i++) {
      p = ensembleDominant.get(i);
      pointsCPY.remove(p);
      pointsCPY.removeAll(voisins(p, pointsCPY, edgeThreshold));
    }
    return pointsCPY.size() == 0;
  }

  private int degre(Point p, ArrayList<Point> points, int edgeThreshold) {
    return voisins(p, points, edgeThreshold).size();
  }

  // faire glouton avec paire de noeuds qui couvrent le plus de voisins.
  // faire glouton avec triplé de noeuds qui couvrent le plus de voisins.

  // prendr ele meilleur ensemble dom et faire des permutations.

  private void supprime1Point(ArrayList<Point> ensembleDominant, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> startSet = (ArrayList<Point>)ensembleDominant.clone();

    while(startSet.size()>ensembleDominant.size()) {
      startSet=(ArrayList<Point>)ensembleDominant.clone();
      for (Point p : startSet) {
        ensembleDominant.remove(p);
        if (!estEnsembleDominant(ensembleDominant, points, edgeThreshold))
          ensembleDominant.add(p);
      }
    }
  }

  private void localSearch21(ArrayList<Point> ensDom, ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> reste = (ArrayList<Point>)points.clone();
    ArrayList<Point> a = (ArrayList<Point>)points.clone();
    ArrayList<Point> nonDomine = new ArrayList<>();
    ArrayList<Point> ensDomDeK = new ArrayList<>();
    ArrayList<Point> voisins = new ArrayList<>();

    reste.removeAll(ensDom);

    Point pi, pj, pk;
    boolean pointADominer, passerAuxPointsSuivants=false;
    for(int i = ensDom.size() - 1 ; i >= 0 ; i--) {
      passerAuxPointsSuivants=false;

      pi=ensDom.get(i);

      // si un des voisins n'est pas de voisin qui est toujours dans ensDom, il faut le dominer avec le nouveau point
      /*pointADominer = true;
      for(Point v : voisins) {
        if(pointADominer=false)
          break;
        for (Point v2v : voisins(v, points, edgeThreshold)) {
          if (ensDom.contains(v2v))
            pointADominer = false;
        }
      }
      if (pointADominer)
        nonDomine.add(pi);*/

      ensDom.remove(pi);

      for (int j = i-1; j >= 0; j--) {
        pj = ensDom.get(j);
        if (pj.distance(pi)>edgeThreshold*4)
          continue;

        //voisins = voisins(pj, points, edgeThreshold);
        // si un des voisins n'est pas de voisin qui est toujours dans ensDom, il faut le dominer avec le nouveau point
        /*pointADominer = true;
        for (Point v : voisins) {
          if (pointADominer = false)
            break;
          for (Point v2v : voisins(v, points, edgeThreshold)) {
            if (ensDom.contains(v2v))
              pointADominer = false;
          }
        }
        if(pointADominer)
          nonDomine.add(pj);*/

        ensDom.remove(pj);

        for (int k = reste.size() - 1; k >= 0; k--) {
          //ensDomDeK.clear();
          pk = reste.get(k);
          if(pk.distance(pi)>2*edgeThreshold || pk.distance(pj)>2*edgeThreshold)
            continue;
          /*ensDomDeK.addAll(voisins(pk, points, edgeThreshold));
          ensDomDeK.add(pk);*/

          ensDom.add(pk);

          if(estEnsembleDominant(ensDom, points, edgeThreshold)) {
            passerAuxPointsSuivants = true;
            System.out.println(pk+" remplace "+pi +" et "+pj);
            break;
          }

          ensDom.remove(pk);
          // si on peut remplacer les 2 points par celui-là car tous les points finaux seront dominés quand même :
          /*if(ensDomDeK.containsAll(nonDomine)) {
            System.out.println("On a vérifié si c'était ok "+pi+" "+pj + " "+pk);

            ensDom.remove(pi);
            ensDom.remove(pj);
            ensDom.add(pk);
            reste.remove(pk);
            passerAuxPointsSuivants=true;
            if(estEnsembleDominant(ensDom, points, edgeThreshold))
              System.out.println("C'est vraiment ok.");
            break;
          }*/
        }
        if(passerAuxPointsSuivants)
          break;
        ensDom.add(pj);
      }
      if(passerAuxPointsSuivants)
        continue;
      ensDom.add(pi);
    }
  }

  private void localSearch32(ArrayList<Point> ensDom, ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> reste = (ArrayList<Point>)points.clone();
    reste.removeAll(ensDom);

    Point ph, pi, pj, pk, pl;
    boolean passerAuxPointsSuivants;
    for(int i = ensDom.size() - 1 ; i >= 0 ; i--) {
      passerAuxPointsSuivants=false;

      pi=ensDom.get(i);

      ensDom.remove(pi);

      for (int j = i-1; j >=0 ; j--) {
        pj = ensDom.get(j);

        if (pj.distance(pi)>edgeThreshold*4)//6
          continue;

        ensDom.remove(pj);
        for (int h = j-1; h >=0 ; h--) {
          ph = ensDom.get(h);

          if (ph.distance(pi)>edgeThreshold*4 || ph.distance(pj)>edgeThreshold*4) //4
            continue;

          ensDom.remove(ph);

          for (int k = reste.size() - 1; k >=0 ; k--) {
            pk = reste.get(k);
            if (pk.distance(pi) > 2 * edgeThreshold || pk.distance(pj) > 2 * edgeThreshold
                    || pk.distance(ph) > 2 * edgeThreshold)
              continue;

            ensDom.add(pk);

            for (int l = k - 1; l >= 0; l--) {
              pl = reste.get(l);
              if (pl.distance(pi) > 2 * edgeThreshold || pl.distance(pj) > 2 * edgeThreshold
                      || pl.distance(ph) > 2 * edgeThreshold)
                continue;

              ensDom.add(pl);

              if (estEnsembleDominant(ensDom, points, edgeThreshold)) {
                passerAuxPointsSuivants = true;
                System.out.println(pk + " et "+ pl + " remplacent " + pi + ", " + pj + " et "+ph);
                break;
              }

              ensDom.remove(pl);
            }

            if(passerAuxPointsSuivants)
              break;
            ensDom.remove(pk);
          }
          if(passerAuxPointsSuivants)
            break;
          ensDom.add(ph);
        }
        if(passerAuxPointsSuivants)
          break;
        ensDom.add(pj);
      }
      if(passerAuxPointsSuivants)
        continue;
      ensDom.add(pi);
    }
  }

  /*private void localSearch43(ArrayList<Point> ensDom, ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> reste = (ArrayList<Point>)points.clone();
    reste.removeAll(ensDom);

    Point ph, pi, pj, pk, pl;
    boolean passerAuxPointsSuivants;
    for(int i = ensDom.size() - 1 ; i > 0 ; i--) {
      passerAuxPointsSuivants=false;

      pi=ensDom.get(i);

      ensDom.remove(pi);

      ensDom.

      for (int j = i-1; j > 0; j--) {
        pj = ensDom.get(j);

        if (pj.distance(pi)>edgeThreshold*4)//6
          continue;

        ensDom.remove(pj);
        for (int h = j-1; h > 0; h--) {
          ph = ensDom.get(h);

          if (ph.distance(pi)>edgeThreshold*4 || ph.distance(pj)>edgeThreshold*4) //4
            continue;

          ensDom.remove(ph);

          for (int k = reste.size() - 1; k > 0; k--) {
            pk = reste.get(k);
            if (pk.distance(pi) > 2 * edgeThreshold || pk.distance(pj) > 2 * edgeThreshold
                    || pk.distance(ph) > 2 * edgeThreshold)
              continue;

            ensDom.add(pk);

            for (int l = k - 1; l > 0; l--) {
              pl = reste.get(l);
              if (pl.distance(pi) > 2 * edgeThreshold || pl.distance(pj) > 2 * edgeThreshold
                      || pl.distance(ph) > 2 * edgeThreshold)
                continue;

              ensDom.add(pl);

              if (estEnsembleDominant(ensDom, points, edgeThreshold)) {
                passerAuxPointsSuivants = true;
                System.out.println(pk + " et "+ pl + " remplacent " + pi + ", " + pj + " et "+ph);
                break;
              }

              ensDom.remove(pl);
            }

            if(passerAuxPointsSuivants)
              break;
            ensDom.remove(pk);
          }
          if(passerAuxPointsSuivants)
            break;
          ensDom.add(ph);
        }
        if(passerAuxPointsSuivants)
          break;
        ensDom.add(pj);
      }
      if(passerAuxPointsSuivants)
        continue;
      ensDom.add(pi);
    }
  }*/

  private ArrayList<Point> methode2(ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> ensDom =  new ArrayList<Point>();
    ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
    ArrayList<Point> reste = (ArrayList<Point>) points.clone();
    ArrayList<Point> voisins = new ArrayList<>();
    ArrayList<Point> result = (ArrayList<Point>) points.clone();

    Point p;

    for(int t=0; t<1000000; t++) {
      result = ensDom;
      couvertureEnsDom =  new ArrayList<Point>();
      reste = (ArrayList<Point>) points.clone();
      voisins = new ArrayList<>();

      while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
        Collections.shuffle(reste, new Random(System.nanoTime() + reste.size()));
        p = reste.get(0);

        if (!couvertureEnsDom.containsAll(voisins(p, points, edgeThreshold)) || !couvertureEnsDom.contains(p)) {
          ensDom.add(p);
          voisins = voisins(p, reste, edgeThreshold);
          couvertureEnsDom.addAll(voisins);
          couvertureEnsDom.add(p);
          reste.removeAll(voisins);
        }

        reste.remove(p);
      }
      if(result.size()<ensDom.size())
        result=ensDom;
    }
    return result;
  }

  Point degreMax(ArrayList<Point> points, int edgeThreshold){
    Point res=null;
    int dres=-1, d;
    for(Point p : points)
      if(dres<(d=degre(p, points, edgeThreshold)) || dres==-1) {
        dres=d;
        res=p;
      }

    return res;
  }

  // prendre 100 ordres aléatoires, fiare un aordre aléatoire. puis mettre 1er noeud, 2e noeud etc. S'i lcouvre une arête. Puis refoire une passe dessus pour voir si c'ets mieux
  // puis refaire un autre ordre etc. 1 million de fois.

  // ordre aléatoire, mettre 1e

  /* Methode principale :  enlever les noeuds de plus haut degré au début. */
  private ArrayList<Point> methode1(ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> ensDom =  new ArrayList<Point>();
    ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
    ArrayList<Point> reste = (ArrayList<Point>) points.clone();
    ArrayList<Point> voisins = new ArrayList<>();
    ArrayList<Point> result = (ArrayList<Point>) points.clone();

    Point p;

    result = ensDom;
    couvertureEnsDom =  new ArrayList<Point>();
    reste = (ArrayList<Point>) points.clone();
    voisins = new ArrayList<>();

    while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
      p=degreMax(reste, edgeThreshold);
      if (!couvertureEnsDom.containsAll(voisins(p, points, edgeThreshold)) || !couvertureEnsDom.contains(p)) {
        ensDom.add(p);
        voisins = voisins(p, reste, edgeThreshold);
        couvertureEnsDom.addAll(voisins);
        couvertureEnsDom.add(p);
        reste.removeAll(voisins);
      }

      reste.remove(p);
    }
    if(result.size()<ensDom.size())
      result=ensDom;

    return result;
  }

  /* Methode principale :  enlever les noeuds de plus haut degré au début en random sur les plus hauts noeuds. . */
  private ArrayList<Point> methode3(ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> ensDom =  new ArrayList<Point>();
    ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
    ArrayList<Point> reste = (ArrayList<Point>) points.clone();
    ArrayList<Point> voisins = new ArrayList<>();
    ArrayList<Point> result = (ArrayList<Point>) points.clone();

    ArrayList<Point> poubelle = new ArrayList<Point>();
    Point p;

    for (int t=0; t<100; t++) {
      ensDom=new ArrayList<Point>();
      couvertureEnsDom=new ArrayList<Point>();
      reste = (ArrayList<Point>) points.clone();

      int i = 0;
      while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
        i++;
        if(reste.isEmpty()) {
          reste.addAll(poubelle);
          poubelle=new ArrayList<Point>();
        }
        p = degreMax(reste, edgeThreshold);

        if (!reste.isEmpty() && new Random(System.nanoTime()).nextInt(10) < 5 && i < 10) {
          reste.remove(p);

          if(reste.isEmpty()) {
            reste.addAll(poubelle);
            poubelle=new ArrayList<Point>();
          }

          poubelle.add(p);
          p = degreMax(reste, edgeThreshold);
        }
        if (!couvertureEnsDom.containsAll(voisins(p, points, edgeThreshold)) || !couvertureEnsDom.contains(p)) {
          ensDom.add(p);
          voisins = voisins(p, reste, edgeThreshold);
          couvertureEnsDom.addAll(voisins);
          couvertureEnsDom.add(p);
          reste.removeAll(voisins);
        }

        reste.remove(p);
      }

      //System.out.println(numeroDeGraphe+") RES : "+result.size()+ " CURR : " +ensDom.size()+ " ITER :" +t);
      if (result.size() > ensDom.size())
        result = ensDom;
    }

    return result;
  }

  /*private ArrayList<Point> methode4(ArrayList<Point> points, int edgeThreshold){
    ArrayList<Point> ensDom =  new ArrayList<Point>();
    ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
    ArrayList<Point> reste = (ArrayList<Point>) points.clone();
    ArrayList<Point> voisins = new ArrayList<>();
    ArrayList<Point> result = (ArrayList<Point>) points.clone();

    ArrayList<Point> poubelle = new ArrayList<Point>();
    Point p;

    for (int t=0; t<50; t++) {
      ensDom=new ArrayList<Point>();
      couvertureEnsDom=new ArrayList<Point>();
      reste = (ArrayList<Point>) points.clone();

      int i = 0;
      while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
        i++;
        if(reste.isEmpty()) {
          reste.addAll(poubelle);
          poubelle=new ArrayList<Point>();
        }
        p = degreMin(reste, edgeThreshold);

        for(int j=reste.size()-1; j >= 0; j++)
          ;
        if (!couvertureEnsDom.containsAll(voisins(p, points, edgeThreshold)) || !couvertureEnsDom.contains(p)) {
          ensDom.add(p);
          voisins = voisins(p, reste, edgeThreshold);
          couvertureEnsDom.addAll(voisins);
          couvertureEnsDom.add(p);
          reste.removeAll(voisins);
        }

        reste.remove(p);
      }

      //System.out.println(numeroDeGraphe+") RES : "+result.size()+ " CURR : " +ensDom.size()+ " ITER :" +t);
      if (result.size() > ensDom.size())
        result = ensDom;
    }

    return result;
  }*/

  public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> result = (ArrayList<Point>)points.clone();

    result = methode3(points, edgeThreshold);

    ArrayList<Point> tmp;
    do {
      tmp = (ArrayList<Point>)result.clone();
      supprime1Point(result, points, edgeThreshold);
      System.out.println("1 On retire "+(tmp.size()-result.size()));
    } while(tmp.size()!=result.size());

    do {
      tmp = (ArrayList<Point>)result.clone();
      localSearch21(result, points, edgeThreshold);
      System.out.println("2 On retire "+(tmp.size()-result.size()));
    } while(tmp.size()!=result.size());

    do {
      tmp = (ArrayList<Point>)result.clone();
      localSearch32(result, points, edgeThreshold);
      System.out.println("3 On retire "+(tmp.size()-result.size()));
    } while(tmp.size()!=result.size());


    int score = score("graphe"+numeroDeGraphe);
    if(score == -1 || score > result.size())
      writePoints(result, "graphe"+numeroDeGraphe, "methode3");

    if(!estEnsembleDominant(result, points, edgeThreshold))
      System.out.println("NOT DOM !!!");

    // if (false) result = readFromFile("output0.points");
    // else saveToFile("output",result);
    //<<<<< REMOVE

    numeroDeGraphe++;
    return result;
  }

  public int score(String filename){
    try {
      Path file = Paths.get(filename);

      if(!Files.exists(file))
        return -1;
      //your code here
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      if(reader.readLine() == null){ // permet de skip la premiere ligne = score
        System.out.println("Fichier vide");
      }
      String line;
      while ((line = reader.readLine()) != null){
        String[] ligne = line.split(" ");
        return Integer.parseInt(ligne[0]);
      }
      reader.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public void writePoints(ArrayList<Point> points, String filename, String methodname){
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      int score = points.size();
      writer.write(methodname + " " + score+"\n");
      for(Point p : points){
        writer.write(p.x + " " + p.y+"\n");
      }
      writer.flush();
      writer.close();
    }catch(IOException ioe){
      ioe.printStackTrace();
    }
  }

  public ArrayList<Point> readPoints(String filename){
    ArrayList<Point> retour = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      if(reader.readLine() == null){ // permet de skip la premiere ligne = score
        System.out.println("Fichier vide");
      }
      String line;
      while ((line = reader.readLine()) != null){
        String[] coordonnees = line.split(" ");
        Point p = new Point(Integer.parseInt(coordonnees[0]), Integer.parseInt(coordonnees[1]));
        retour.add(p);
      }
      reader.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
    return retour;
  }

  //FILE PRINTER
  private void saveToFile(String filename,ArrayList<Point> result){
    int index=0;
    try {
      while(true){
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
        }
        index++;
      }
    } catch (FileNotFoundException e) {
      printToFile(filename+Integer.toString(index)+".points",result);
    }
  }
  private void printToFile(String filename,ArrayList<Point> points){
    try {
      PrintStream output = new PrintStream(new FileOutputStream(filename));
      int x,y;
      for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
      output.close();
    } catch (FileNotFoundException e) {
      System.err.println("I/O exception: unable to create "+filename);
    }
  }

  //FILE LOADER
  private ArrayList<Point> readFromFile(String filename) {
    String line;
    String[] coordinates;
    ArrayList<Point> points=new ArrayList<Point>();
    try {
      BufferedReader input = new BufferedReader(
              new InputStreamReader(new FileInputStream(filename))
      );
      try {
        while ((line=input.readLine())!=null) {
          coordinates=line.split("\\s+");
          points.add(new Point(Integer.parseInt(coordinates[0]),
                  Integer.parseInt(coordinates[1])));
        }
      } catch (IOException e) {
        System.err.println("Exception: interrupted I/O.");
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename);
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found.");
    }
    return points;
  }
}
