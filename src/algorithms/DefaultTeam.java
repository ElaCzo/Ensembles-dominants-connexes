package algorithms;

import java.awt.Point;
import java.io.*;
import java.lang.reflect.Array;
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

        for (Point point : points) {
            if (estArete(p, point, edgeThreshold) && !point.equals(p))
                result.add(point);
        }

        return result;
    }

    private boolean estEnsembleDominant(ArrayList<Point> ensembleDominant, ArrayList<Point> points, int edgeThreshold) {
        Point p;
        ArrayList<Point> pointsCPY = (ArrayList)points.clone();

        for (int i = 0; i < ensembleDominant.size(); i++) {
            p = ensembleDominant.get(i);
            pointsCPY.removeAll(voisins(p, pointsCPY, edgeThreshold));
            pointsCPY.remove(p);
        }

        return pointsCPY.size() == 0;
    }

    private int degre(Point p, ArrayList<Point> points, int edgeThreshold) {
        return voisins(p, points, edgeThreshold).size();
    }

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
        reste.removeAll(ensDom);

        Point pi, pj, pk;
        boolean passerAuxPointsSuivants;
        for(int i = ensDom.size() - 1 ; i >= 0 ; i--) {
            passerAuxPointsSuivants=false;
            pi=ensDom.get(i);
            ensDom.remove(pi);

            for (int j = i-1; j >= 0; j--) {
                pj = ensDom.get(j);
                if (pj.distance(pi)>edgeThreshold*4)
                    continue;

                ensDom.remove(pj);

                for (int k = reste.size() - 1; k >= 0; k--) {
                    //ensDomDeK.clear();
                    pk = reste.get(k);
                    if(pk.distance(pi)>2*edgeThreshold || pk.distance(pj)>2*edgeThreshold)
                        continue;

                    ensDom.add(pk);

                    if(estEnsembleDominant(ensDom, points, edgeThreshold)) {
                        passerAuxPointsSuivants = true;
                        System.out.println(pk+" remplace "+pi +" et "+pj);
                        break;
                    }

                    ensDom.remove(pk);
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

    /* Méthode : full random. Point pris au hasard qu'on ajout à ensDom.
    On teste à chaque fois si on a un ensemble dominant et on arrête l'algo si c'est le cas. */
    private ArrayList<Point> methode2(ArrayList<Point> points, int edgeThreshold){
        ArrayList<Point> ensDom =  new ArrayList<Point>();
        ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        ArrayList<Point> voisins = new ArrayList<>();
        ArrayList<Point> result = (ArrayList<Point>) points.clone();

        Point p;

        for(int t=0; t<100; t++) {
            result = ensDom;
            couvertureEnsDom =  new ArrayList<Point>();
            reste = (ArrayList<Point>) points.clone();

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

    Point degreMax(ArrayList<Point> liste, ArrayList<Point> ensemble, int edgeThreshold){
        Point res=null;
        int dres=-1, d;
        for(Point p : liste)
            if(dres<(d=degre(p, ensemble, edgeThreshold)) || dres==-1) {
                dres=d;
                res=p;
            }

        assert res!=null;
        return res;
    }

    Point degreMin(ArrayList<Point> points, int edgeThreshold){
        Point res=null;
        int dres=-1, d;
        for(Point p : points)
            if(dres>(d=degre(p, points, edgeThreshold)) || dres==-1) {
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

    /* Methode :  1 chance sur 2 d'enlever le noeud de plus haut degré ou le deuxième.
     Dans le cas où on prend le deuxième noeud, le premier est placé dans une "poubelle",
     dès que reste est vide et que ensDom n'est toujours pas un ensemble dominant,
     on remplit "reste" avec la poubelle. */
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

                if (!reste.isEmpty() && new Random(System.nanoTime()).nextInt(10) < 5) {
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

            if (result.size() > ensDom.size())
                result = ensDom;
        }

        return result;
    }

    /* Méthode : 1 chance sur 2 d'enlever le noeuds de plus haut degré ou le second noeud de plus haut degré.
    * Si le deuxième noeud est pris, le premier est immédiatement remis dans l'ensemble "reste".*/
    private ArrayList<Point> methode7(ArrayList<Point> points, int edgeThreshold){
        ArrayList<Point> ensDom =  new ArrayList<Point>();
        ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        ArrayList<Point> voisins = new ArrayList<>();
        ArrayList<Point> result = (ArrayList<Point>) points.clone();

        Point p, ptmp;


        for (int t=0; t<100; t++) {
            ensDom=new ArrayList<Point>();
            couvertureEnsDom=new ArrayList<Point>();
            reste = (ArrayList<Point>) points.clone();

            int i = 0;
            while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
                i++;

                p = degreMax(reste, edgeThreshold);

                if (reste.size()>1 && new Random(System.nanoTime()).nextInt(10) < 5) {
                    ptmp=p;
                    reste.remove(p);
                    p = degreMax(reste, edgeThreshold);
                    reste.add(ptmp);
                }

                // si p est de ddegré 0
                if(degre(p, reste, edgeThreshold)==0) {
                    ensDom.add(p);
                    couvertureEnsDom.add(p);
                    reste.remove(p);
                    continue;
                }

                ensDom.add(p);
                voisins = voisins(p, reste, edgeThreshold);
                reste.removeAll(voisins);
                reste.remove(p);
                couvertureEnsDom.addAll(voisins);
                couvertureEnsDom.add(p);
            }

            if (result.size() > ensDom.size())
                result = ensDom;
        }

        return result;
    }

    private ArrayList<Point> methode5(ArrayList<Point> points, int edgeThreshold){
        ArrayList<Point> ensDom =  new ArrayList<Point>();
        ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        ArrayList<Point> voisins = new ArrayList<>();
        ArrayList<Point> result = (ArrayList<Point>) points.clone();

        Point p, q, qtmp;

        for (int t=0; t<100; t++) {
            ensDom=new ArrayList<Point>();
            couvertureEnsDom=new ArrayList<Point>();
            reste = (ArrayList<Point>) points.clone();

            for(int i=reste.size()-1; i>=0 ; i--){
                if(degre(p=reste.get(i), reste, edgeThreshold)==0) {
                    ensDom.add(p);
                    couvertureEnsDom.add(p);
                }
            }

            for(int i=reste.size()-1; i>=0 ; i--){
                if(degre(p=reste.get(i), reste, edgeThreshold)==1) {
                    q=voisins(p, reste, edgeThreshold).get(0);
                    ensDom.add(q);
                    couvertureEnsDom.add(p);
                    couvertureEnsDom.add(q);
                }
            }

            reste.removeAll(couvertureEnsDom);

            int i = 0;
            while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
                i++;

                p = degreMin(reste, edgeThreshold);

                // !! vérifier si p est pas de degré 0 pour les autres méthodes !!
                if(degre(p, reste, edgeThreshold)==0) {
                    ensDom.add(p);
                    couvertureEnsDom.add(p);
                    reste.remove(p);
                    continue;
                }

                voisins = voisins(p, reste, edgeThreshold);

                q = degreMax(voisins, reste, edgeThreshold);

                if (reste.size()>1 && new Random(System.nanoTime()).nextInt(10) < 5) {
                    qtmp=q;
                    reste.remove(q);
                    q = degreMax(reste, edgeThreshold);
                    reste.add(qtmp);
                }

                ensDom.add(q);
                couvertureEnsDom.addAll(voisins=voisins(q, reste, edgeThreshold));
                couvertureEnsDom.add(q);
                reste.removeAll(voisins);
                reste.remove(q);
            }

            if (result.size() > ensDom.size())
                result = ensDom;
        }

        return result;
    }

    private ArrayList<Point> methode4(ArrayList<Point> points, int edgeThreshold){
        ArrayList<Point> ensDom =  new ArrayList<Point>();
        ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        ArrayList<Point> voisins = new ArrayList<>();
        ArrayList<Point> result = (ArrayList<Point>) points.clone();

        ArrayList<Point> poubelle = new ArrayList<Point>();
        Point p, q, qmax=null;

        // faire séparateurs
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

                ensDom.add(p);
                for(int j=ensDom.size()-1; j >= 0; j++)
                    if((q=ensDom.get(j)).distance(p)<edgeThreshold*2 && q.distance(p)>qmax.distance(p)) ;

                reste.remove(p);
            }

            if (result.size() > ensDom.size())
                result = ensDom;
        }

        return result;
    }

    private ArrayList<Point> indicesMax(ArrayList<Integer> liste, int degreMax, ArrayList<Point> reste, int edgeThreshold){
        ArrayList<Point> res = new ArrayList<Point>();
        for(int i=0; i<liste.size(); i++){
            if(liste.get(i)==degreMax)
                res.add(reste.get(i));
        }

        return res;
    }

    private ArrayList<Point> indicesMin(ArrayList<Integer> liste, int degreMin, ArrayList<Point> reste, int edgeThreshold){
        ArrayList<Point> res = new ArrayList<Point>();
        for(int i=0; i<liste.size(); i++){
            if(liste.get(i)==0)
                res.add(reste.get(i));
        }

        return res;
    }

    private Point chercheMeilleurVoisin(ArrayList<Point> ensDom, ArrayList<Point> reste, int edgeThreshold){
        ArrayList<Integer> nombreVoisinsOK = new ArrayList<Integer>(); // voisins à la bonne distance
        ArrayList<Integer> nombreVoisinsPasOK = new ArrayList<Integer>(); // voisins trop loin
        Point p, q;
        int nvOK, nvPasOK;
        for(int i=0; i<reste.size(); i++){
            p=reste.get(i);
            nvOK=0;
            nvPasOK=0;
            for(int j=0; j<ensDom.size(); j++) {
                q=ensDom.get(j);
                // on note les points selon s'ils sont à la bonne distance ou non des autres déjà présents dans ensDom
                if (q.distance(p)<2*edgeThreshold) {
                    if(q.distance(p)>2*edgeThreshold*2.0/3.0)
                        nvOK++;
                    else
                        nvPasOK++;
                }
            }

            nombreVoisinsOK.add(nvOK);
            nombreVoisinsPasOK.add(nvPasOK);
        }

        ArrayList<Point> lok, lpasok;
        int degreMax=Collections.max(nombreVoisinsPasOK);
        int imin, imax;
        lpasok = indicesMin(nombreVoisinsPasOK, 0, reste, edgeThreshold);

        lok = indicesMax(nombreVoisinsOK, degreMax, reste, edgeThreshold);

        lpasok.retainAll(lok);

        if (lpasok.isEmpty()){
            lpasok = indicesMin(nombreVoisinsPasOK, 1, reste, edgeThreshold);

            lpasok.retainAll(lok);
        }
        else{
            Collections.shuffle(lpasok, new Random(System.nanoTime()));
            return lpasok.get(0);
        }

        if(lpasok.isEmpty()){
            lpasok = indicesMin(nombreVoisinsPasOK, 2, reste, edgeThreshold);
            lpasok.retainAll(lok);
        }
        else{
            Collections.shuffle(lpasok, new Random(System.nanoTime()));
            return lpasok.get(0);
        }

        if(lpasok.isEmpty()){
            lpasok = indicesMin(nombreVoisinsPasOK, 2, reste, edgeThreshold);
        }
        else {
            Collections.shuffle(lpasok, new Random(System.nanoTime()));
            return lpasok.get(0);
        }

        if(!lpasok.isEmpty()) {
            Collections.shuffle(lpasok, new Random(System.nanoTime()));
            return lpasok.get(0);
        }
        else
            return reste.get(0);

    }

    /* Methode principale :  enlever les noeuds de plus haut degré au début en random sur les plus hauts noeuds. . */
    private ArrayList<Point> methode6(ArrayList<Point> points, int edgeThreshold){
        ArrayList<Point> ensDom =  new ArrayList<Point>();
        ArrayList<Point> couvertureEnsDom =  new ArrayList<Point>();
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        ArrayList<Point> voisins = new ArrayList<>();
        ArrayList<Point> result = (ArrayList<Point>) points.clone();

        ArrayList<Point> liste = new ArrayList<>();

        Point p, q, qtmp;

        boolean continuer =true;
        for (int t=0; t<100; t++) {
            ensDom=new ArrayList<Point>();
            couvertureEnsDom=new ArrayList<Point>();
            reste = (ArrayList<Point>) points.clone();

            continuer=true;
            while(continuer) {
                continuer=false;
                for (int i = reste.size() - 1; i >= 0; i--) {
                    if (degre(p = reste.get(i), reste, edgeThreshold) == 0) {
                        ensDom.add(p);
                        couvertureEnsDom.add(p);
                        reste.remove(p);
                        continuer=true;
                    }
                    if (degre(p = reste.get(i), reste, edgeThreshold) == 1) {
                        q = voisins(p, reste, edgeThreshold).get(0);
                        ensDom.add(q);
                        couvertureEnsDom.add(p);
                        couvertureEnsDom.add(q);
                        reste.remove(p);
                        reste.remove(q);
                        liste.addAll(voisins(q, reste, edgeThreshold));
                        continuer=true;
                    }
                }
            }

            int i = 0;
            while (!estEnsembleDominant(ensDom, points, edgeThreshold)) {
                i++;

                Collections.shuffle(reste, new Random(System.nanoTime()));
                p = chercheMeilleurVoisin(ensDom, reste, edgeThreshold);

                // !! vérifier si p est pas de degré 0 !!! pour les autres méthodes !!! :
                if(degre(p, reste, edgeThreshold)==0) {
                    ensDom.add(p);
                    couvertureEnsDom.add(p);
                    reste.remove(p);
                    continue;
                }

                voisins = voisins(p, reste, edgeThreshold);

                ensDom.add(p);
                couvertureEnsDom.addAll(voisins);
                couvertureEnsDom.add(p);
                reste.removeAll(voisins);
                reste.remove(p);
            }

            if (result.size() > ensDom.size())
                result = ensDom;
        }

        return result;
    }

    public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
        ArrayList<Point> result = (ArrayList<Point>)points.clone();

        ArrayList<Point> tmp;
        tmp = methode1(points, edgeThreshold);
        System.out.println("méthode 1 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;

        tmp = methode2(points, edgeThreshold);
        System.out.println("méthode 2 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;

        tmp = methode3(points, edgeThreshold);
        System.out.println("méthode 3 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;

        /*tmp = methode4(points, edgeThreshold);
        System.out.println("méthode 4 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;*/

        tmp = methode5(points, edgeThreshold);
        System.out.println("méthode 5: "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;

        tmp = methode6(points, edgeThreshold);
        System.out.println("méthode 6 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;

        tmp = methode7(points, edgeThreshold);
        System.out.println("méthode 7 : "+tmp.size());
        if(tmp.size()<result.size()) result=tmp;


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


        int score = score("graphe" + numeroDeGraphe);
        if (score == -1 || score > result.size())
            writePoints(result, "graphe" + numeroDeGraphe, "methode7");
        else {
            result = readPoints("graphe" + numeroDeGraphe);
        }

        numeroDeGraphe++;
        return result;
    }

    public int score(String filename){
        try {
            Path file = Paths.get(filename);

            if(!Files.exists(file))
                return -1;
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
