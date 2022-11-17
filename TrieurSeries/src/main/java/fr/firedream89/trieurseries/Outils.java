package fr.firedream89.trieurseries;

import java.io.File;
import java.util.ArrayList;
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
        String[] tentative = nom.replace(".", " ").split(" ");
        Pattern patternx = Pattern.compile("[0-9][0-9]x[0-9][0-9]");
        Pattern patternx2 = Pattern.compile("[0-9]x[0-9][0-9]");
        Pattern patternse = Pattern.compile("S[0-9][0-9]E[0-9][0-9]");
        Pattern patternse2 = Pattern.compile("S[0-9][0-9]E[0-9][0-9][0-9]");
        Pattern patternse3 = Pattern.compile("S[0-9]E[0-9][0-9]");

        String[] result = new String[3];
        for(String value : tentative) {
            if(patternx.matcher(value).matches() || patternx2.matcher(value).matches()) {
                result[1] = value.split("x")[0];
                result[2] = value.split("x")[1];
                result[0] = pointToSpace(nom.split(result[1] + "x" + result[2])[0]);
                break;
            } else if (patternse.matcher(value).matches() || patternse2.matcher(value).matches()
                    || patternse3.matcher(value).matches()) {
                result[1] = value.split("E")[0].split("S")[1];
                result[2] = value.split("E")[1];
                result[0] = pointToSpace(nom.split("S" + result[1] + "E" + result[2])[0]);
                break;
            }
        }
        return result;
    }

    public static String[] extraireNomEtNumSaison(String nom) {
        ArrayList<String> tentative = new ArrayList<>(Stream.of(nom.toUpperCase().replace(".", " ").split(" ")).toList());
        //Saison seulement dossier
        Pattern patterns = Pattern.compile("Saison");
        Pattern patterns2 = Pattern.compile("S[0-9][0-9]");
        Pattern patterns3 = Pattern.compile("S[0-9]");
        //Saison et épisode pour les fichiers
        Pattern patternx = Pattern.compile("[0-9][0-9]X[0-9][0-9]");
        Pattern patternx2 = Pattern.compile("[0-9]X[0-9][0-9]");
        Pattern patternse = Pattern.compile("S[0-9][0-9]E[0-9][0-9]");
        Pattern patternse2 = Pattern.compile("S[0-9][0-9]E[0-9][0-9][0-9]");
        Pattern patternse3 = Pattern.compile("S[0-9]E[0-9][0-9]");

        String[] result = new String[2];
        for(String value : tentative) {
            if (patterns2.matcher(value).matches()
                    || patterns3.matcher(value).matches()) {
                result[1] = value.split("S")[1];
                result[0] = pointToSpace(nom.toUpperCase().split("S" + result[1])[0]);
                break;
            } else if (patterns.matcher(value).matches()) {
                result[1] = String.valueOf(tentative.indexOf(value)+1);
                result[0] = pointToSpace(nom.toUpperCase().split("Saison")[0]);
                break;
            } else if (patternx.matcher(value).matches() || patternx2.matcher(value).matches()) {;
                result[1] = value.split("X")[0];
                String num = value.split("X")[1];
                result[0] = pointToSpace(nom.toUpperCase().split(result[1] + "X" + num)[0]);
                break;
            } else if (patternse.matcher(value).matches() || patternse2.matcher(value).matches() || patternse3.matcher(value).matches()) {
                result[1] = value.split("E")[0].split("S")[1];
                String num = value.split("E")[1];
                result[0] = pointToSpace(nom.toUpperCase().split("S" + result[1] + "E" + num)[0]);
                break;
            }
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
