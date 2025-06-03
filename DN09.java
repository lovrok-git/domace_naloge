import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.awt.Color;


public class DN09 {
    public static Postaja[] postaje;
    public static Linija[] linije;
    public static Avtobus[] avtobusi;

    public static int stevecLinije = 0;
    public static int stevecAvtobusov = 0;
    public static int stevecPostaj = 0;

    public static void main(String[] args) throws FileNotFoundException {

        String imeDetoteke = args[0];
        String nacin = args[1];
        Scanner sc = new Scanner(new File(imeDetoteke));
        String[] stevila = sc.nextLine().split(",");
        postaje = new Postaja[Integer.parseInt(stevila[0])];
        linije = new Linija[Integer.parseInt(stevila[1])];
        avtobusi = new Avtobus[Integer.parseInt(stevila[2])];

        boolean vrstaBranja = false;
        int stevc = 0;
        while (sc.hasNextLine()) {
            String vrstica = sc.nextLine().trim();
            if (vrstica.isEmpty()) {
                stevc++;
                if(stevc > 1){
                    vrstaBranja = true;
                }
                continue;
            }
            if(!vrstaBranja){
                String[] podatkiPostaj = vrstica.split(",");
                if (podatkiPostaj.length < 7) continue;

                Postaja p = new Postaja(
                        Integer.parseInt(podatkiPostaj[0]),
                        podatkiPostaj[1],
                        Integer.parseInt(podatkiPostaj[2]),
                        Integer.parseInt(podatkiPostaj[3]),
                        Integer.parseInt(podatkiPostaj[6])
                );
                postaje[stevecPostaj++] = p;

                String[] stevilaLinij = podatkiPostaj[4].split(";");
                for (String linijaIdStr : stevilaLinij) {
                    if (linijaIdStr.isEmpty()) continue;
                    int idLinije = Integer.parseInt(linijaIdStr);

                    boolean obstaja = false;
                    for (int j = 0; j < stevecLinije; j++) {
                        if (linije[j] != null && linije[j].getID() == idLinije) {
                            obstaja = true;
                            break;
                        }
                    }

                    if (!obstaja && stevecLinije < linije.length) {
                        linije[stevecLinije++] = new Linija(idLinije);
                    }
                }

                String[] avtobusiStr = podatkiPostaj[5].split(";");
                for (String avtobusStr : avtobusiStr) {
                    if (avtobusStr.isEmpty() || !avtobusStr.contains("(") || !avtobusStr.contains(")")) continue;

                    int oklepaj = avtobusStr.indexOf('(');
                    int zaklepaj = avtobusStr.indexOf(')');
                    int idAvtobusa = Integer.parseInt(avtobusStr.substring(0, oklepaj));
                    int potniki = Integer.parseInt(avtobusStr.substring(oklepaj + 1, zaklepaj));

                    boolean obstaja = false;
                    for (int j = 0; j < stevecAvtobusov; j++) {
                        if (avtobusi[j] != null && avtobusi[j].getID() == idAvtobusa) {
                            obstaja = true;
                            break;
                        }
                    }

                    if (!obstaja && stevecAvtobusov < avtobusi.length) {
                        avtobusi[stevecAvtobusov] = new Avtobus(idAvtobusa, potniki);
                        avtobusi[stevecAvtobusov].setTrenutnaPostaja(p);
                        stevecAvtobusov++;
                    }
                }
            } else if (vrstaBranja) {
                if (vrstica.isEmpty()) continue;
                String[] podatkiLinij = vrstica.split(",");
                int idLinije = Integer.parseInt(podatkiLinij[0]);
                String barva = podatkiLinij[1];
                String[] idAvtobusov = podatkiLinij[2].split(";");
                String[] idPostaj = podatkiLinij[3].split("\\|");

                for (int i = 0; i < linije.length; i++) {
                    if (linije[i] != null && linije[i].getID() == idLinije) {
                        linije[i].setBarva(barva);

                        for (String identitetaAvtobusov : idAvtobusov) {
                            if (identitetaAvtobusov.isEmpty()) continue;
                            int idAvtobusa = Integer.parseInt(identitetaAvtobusov);
                            for (Avtobus a : avtobusi) {
                                if (a != null && a.getID() == idAvtobusa) {
                                    linije[i].dodajAvtobus(a);
                                    break;
                                }
                            }
                        }
                        for (String identitaPostaj : idPostaj) {
                            if (identitaPostaj.isEmpty()) continue;
                            int idPostaje = Integer.parseInt(identitaPostaj);
                            for (Postaja p : postaje) {
                                if (p != null && p.getID() == idPostaje) {
                                    linije[i].dodajPostajo(p);
                                    break;
                                }
                            }
                        }

                    }
                }
            }

        }

        sc.close();
        if (nacin.equals("izpisi")) {
            izpisi();
        }
        if (nacin.equals("najboljObremenjena")){
            int kapaciteta = Integer.parseInt(args[2]);
            izpisNajboljObremenjenePostaje(kapaciteta);
        }
        if (nacin.equals("premik")){
            int n = Integer.parseInt(args[2]);
            izpisiPoNPremikih(n);
        }
        if (nacin.equals("ekspres")){
            int n = Integer.parseInt(args[2]);
            dodajEkspresneAvtobuse();
        }

        if(nacin.equals("najblizja")){
            String imePostaje = args[2];
            izpisNajblizjePostaje(imePostaje);
        }
        if(nacin.equals("izrisi")){
            String ime = args[2];
            izrisi(ime);
        }
    }

