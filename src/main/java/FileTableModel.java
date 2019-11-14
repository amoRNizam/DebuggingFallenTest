import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class FileTableModel extends AbstractTableModel {
    public static Map<String, String> difImg = new HashMap<>();
    public static String fileName = "difference_scr";

    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
            "Icon",
            "File",
            "Path/name",
            "Size",
            "Last Modified",
            "R",
            "W",
            "E",
            "D",
            "F",
            "Pass/Fail",
    };

    FileTableModel() {
        this(new File[0]);
    }

    FileTableModel(File[] files) {
        this.files = files;
    }
    public Object getValueAt(int row, int column) {
        File file = files[row];
        getFailDiffImg(file);
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            case 10:
                return getTestResults(file) ? "fail" : "pass";
//            default:
//                System.err.println("Logic Error");
        }
        return "";
    }

    public void getFailDiffImg(File file) {

        File root = file;
        try {
            boolean recursive = true;

            if (file.isDirectory()) {
                Collection files = FileUtils.listFiles(root, null, recursive);
                for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                    File file1 = (File) iterator.next();
//                    System.out.println(file1.getAbsolutePath());
                    if (file1.getName().contains(fileName)) {
                        difImg.put(file.getName(), file1.getAbsolutePath().trim());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        for (String img :
//                difImg) {
//            System.out.println(img);
//        }
//        for (Map.Entry<String, String> img : difImg.entrySet()) {
//            System.out.println(img.getKey() + " - " + img.getValue());
//        }

    }

    private boolean getTestResults(File file) {
        return func(file.getAbsolutePath(), fileName);
    }

    public static void getListDiffImg() {
        for (Map.Entry<String, String> img : difImg.entrySet()) {
            System.out.println(img.getKey() + " - " + img.getValue());
        }
    }

    static boolean flag;

    static boolean func(String path, String find) {
        try {
            File f = new File(path);
            String[] list = f.list();     //список файлов в текущей папке
            for (String file : list) {      //проверка на совпадение
                if (file.contains(find)) {
                    flag = true;
//                    System.out.println(path + "\\" + file + " !!!!!!!!!!!!!!!!!!");  //если найден, то выход
                    return true;
                }
                if (!path.endsWith("\\")) {
                    path += "\\";
                }
                File tempfile = new File(path + file);
//                System.out.println(path + file);
                if (!file.equals(".") && !file.equals("..")) {        //!!!
                    if (tempfile.isDirectory()) {      //иначе проверяем, если это папка
                        //path += file;
                        func(path + file, find);               //то рекурсивный вызов этой функции
                        if (flag) return true;
                    }
                }
            }
        }catch (NullPointerException ignored){}
        return false;
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}
