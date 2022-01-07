package server;



public class GetDirectory {
    public static String FileDir(String file) {
        return System.getProperty("user.dir") + System.getProperty("file.separator") + "Files" + System.getProperty("file.separator") + file;
        //return System.getProperty("user.dir") + System.getProperty("file.separator") + "RTI_Chat" + System.getProperty("file.separator") + "Files" + System.getProperty("file.separator") + file;
    }
}