    public static boolean jeInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void izpisNajblizjePostaje(String imePostaje) {
        Postaja izbrana = null;

        if (jeInt(imePostaje)) {
            int id = Integer.parseInt(imePostaje);
            for (Postaja p : postaje) {
                if (p != null && p.getID() == id) {
                    izbrana = p;
                    break;
                }
            }
            if (izbrana == null) {
                System.out.println("Postaja z ID " + id + " ne obstaja.");
                return;
            }
        } else {
            for (Postaja p : postaje) {
                if (p != null && p.getIme().equalsIgnoreCase(imePostaje)) {
                    izbrana = p;
                    break;
                }
            }
            if (izbrana == null) {
                System.out.println("Postaja z imenom " + imePostaje + " ne obstaja.");
                return;
            }
        }

        Postaja najblizja = null;
        double minRazdalja = Double.MAX_VALUE;

        for (Postaja p : postaje) {
            if (p == null || p.getID() == izbrana.getID()) continue;
            double dx = izbrana.getX() - p.getX();
            double dy = izbrana.getY() - p.getY();
            double razdalja = Math.sqrt(dx * dx + dy * dy);

            if (razdalja == minRazdalja) {
                if (p.getIme().compareTo(najblizja.getIme()) < 0) {
                    najblizja = p;
                }
            } else if (razdalja < minRazdalja) {
                minRazdalja = razdalja;
                najblizja = p;
            }

        }

        System.out.printf("Najblizja postaja %s (ID %d):\n", izbrana.getIme(), izbrana.getID());
        System.out.printf("%d. %s - razdalja: %.2f\n", najblizja.getID(), najblizja.getIme(), minRazdalja);
    }




    public static void dodajEkspresneAvtobuse(){
        int stevec = 965;
        int stPotnikov = 2;
        for(Linija l : linije){
            if (l == null){
                continue;
            }
            int zaporedje = 0;
            Postaja[] zadnjaInPrva = new Postaja[2];
            for(int i = 0; i < l.getStPostaj(); i++){
                    if(i == 0 || (i == (l.getStPostaj() - 1))){
                        zadnjaInPrva[zaporedje] = l.getPostaje()[i];

                }
            }
            EkspresniAvtobus e = new EkspresniAvtobus(stevec, stPotnikov, zadnjaInPrva);
            stevec++;
            l.dodajAvtobus(e);
        }
    }
    public void naslednjeStanjeEkspres(){
        for(Linija l : linije){
            if (l == null){
                continue;
            }
            for(Avtobus a : l.getAvtobusi()){

            }

        }
    }
    public void izpisiPoNPremikihEkspres(int n){
        int stevec = 965;
        int stPotnikov = 2;
        for(Linija l : linije){
            if (l == null){
                continue;
            }
            int zaporedje = 0;
            Postaja[] zadnjaInPrva = new Postaja[2];
            for(int i = 0; i < l.getStPostaj(); i++){
                if(i == 0 || (i == (l.getStPostaj() - 1))){
                    zadnjaInPrva[zaporedje] = l.getPostaje()[i];

                }
            }
            EkspresniAvtobus e = new EkspresniAvtobus(stevec, stPotnikov, zadnjaInPrva);
            stevec++;
            l.dodajAvtobus(e);
        }
    }

