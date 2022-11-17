package fr.firedream89.trieurseries;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Outils {

    /**
     * retourne la liste des fichiers et dossiers contenu dans le répertoire
     * @param path lien vers le répertoire
     * @return liste de fichiers/dossiers
     */
    public static ArrayList<File> getItemsInDirectory(String path) {
        return new ArrayList<File>(Stream.of(new File(path).listFiles()).toList());
    }

    /**
     * retourne la liste des fichiers et dossiers contenu dans le répertoire
     * @param path lien vers le répertoire
     * @param filter liste de String déterminant le filtrage des fichiers (les dossiers sont exclus du filtrage)
     * @return liste de fichiers/dossiers
     */
    public static ArrayList<File> getItemsInDirectory(String path, String[] filter) {
        ArrayList<File> list;
        try {
            list = new ArrayList<File>(Stream.of(new File(path).listFiles()).toList());

            for(int i = 0; i < list.size(); i++) {
                boolean testFilter = false;
                File f = list.get(i);
                //System.out.print(f.getName());
                for(String flt : filter) {
                    if (f.isFile() && f.getName().contains(flt)) {
                        testFilter = true;
                    }
                }
                if(f.isFile() && !testFilter) {
                    //System.out.print(" Removed");
                    list.remove(f);
                    i--;
                }
                //System.out.println();
            }
        } catch (NullPointerException e) {
            list = new ArrayList<File>();
        }
        return list;
    }

    public static ArrayList<File> getFilesInDirectory(String path, String[] filter, boolean recursive) {
        ArrayList<File> list;
        ArrayList<File> result = new ArrayList<>();
        list = getItemsInDirectory(path, filter);
        for(File f : list) {
            if (f.isFile())
                result.add(f);
            else if (recursive)
                result.addAll(getFilesInDirectory(f.getPath(), filter, recursive));
        }
        return result;
    }

    public static String[] extraireNumEpEtSaison(String nom) {
        nom = nom.toUpperCase().replace(".", " ");

        Pattern patternx = Pattern.compile("[0-9]{1,2}x[0-9]{1,3}");
        Matcher mX = patternx.matcher(nom);
        Pattern patternse = Pattern.compile("S[0-9]{1,2}E[0-9]{1,3}");
        Matcher mSe = patternse.matcher(nom);

        String[] result = new String[3];//nom, saison, episode

        try {
            if (mSe.find()) {
                String subStr = mSe.group();
                result[2] = subStr.split("E")[1];
                result[1] = subStr.split("E")[0].split("S")[1];//Saison
                result[0] = pointToSpace(nom.split(subStr)[0]);//Nom
            } else if (mX.find()) {
                String subStr = mX.group();
                result[2] = subStr.split("X")[1];
                result[1] = subStr.split("X")[0];
                result[0] = pointToSpace(nom.split(subStr)[0]);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String[] extraireNomEtNumSaison(String nom) {
        nom = nom.toUpperCase().replace("."," ");

        Pattern patterns = Pattern.compile("Saison [0-9]");
        Matcher mS = patterns.matcher(nom);
        Pattern patterns2 = Pattern.compile("S[0-9]{1,2}");
        Matcher mS2 = patterns2.matcher(nom);
        Pattern patternx = Pattern.compile("[0-9]{1,2}X[0-9]{1,3}");
        Matcher mX = patternx.matcher(nom);

        String[] result = new String[2];

        try {
            if (mS.find()) {
                String subStr = mS.group();
                result[1] = subStr.split(" ")[subStr.split(" ").length - 1];//Saison
                result[0] = pointToSpace(nom.split(subStr)[0]);//Nom
            } else if (mX.find()) {
                String subStr = mX.group();
                result[1] = subStr.split("X")[0];
                result[0] = pointToSpace(nom.split(subStr)[0]);
            } else if (mS2.find()) {
                String subStr = mS2.group();
                result[1] = subStr.split("S")[1];
                result[0] = pointToSpace(nom.split(subStr)[0]);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if(result[0] != null) {
            result[0] = capitalizeAllWord(removeBadChar(result[0]));
            result[1] = removeZero(result[1]);
        }
        return result;
    }

    public static String pointToSpace(String str) {
        if(str != null) {
            StringBuilder strb = new StringBuilder(str.replace(".", " "));
            if (!strb.isEmpty() && strb.charAt(str.length() - 1) == ' ')
                str = strb.substring(0, strb.length() - 1);
        }
        return str;
    }

    public static String capitalizeAllWord(String str) {
        StringBuffer finalStr = new StringBuffer();
        if(str != null && !str.isEmpty()) {
            String[] strSplitted = str.split(" ");
            for(String s : strSplitted) {
                if(!finalStr.isEmpty())
                    finalStr.append(" ");
                finalStr.append(s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase());
            }
        }
        return finalStr.toString();
    }

    public static String removeZero(String str) {
        return String.valueOf(Integer.parseInt(str));
    }

    public static String removeBadChar(String str) {
        if(str != null && !str.isEmpty()) {
            if (str.contains("-")) {
                str = str.split("-")[0];
            }
            if (str.charAt(str.length() - 1) == ' ') {
                str = str.substring(0, str.length()-1);
            }
        }
        return str;
    }
}
