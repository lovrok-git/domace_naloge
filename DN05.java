import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DN05 {
    public static int sirinaIgralnePovrsine;
    public static int visinaIgralnePovrsine;

    public static void main(String[] args) throws FileNotFoundException {
        String ukaz = args[0];
        String imeDatoteke = args[1];

        if (ukaz.equals("postavitev")) {
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            izpisiPostavitev(postavitev);

        } else if (ukaz.equals("povrsina")) {
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            int[][] povrsina = izdelajIgralnoPovrsino(postavitev);
            izrisiIgralnoPovrsino(povrsina);
        }
        else if (ukaz.equals("povecanje")) {
            String noveDimenzije = args[2];
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            postavitev = povecajIgralnoPovrsino(postavitev, noveDimenzije);
            int[][] povrsina = izdelajIgralnoPovrsino(postavitev);
            izrisiIgralnoPovrsino(povrsina);
        }else if (ukaz.equals("zmanjsanje")) {
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            int[][] igralnaPovrsina = izdelajIgralnoPovrsino(postavitev);
            int[][] novaPostavitev = minimizirajIgralnoPovrsino(postavitev, igralnaPovrsina);
            int[][] povrsina = izdelajIgralnoPovrsino(novaPostavitev);
            izrisiIgralnoPovrsino(povrsina);
        }else if (ukaz.equals("zasuk")) {
            String smer = args[2];
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            int[][] igralnaPovrsina = izdelajIgralnoPovrsino(postavitev);
            int[][] novaPostavitev = zasukajLadje(postavitev, smer);
            int[][] povrsina = izdelajIgralnoPovrsino(novaPostavitev);
            izrisiIgralnoPovrsino(povrsina);
        } else if (ukaz.equals("simulacija")) {
            String datotekaPotez = args[2];
            int[][] postavitev = preberiZacetnoPostavitev(imeDatoteke);
            int[][] igralnaPovrsina = izdelajIgralnoPovrsino(postavitev);
            int[][] novaPovrsina = simulirajIgro(igralnaPovrsina, datotekaPotez);
            izrisiIgralnoPovrsino(novaPovrsina);
        }


    }


    public static int[][] preberiZacetnoPostavitev(String imeDatoteke) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(imeDatoteke));
        } catch (FileNotFoundException e) {
            System.out.println("Napaka: datoteka ne obstaja.");
            System.exit(0);
        }
        if (!sc.hasNextLine()) {
            System.out.println("Napaka: Manjka podatek o dimenzijah igralne povrsine.");
            System.exit(0);
        }
        String vrstica = sc.nextLine();
        String[] deli = vrstica.split("x");
        if (deli.length != 2) {
            System.out.println("Napaka: Nepravilen podatek o dimenzijah igralne povrsine.");
            System.exit(0);
        }
        int sirina = 0, visina = 0;
        try {
            sirina = Integer.parseInt(deli[0].trim());
            visina = Integer.parseInt(deli[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Napaka: Nepravilen podatek o dimenzijah igralne povrsine.");
            System.exit(0);
        }
        if (sirina <= 0 || visina <= 0) {
            System.out.println("Napaka: Dimenzija mora biti pozitivna.");
            System.exit(0);
        }
        sirinaIgralnePovrsine = sirina;
        visinaIgralnePovrsine = visina;

        if (!sc.hasNextLine()) {
            System.out.println("Napaka: Manjka podatek o stevilu ladij.");
            System.exit(0);
        }
        String shipsLine = sc.nextLine().trim();
        int steviloLadij = 0;
        try {
            steviloLadij = Integer.parseInt(shipsLine);
        } catch (NumberFormatException e) {
            System.out.println("Napaka: Manjka podatek o stevilu ladij.");
            System.exit(0);
        }
        if (steviloLadij < 0) {
            System.out.println("Napaka: Stevilo ladij ne sme biti negativno.");
            System.exit(0);
        }

        int[][] kordinateLadji = new int[steviloLadij][5];
        int stevecLadij = 0;


        while (sc.hasNextLine() && stevecLadij < steviloLadij) {
            vrstica = sc.nextLine();
            deli = vrstica.split(" ");
            if (deli.length != 5) {
                System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                System.exit(0);
            }
            int igralec;
            int x;
            int y;
            int dolzinaLadje;
            int smer = -1;
            try {
                igralec = Integer.parseInt(deli[0].trim());
                x = Integer.parseInt(deli[1].trim());
                y = Integer.parseInt(deli[2].trim());
                dolzinaLadje = Integer.parseInt(deli[3].trim());

                if (igralec != 0 && igralec != 1) {
                    System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                    System.exit(0);
                }
            } catch (NumberFormatException e) {
                System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                System.exit(0);
                return null;
            }

            String smerBeseda = deli[4].trim();
            switch (smerBeseda) {
                case "S":
                    smer = 0;
                    break;
                case "J":
                    smer = 1;
                    break;
                case "V":
                    smer = 2;
                    break;
                case "Z":
                    smer = 3;
                    break;
                default:
                    System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                    System.exit(0);
            }
            kordinateLadji[stevecLadij][0] = igralec;
            kordinateLadji[stevecLadij][1] = x;
            kordinateLadji[stevecLadij][2] = y;
            kordinateLadji[stevecLadij][3] = dolzinaLadje;
            kordinateLadji[stevecLadij][4] = smer;
            stevecLadij++;
        }
        if (stevecLadij < steviloLadij) {
            System.out.println("Napaka: Podatek o stevilu ladij se ne ujema s stevilom vnosov.");
            System.exit(0);
        }
        sc.close();
        return kordinateLadji;
    }
    public static void izpisiPostavitev(int[][] postavitev){
        for (int i = 0; i < postavitev.length; i++){
            System.out.printf("Igralec: %d  Dolzina: %d  Smer: %d  Koordinate premca: (%d,%d)%n", postavitev[i][0], postavitev[i][3], postavitev[i][4], postavitev[i][1], postavitev[i][2]);
        }
    }
    public static int[][] izdelajIgralnoPovrsino(int[][] postavitev) {
        int[][] koordinatnoPolje = new int[visinaIgralnePovrsine][sirinaIgralnePovrsine * 2 + 1];
        for (int i = 0; i < postavitev.length; i++) {
            int igralec = postavitev[i][0];
            int x_os = postavitev[i][1];
            int y_os = postavitev[i][2];
            int dolzina = postavitev[i][3];
            int smer = postavitev[i][4];

            if (igralec == 1) {
                x_os += sirinaIgralnePovrsine;
            }
            boolean stanje = false;
            for (int k = 0; k < dolzina; k++){
                if (smer == 1) {
                    if (y_os - k < 0 || y_os - k >= visinaIgralnePovrsine || koordinatnoPolje[y_os - k][x_os] != 0){
                        stanje = true;
                    }
                } else if (smer == 0) {
                    if (y_os + k < 0 || y_os + k >= visinaIgralnePovrsine || koordinatnoPolje[y_os + k][x_os] != 0){
                        stanje = true;
                    }
                } else if (smer == 3) {
                    if (x_os + k < 0 || x_os + k >= sirinaIgralnePovrsine * 2 + 1 || koordinatnoPolje[y_os][x_os + k] != 0){
                        stanje = true;
                    }
                    if (igralec == 0){
                        if (x_os + k > sirinaIgralnePovrsine){
                            stanje = true;
                        }
                    }
                } else if (smer == 2) {
                    if (x_os - k < 0 || x_os - k >= sirinaIgralnePovrsine * 2 + 1 || koordinatnoPolje[y_os][x_os - k] != 0){
                        stanje = true;
                    }
                    if (igralec == 1){
                        if (x_os - k < 0 + sirinaIgralnePovrsine){
                            stanje = true;
                        }
                    }
                }
            }
            if (stanje == false) {
                koordinatnoPolje[y_os][x_os] = i * 10 + 1;
                for (int j = 1; j < dolzina; j++) {
                    if (smer == 1) {
                        if (y_os - j < 0 || y_os - j >= visinaIgralnePovrsine) {
                            break;
                        }
                        koordinatnoPolje[y_os - j][x_os] = i * 10 + 2;
                    } else if (smer == 0) {
                        if (y_os + j < 0 || y_os + j >= visinaIgralnePovrsine) {
                            break;
                        }
                        koordinatnoPolje[y_os + j][x_os] = i * 10 + 2;
                    } else if (smer == 3) {
                        if (x_os + j < 0 || x_os + j >= sirinaIgralnePovrsine * 2 + 1) {
                            break;
                        }
                        koordinatnoPolje[y_os][x_os + j] = i * 10 + 2;
                    } else if (smer == 2) {
                        if (x_os - j < 0 || x_os - j >= sirinaIgralnePovrsine * 2 + 1) {
                            break;
                        }
                        koordinatnoPolje[y_os][x_os - j] = i * 10 + 2;
                    }
                }
            }

            }
        return koordinatnoPolje;
    }
    public static void izrisiIgralnoPovrsino(int[][] igralnaPovrsina){
        for (int k = 0; k < 2*sirinaIgralnePovrsine + 3; k++){
            System.out.print("# ");
        }
        System.out.println();
        for (int i = 0; i < igralnaPovrsina.length; i++){
            System.out.print("# ");
            for (int j = 0; j < sirinaIgralnePovrsine * 2; j++){
                if (igralnaPovrsina[i][j] == 0){
                    System.out.print("  ");
                } else if (igralnaPovrsina[i][j] % 10 == 1) {
                    System.out.print("p ");
                } else if ((igralnaPovrsina[i][j] - 3) % 10 == 0) {
                    System.out.print("X ");
                } else if ((igralnaPovrsina[i][j] - 4) % 10 == 0) {
                    System.out.print("x ");
                }  else if ((igralnaPovrsina[i][j] - 5) % 10 == 0) {
                    System.out.print("@ ");
                }   else if ((igralnaPovrsina[i][j] - 6) % 10 == 0) {
                    System.out.print("o ");
                } else {
                    System.out.print("t ");
                }
                if (j == sirinaIgralnePovrsine - 1){
                    System.out.print("# ");
                }
            }
            System.out.print("#");
            System.out.println();
        }
        for (int k = 0; k < 2*sirinaIgralnePovrsine + 3; k++){
            System.out.print("# ");
        }
    }
    public static int[][] povecajIgralnoPovrsino(int[][] postavitev, String noveDimenzije) {
        String[] parts = noveDimenzije.split("x");
        int novaSirina = Integer.parseInt(parts[0]);
        int novaVisina = Integer.parseInt(parts[1]);
        if (novaSirina <= sirinaIgralnePovrsine && novaVisina <= visinaIgralnePovrsine) {
            return postavitev;
        }
        int zamikX = Math.max(0, (novaSirina - sirinaIgralnePovrsine + 1) / 2);
        int zamikY = Math.max(0, (novaVisina - visinaIgralnePovrsine + 1) / 2);
        for (int i = 0; i < postavitev.length; i++) {
            postavitev[i][1] += zamikX;
            postavitev[i][2] += zamikY;
        }
        sirinaIgralnePovrsine = Math.max(sirinaIgralnePovrsine, novaSirina);
        visinaIgralnePovrsine = Math.max(visinaIgralnePovrsine, novaVisina);

        return postavitev;
    }
    public static int[][] minimizirajIgralnoPovrsino(int[][] postavitev, int[][] igralnaPovrsina) {
        int maxValue = -1;
        int minY = igralnaPovrsina.length;
        int maxY = maxValue;
        int minX0 = igralnaPovrsina[0].length;
        int maxX0 = maxValue;
        int minX1 = igralnaPovrsina[0].length;
        int maxX1 = maxValue;
        for (int i = 0; i < igralnaPovrsina.length; i++) {
            for (int j = 0; j < igralnaPovrsina[0].length; j++) {
                if (igralnaPovrsina[i][j] != 0) {
                    if (i < minY) minY = i;
                    if (i > maxY) maxY = i;
                    if (j < sirinaIgralnePovrsine) {
                        if (j < minX0) minX0 = j;
                        if (j > maxX0) maxX0 = j;
                    } else {
                        int lokalniX = j - sirinaIgralnePovrsine;
                        if (lokalniX < minX1) minX1 = lokalniX;
                        if (lokalniX > maxX1) maxX1 = lokalniX;
                    }
                }
            }
        }
        int zamikY = minY;
        int zamikX0 = minX0;
        int zamikX1 = minX1;
        for (int i = 0; i < postavitev.length; i++) {
            if (postavitev[i][0] == 0) {
                postavitev[i][1] -= zamikX0;
            } else {
                postavitev[i][1] -= zamikX1;
            }
            postavitev[i][2] -= zamikY;
        }

        sirinaIgralnePovrsine = Math.max(maxX0 - minX0 + 1, maxX1 - minX1 + 1);
        visinaIgralnePovrsine = maxY - minY + 1;

        return postavitev;
    }

    public static int[][] zasukajLadje(int[][] postavitev, String smerVetra){
        for(int i = 0; i < postavitev.length; i++){
            for (int j = 0; j < postavitev[0].length; j++){
                if(j == 4){
                    int n = 0;
                    switch (smerVetra){
                        case "S":
                            n = 0;
                            break;
                        case "J":
                            n = 1;
                            break;
                        case "V":
                            n = 2;
                            break;
                        case "Z":
                            n = 3;
                            break;
                    }
                    postavitev[i][j] = n;
                }
            }
        }
        return postavitev;
    }
    public static int[][] simulirajIgro(int[][] igralnaPovrsina, String imeDatoteke) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(imeDatoteke));
        boolean igralec = false;
        while (sc.hasNextLine()){
            String vrstica = sc.nextLine().trim();
            if(vrstica.isEmpty()){
                continue;
            }
            String[] deli = vrstica.split(",");
            if(deli.length != 2) continue;
            int x_kordinata = Integer.parseInt(deli[0].trim()) - 1;
            int y_kordinata = Integer.parseInt(deli[1].trim()) - 1;
            int k = 0;
            if (x_kordinata < 0 || y_kordinata < 0 ||
                    x_kordinata >= igralnaPovrsina[0].length/2 || y_kordinata >= igralnaPovrsina.length) {
                continue;
            }
            if (!igralec) {
                x_kordinata += igralnaPovrsina[0].length / 2;
            }
            if (x_kordinata < 0 || x_kordinata >= igralnaPovrsina[0].length ||
                    y_kordinata < 0 || y_kordinata >= igralnaPovrsina.length) {
                continue;
            }

            if (igralnaPovrsina[y_kordinata][x_kordinata] == 0) {
                igralnaPovrsina[y_kordinata][x_kordinata] = 6;
            }
            else if ((igralnaPovrsina[y_kordinata][x_kordinata] - 1) % 10 == 0) {
                k = (igralnaPovrsina[y_kordinata][x_kordinata] - 1) / 10;
                igralnaPovrsina[y_kordinata][x_kordinata] = 10 * k + 3;
            } else if ((igralnaPovrsina[y_kordinata][x_kordinata] - 2) % 10 == 0) {
                k = (igralnaPovrsina[y_kordinata][x_kordinata] - 2) / 10;
                igralnaPovrsina[y_kordinata][x_kordinata] = 10 * k + 4;
            }

            if (k != 0) {
                boolean vsiZadeti = true;
                for (int i = 0; i < igralnaPovrsina.length; i++) {
                    for (int j = 0; j < igralnaPovrsina[0].length; j++) {
                        if (((igralnaPovrsina[i][j] - 2) / 10) == k || ((igralnaPovrsina[i][j] - 1) / 10) == k){
                            vsiZadeti = false;
                            break;
                        }
                    }
                }
                if (vsiZadeti) {
                    for (int i = 0; i < igralnaPovrsina.length; i++) {
                        for (int j = 0; j < igralnaPovrsina[0].length; j++) {
                            if (((igralnaPovrsina[i][j] - 3) / 10) == k || ((igralnaPovrsina[i][j] - 4) / 10) == k) {
                                igralnaPovrsina[i][j] = k * 10 + 5;
                            }
                        }
                    }
                }
            }
            if (igralnaPovrsina[y_kordinata][x_kordinata] == 6) {
                igralec = !igralec;
            }
        }

        sc.close();
        return igralnaPovrsina;
    }

}