    public static void izpisiPoNPremikih(int n) {
        System.out.println("Zacetno stanje");
        for (int i = 0; i < linije.length; i++) {
            if (linije[i] != null) {
                System.out.println(linije[i]);
            }
        }

        for(Linija l : linije){
            if(l == null){
                continue;
            }
            Postaja[] postajeLinije = l.getPostaje();
            int stPostaj = l.getStAvtobusov();
            int stevecTrenutnaPostaja = 1;
            for (Avtobus avtobus : l.getAvtobusi()) {
                stevecTrenutnaPostaja++;
                if (avtobus == null) {
                    continue;
                }
                int mesto = 0;
                Postaja[] postajaLinijeBrezNull = new Postaja[l.getStPostaj()];
                int stevecPostajeLinije = 0;
                for(int i = 0; i < postajeLinije.length; i++){
                    if(postajeLinije[i] != null){
                        postajaLinijeBrezNull[stevecPostajeLinije] = postajeLinije[i];
                        stevecPostajeLinije++;
                    }
                }
                for(int i = 0; i < postajaLinijeBrezNull.length; i++) {
                    if(postajaLinijeBrezNull[i] == null){
                        continue;
                    }
                    if(l.getStPostaj() == 1){
                        break;
                    }
                    if (avtobus.getTrenutnaPostaja() != null &&
                            postajaLinijeBrezNull[i] != null &&
                            avtobus.getTrenutnaPostaja().getID() == postajaLinijeBrezNull[i].getID()) {
                        boolean smer = false;
                        int indeks = i;
                        int stevec = 0;
                        while(stevec < n) {
                            stevec++;
                            if (!smer) {
                                mesto = indeks + 1;
                                if (mesto > (l.getStPostaj() - 1)) {
                                    mesto = l.getStPostaj() - 2;
                                    smer = true;
                                    indeks--;
                                }else{
                                    indeks++;
                                }
                                avtobus.setTrenutnaPostaja(null);
                                if (mesto >= 0 && mesto < postajaLinijeBrezNull.length) {
                                    avtobus.setTrenutnaPostaja(postajaLinijeBrezNull[mesto]);
                                }

                            } else {
                                mesto = indeks - 1;
                                if (mesto < 0) {
                                    mesto = 1;
                                    smer = false;
                                    indeks++;
                                }else {
                                    indeks--;
                                }
                                avtobus.setTrenutnaPostaja(null);
                                if (mesto >= 0 && mesto < postajaLinijeBrezNull.length) {
                                    avtobus.setTrenutnaPostaja(postajaLinijeBrezNull[mesto]);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        System.out.println();
        System.out.printf("Stanje po %d premikih\n", n);
        for (int i = 0; i < linije.length; i++) {
            if (linije[i] != null) {
                System.out.println(linije[i]);
            }
        }
    }
    public static void izrisi(String ime_slike){
        StdDraw.setCanvasSize(400,400);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenColor(StdDraw.WHITE);
        for(Linija l : linije){
            if (l == null){
                continue;
            }
            Color barva = Color.decode(l.getBarva());
            StdDraw.setPenRadius(0.015);
            StdDraw.setPenColor(barva);
            Postaja zadnjaPostaja = null;
            for(Postaja p : l.getPostaje()){
                if(p == null){
                    continue;
                }
                if(zadnjaPostaja != null){
                    StdDraw.line(zadnjaPostaja.getX(),zadnjaPostaja.getY(),p.getX(),p.getY());
                }
                zadnjaPostaja = p;
            }
            StdDraw.setPenColor(StdDraw.BLACK);
            for(Postaja p : postaje){
                if(p == null){
                    continue;
                }
                StdDraw.filledSquare(p.getX(), p.getY(), 1.5);
            }
            StdDraw.setPenColor(StdDraw.DARK_GRAY);
            for(Postaja p : postaje){
                if(p == null){
                    continue;
                }
                StdDraw.text(p.getX(), p.getY() - 3, p.getIme());
            }
            StdDraw.show();
            StdDraw.save(ime_slike);
        }
    }
    public static void naslednjeStanje(){
        for (int i = 0; i < linije.length; i++) {
            if (linije[i] != null) {
                System.out.println(linije[i]);
            }
        }

        for(Linija l : linije){
            if(l == null){
                continue;
            }
            Postaja[] postajeLinije = l.getPostaje();
            int stPostaj = l.getStAvtobusov();
            int stevecTrenutnaPostaja = 1;
            for (Avtobus avtobus : l.getAvtobusi()) {
                stevecTrenutnaPostaja++;
                if (avtobus == null) {
                    continue;
                }
                int mesto = 0;
                for(int i = 0; i < postajeLinije.length; i++) {
                    if(postajeLinije[i] == null){
                        continue;
                    }
                    if (avtobus.getTrenutnaPostaja() != null && avtobus.getTrenutnaPostaja().equals(postajeLinije[i])) {
                        mesto = i + 1;
                        avtobus.setTrenutnaPostaja(null);
                        avtobus.setTrenutnaPostaja(postajeLinije[mesto]);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < linije.length; i++) {
            if (linije[i] != null) {
                System.out.println(linije[i]);
            }
        }
    }


    public static void izpisNajboljObremenjenePostaje(int kapaciteta){
        Postaja najObremenjena = null;
        double najRazmerje = 1000;
        int najCakajoci = 0;
        int najPraznihMest = 0;
        int stPraznihMest = 0;
        int cakajociPotniki = 0;
        for(Postaja postaja : postaje){
            if(postaja == null){
                continue;
            }
            int prostaMesta = 0;
            cakajociPotniki = postaja.getCakajoci();
            if (cakajociPotniki == 0){
                cakajociPotniki = 1;
            }
            for(Linija l : linije) {
                if (l == null) {
                    continue;
                }
                boolean stanje = false;
                for (Postaja p : l.getPostaje()) {
                    if (p == null) break;
                    if (p != null && postaja.getID() == p.getID()){
                        stanje = true;
                    }

                }
                if(stanje){
                    for(Avtobus a : l.getAvtobusi()){
                        if (a == null){
                            continue;
                        }
                        int mesta = kapaciteta - a.getSteviloPotnikov();
                        if (mesta > 0) {
                            prostaMesta += mesta;
                        }

                    }
                }
            }
            double razmerje;
            razmerje = (double)prostaMesta / cakajociPotniki;
            if(razmerje < najRazmerje || (razmerje == najRazmerje && postaja.getID() < najObremenjena.getID())){
                najRazmerje = razmerje;
                najObremenjena = postaja;
                najPraznihMest = prostaMesta;
                najCakajoci = postaja.getCakajoci();

            }
        }
        System.out.printf("Najbolj obremenjena postaja: %d %s\n", najObremenjena.getID(), najObremenjena.getIme());
        System.out.printf(Locale.US,"Cakajoci: %d, Stevilo prostih mest: %d, Razmerje: %.2f",najCakajoci,Math.abs(najPraznihMest),najRazmerje);

    }


    public static void izpisi() {
        for (int i = 0; i < linije.length; i++) {
            if (linije[i] != null) {
                System.out.println(linije[i]);
            }
        }
    }

}

class Postaja {
    private int ID;
    private String ime;
    private int x;
    private int y;
    private int cakajoci;

    public int getCakajoci() {
        return cakajoci;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public String getIme() {
        return ime;
    }

    public int getID() {
        return ID;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public Postaja(int ID, String ime, int x, int y, int cakajoci) {
        this.ID = ID;
        this.ime = ime;
        this.x = x;
        this.y = y;
        this.cakajoci = cakajoci;
    }

    public String toString() {
        return String.format("%s %s [%d,%d] čakajoči: %d", getID(), getIme(), getX(), getY(), getCakajoci());
    }
}

class Avtobus {
    private int ID;
    private int steviloPotnikov;
    public Postaja trenutnaPostaja;

    public int getID() {
        return ID;
    }

    public int getSteviloPotnikov() {
        return steviloPotnikov;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTrenutnaPostaja(Postaja trenutnaPostaja) {
        this.trenutnaPostaja = trenutnaPostaja;
    }

    public Postaja getTrenutnaPostaja() {
        return trenutnaPostaja;
    }

    public Avtobus(int ID, int steviloPotnikov) {
        this.ID = ID;
        this.steviloPotnikov = steviloPotnikov;
    }

    public String toString() {
        return String.format("%d (%d) - %s", getID(), getSteviloPotnikov(), getTrenutnaPostaja().getIme());
    }

}

class Linija {
    private int ID;
    private String barva;
    private Postaja[] postaje;
    private Avtobus[] avtobusi;
    private int stAvtobusov;
    private int stPostaj;

    public int getID() {
        return ID;
    }

    public Avtobus[] getAvtobusi() {
        return avtobusi;
    }

    public String getBarva() {
        return barva;
    }

    public Postaja[] getPostaje() {
        return postaje;
    }

    public void setBarva(String barva) {
        this.barva = barva;
    }

    public Linija(int ID) {
        this.ID = ID;
        this.barva = null;
        this.postaje = new Postaja[10];
        this.avtobusi = new Avtobus[5];
        this.stPostaj = 0;
        this.stAvtobusov = 0;
    }

    public void setPostaje(Postaja[] postaje) {
        this.postaje = postaje;
    }

    public void setAvtobusi(Avtobus[] avtobusi) {
        this.avtobusi = avtobusi;
    }

    boolean dodajPostajo(Postaja postaja) {
        for (int i = 0; i < postaje.length; i++) {
            if (postaje[i] == null) {
                postaje[i] = postaja;
                stPostaj++;
                return true;
            }
        }
        return false;
    }



    boolean dodajAvtobus(Avtobus avtobus) {
        for (int i = 0; i < avtobusi.length; i++) {
            if (avtobusi[i] == null) {
                avtobusi[i] = avtobus;
                stAvtobusov++;
                return true;
            }
        }
        return false;
    }


    public String toString() {
        String vrstniRedPostaj = "";
        boolean first = true;
        for (int i = 0; i < postaje.length; i++) {
            Postaja postaja = postaje[i];
            if (postaja == null) {
                break;
            }

            boolean imaBus = false;
            for (Avtobus a : avtobusi) {
                if (a != null && a.getTrenutnaPostaja() != null &&
                        a.getTrenutnaPostaja().getID() == postaja.getID()) {
                    imaBus = true;
                    break;
                }
            }

            vrstniRedPostaj += postaja.getIme();
            if (imaBus) {
                vrstniRedPostaj += " (bus)";
            }
            if (i + 1 < postaje.length && postaje[i + 1] != null) {
                vrstniRedPostaj += " -> ";
            }
        }
        return String.format("Linija %d - %s", getID(), vrstniRedPostaj);
    }

    public int getStAvtobusov() {
        return stAvtobusov;
    }

    public int getStPostaj() {
        return stPostaj;
    }
}
class EkspresniAvtobus extends Avtobus{
    public Postaja[] preskociPostaje;

    public Postaja[] getPreskociPostaje() {
        return preskociPostaje;
    }

    public void setPreskociPostaje(Postaja[] preskociPostaje) {
        this.preskociPostaje = preskociPostaje;
    }

    public EkspresniAvtobus(int ID, int steviloPotnikov, Postaja[] preskociPostaje) {
        super(ID, steviloPotnikov);
        this.preskociPostaje = preskociPostaje;
    }
    public boolean preskoci(Postaja p) {
        for (Postaja ps : preskociPostaje) {
            if (ps != null && ps.getID() == p.getID()) {
                return true;
            }
        }
        return false;
    }
}
