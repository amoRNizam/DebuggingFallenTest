import com.sun.deploy.panel.JreTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;

public class FileManager {

    /**
     * Title of the application
     */
    public static final String APP_TITLE = "Debug tests";
    /**
     * Used to open/edit/print files.
     */
    private Desktop desktop;
    /**
     * Provides nice icons and names for files.
     */
    private FileSystemView fileSystemView;

    /**
     * currently selected File.
     */
    private static File currentFile;
    private static String currentFileDiff;

    /**
     * Main GUI container
     */
    private JPanel gui;

    /**
     * File-system tree. Built Lazily
     */
    private JTree tree;
    private DefaultTreeModel treeModel;

    /**
     * Directory listing
     */
    private JTable table;
    private JProgressBar progressBar;
    /**
     * Table model for File[].
     */
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;

    /* File controls. */
    private JButton openFile;
    private JButton printFile;
    private JButton editFile;
    private JButton deleteFile;
    private JButton newFile;
    private JButton copyFile;
    /* File details. */
    private JLabel fileName;
    private JTextField path;
    public static JTextField pathProject;
    private JTextField pathResult;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;

    /* GUI options/containers for new File/Directory creation.  Created lazily. */
    private JPanel newFilePanel;
    private JRadioButton newTypeFile;
    private JTextField name;

    public Container getGui() {
        if (gui == null) {
            gui = new JPanel(new BorderLayout(3, 3));
            gui.setBorder(new EmptyBorder(5, 5, 5, 5));
            //-------------------------------------
//            JList<JListExample.Product> list = new JList<>();
//            DefaultListModel<JListExample.Product> model = new DefaultListModel<>();
//            JSplitPane splitPane2 = new JSplitPane();
//            JFrame frame = new JFrame("Storage");
//            JPanel panel = new JPanel();
//            list.setModel(model);
//            model.addElement(new JListExample.Product("Item1", new BigDecimal("49.00")));
//            model.addElement(new JListExample.Product("Item2", new BigDecimal("150")));
//            model.addElement(new JListExample.Product("Item3", new BigDecimal("54.5")));
//            model.addElement(new JListExample.Product("Item4", new BigDecimal("120.00")));
//            splitPane2.setLeftComponent(new JScrollPane(list));
////            panel.add(label);
//            splitPane2.setRightComponent(panel);
//            splitPane2.setResizeWeight(0.5); //used to set the way it splits the left and right component
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.add(splitPane2);
//            frame.pack();
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//            frame.setSize(500, 300);
//            gui.add(list);
            //-------------------------------

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            JPanel detailView = new JPanel(new BorderLayout(3, 3));
            //fileTableModel = new FileTableModel();

            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    setFileDetails(((FileTableModel) table.getModel()).getFile(row));
                }
            };
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
            detailView.add(tableScroll, BorderLayout.CENTER);

            // the File tree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse) {
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
                    showChildren(node);
                    setFileDetails((File) node.getUserObject());
                }
            };

            // show the file system roots.
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add(node);
                //showChildren(node);
                //
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
                //
            }

            tree = new JTree(treeModel);
            tree.setRootVisible(false);
//            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            //**************************************
            JPanel fileMainDetails2 = new JPanel(new BorderLayout(4, 2));
            JList<JListExample.Product> list = new JList<>();
            DefaultListModel<JListExample.Product> model = new DefaultListModel<>();
            JLabel label = new JLabel();
            JPanel panel = new JPanel();
            JTextPane textPane = new JTextPane();
            JSplitPane splitPane2 = new JSplitPane();
            JToolBar toolBar2 = new JToolBar();


            list.setModel(model);
            // здесь заполняется текст для отображения в правой части панели
            list.getSelectionModel().addListSelectionListener(e -> {
                JListExample.Product p = list.getSelectedValue();
////                    textPane.setText(currentFile.getAbsolutePath());
//                label.setText(p.getName() + " price is = " + p.getPath());
                File file;
                try {

                    file = new File(FileTableModel.difImg.get("" + p.getName().trim()));
                    textPane.setText(file.getAbsolutePath());
                } catch (NullPointerException em) {
                    textPane.setText("Не найдено изображение расхождения сравниваемых изображений.");
                }
//                try {
//                    desktop.open(file);
//                } catch (IOException | NullPointerException ex) {
//                    ex.printStackTrace();
//                }
//                int row = list.getSelectionModel().getLeadSelectionIndex();
//                System.out.println("VALUE - " + list.getSelectedValue().getPath());
//                setSelectFile(((FileTableModel) list.getModel()).getFile(row));


            });
//            list.getSelectionModel().addListSelectionListener(e -> {
//                    int row = list.getSelectionModel().getLeadSelectionIndex();
//                    setSelectFile(((FileTableModel) list.getModel()).getFile(row));
//
//            });

            splitPane2.setLeftComponent(new JScrollPane(list));
            panel.add(textPane);
            splitPane2.setRightComponent(panel);
            splitPane2.setResizeWeight(0.5); // способ разделеня левого и правого компонента
            splitPane2.getLeftComponent().setPreferredSize(new Dimension(2, 180));
            splitPane2.getRightComponent().setPreferredSize(new Dimension(350, 180));
            //*****************************************
            JScrollPane treeScroll = new JScrollPane(splitPane2);
            tree.setVisibleRowCount(15);

            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(
                    200,
                    (int) preferredSize.getHeight());
            treeScroll.setPreferredSize(widePreferred);

            // details for a File
            JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
            fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

            fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            fileDetailsLabels.add(new JLabel("'Input' in project", JLabel.TRAILING));
            fileDetailsLabels.add(new JLabel("Result dir", JLabel.TRAILING));
            path = new JTextField(5);
            pathProject = new JTextField(5);
            pathResult = new JTextField(5);
            path.setEditable(false);
            fileDetailsValues.add(pathProject);
            fileDetailsValues.add(pathResult);
            fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
            date = new JLabel();
            fileDetailsValues.add(date);
            fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
            size = new JLabel();
            fileDetailsValues.add(size);
            fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

            JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
            isDirectory = new JRadioButton("Directory");
            isDirectory.setEnabled(false);
            flags.add(isDirectory);

            isFile = new JRadioButton("File");
            isFile.setEnabled(false);
            flags.add(isFile);
            fileDetailsValues.add(flags);

            int count = fileDetailsLabels.getComponentCount();
            for (int ii = 0; ii < count; ii++) {
                fileDetailsLabels.getComponent(ii).setEnabled(false);
            }

            JToolBar toolBar = new JToolBar();
            // mnemonics stop working in a floated toolbar
            toolBar.setFloatable(false);

//            openFile = new JButton("Open");
//            openFile.setMnemonic('o');
//            openFile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent ae) {
//                    try {
//                        desktop.open(currentFile);
//                    } catch (Throwable t) {
//                        showThrowable(t);
//                    }
//                    gui.repaint();
//                }
//            });
//            toolBar.add(openFile);

            editFile = new JButton("Edit");
            editFile.setMnemonic('e');
            editFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
//                    try {
//                        desktop.edit(currentFile);
//                    } catch (Throwable t) {
//                        showThrowable(t);
//                    }
//                    for (Map.Entry<String, String> img : FileTableModel.ERROR_DIFF_IMG.entrySet()) {
//                        System.out.println(img.getKey() + " * " + img.getValue());
//                    }
                    try {
                        Utils.reReference();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            toolBar.add(editFile);

//            printFile = new JButton("Print");
//            printFile.setMnemonic('p');
//            printFile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent ae) {
//                    try {
//                        desktop.print(currentFile);
//                    } catch (Throwable t) {
//                        showThrowable(t);
//                    }
//                }
//            });
//            toolBar.add(printFile);
            JButton openErrorImg = new JButton("Open Error");
            openErrorImg.setMnemonic('r');
            openErrorImg.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    //                        replaceFile();
//                    FileTableModel.getListDiffImg();
                    File file = new File(FileTableModel.difImg.get(list.getSelectedValue().getName()));
                    System.out.println(file.getAbsolutePath());
                    try {
                        desktop.open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            toolBar.add(openErrorImg);

            toolBar.addSeparator();

//            newFile = new JButton("New");
//            newFile.setMnemonic('n');
//            newFile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent ae) {
//                    newFile();
//                }
//            });
//            toolBar.add(newFile);

//            copyFile = new JButton("Copy");
//            copyFile.setMnemonic('c');
//            copyFile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent ae) {
//                    showErrorMessage("'Copy' not implemented.", "Not implemented.");
//                }
//            });
//            toolBar.add(copyFile);

//            JButton renameFile = new JButton("Rename");
//            renameFile.setMnemonic('r');
//            renameFile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent ae) {
//                    renameFile();
//                }
//            });
//            toolBar.add(renameFile);

            openFile = new JButton("Open");
            openFile.setMnemonic('o');
            openFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.open(currentFile);
                    } catch (Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(openFile);

            JButton replaceFile = new JButton("Replace");
            replaceFile.setMnemonic('r');
            replaceFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    //                        replaceFile();
                    FileTableModel.getListDiffImg();
                }
            });
            toolBar.add(replaceFile);

            // выбор директории проекта
            JButton selectDirProject = new JButton(" Select dirProject ");
//            selectDirProject.setIcon(new ImageIcon("resources/addFolder.png"));
//            selectDirProject.setIcon(new ImageIcon("resources/add.png"));
//            selectDirProject.setSize(20, 15);
            selectDirProject.setBorder(new LineBorder(Color.GRAY, 1));
            selectDirProject.setMnemonic('s');
            selectDirProject.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Выбор директории проекта");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    int result = chooser.showOpenDialog(gui);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        System.out.println(chooser.getSelectedFile());
                        pathProject.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
            toolBar.add(selectDirProject);

            // выбор директории результатов прогона
            JButton selectDirResultTest = new JButton(" Select DirResult ");
            selectDirResultTest.setBorder(new LineBorder(Color.GRAY, 1));
            selectDirResultTest.setMnemonic('s');
            selectDirResultTest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Выбор директории с результатами прогона");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    int result = chooser.showOpenDialog(gui);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        System.out.println(chooser.getSelectedFile());
                        pathResult.setText(chooser.getSelectedFile().getAbsolutePath());
                        showChildrenRes(pathResult);
                        model.clear(); // при выборе папки с результатами очищаем таблицу выбранных тестов
                    }
                }
            });
            toolBar.add(selectDirResultTest);

            deleteFile = new JButton(" ADD ");
            deleteFile.setMnemonic('d');
            deleteFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    if (FileTableModel.difImg.containsKey(currentFile.getName())) {
                        model.addElement(new JListExample.Product(currentFile.getName(), currentFile.getAbsolutePath()));
                        FileTableModel.ERROR_DIFF_IMG.put(currentFile.getName(), FileTableModel.difImg.get(currentFile.getName()));
                    } else {
                        System.out.println("В тесте нет ошибок!");
                    }
//                    System.out.println(table.getModel().getRowCount());
//                    table.getModel().getRowCount();
//                    System.out.println(currentFileDiff);
//                    System.out.println(Utils.getProperty().get("key"));
//                    System.out.println("Working Directory = " +
//                            System.getProperty("user.dir"));
                }
            });
            toolBar.add(deleteFile);

            // Check the actions are supported on this platform!
            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
//            printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

            toolBar.addSeparator();

            readable = new JCheckBox("Read  ");
            readable.setMnemonic('a');
            //readable.setEnabled(false);
            toolBar.add(readable);

            writable = new JCheckBox("Write  ");
            writable.setMnemonic('w');
            //writable.setEnabled(false);
            toolBar.add(writable);

            executable = new JCheckBox("Execute");
            executable.setMnemonic('x');
            //executable.setEnabled(false);
            toolBar.add(executable);

            JPanel fileView = new JPanel(new BorderLayout(3, 3));

            fileView.add(toolBar, BorderLayout.NORTH);
            fileView.add(fileMainDetails, BorderLayout.CENTER);
            fileView.add(fileMainDetails, BorderLayout.CENTER);

            detailView.add(fileView, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.VERTICAL_SPLIT,
                    treeScroll,
                    detailView);
            gui.add(splitPane, BorderLayout.CENTER);

            JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);

        }
        return gui;
    }

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0, 0);
    }

    private TreePath findTreePath(File find) {
        for (int ii = 0; ii < tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            File nodeFile = (File) node.getUserObject();

            if (nodeFile == find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    private void renameFile() {
        if (currentFile == null) {
            showErrorMessage("No file selected to rename.", "Select File");
            return;
        }

        String renameTo = JOptionPane.showInputDialog(gui, "New Name");
        if (renameTo != null) {
            try {
                boolean directory = currentFile.isDirectory();
                TreePath parentPath = findTreePath(currentFile.getParentFile());
                DefaultMutableTreeNode parentNode =
                        (DefaultMutableTreeNode) parentPath.getLastPathComponent();

                boolean renamed = currentFile.renameTo(new File(
                        currentFile.getParentFile(), renameTo));
                if (renamed) {
                    if (directory) {
                        // rename the node..

                        // delete the current node..
                        TreePath currentPath = findTreePath(currentFile);
                        System.out.println(currentPath);
                        DefaultMutableTreeNode currentNode =
                                (DefaultMutableTreeNode) currentPath.getLastPathComponent();

                        treeModel.removeNodeFromParent(currentNode);

                        // add a new node..
                    }

                    showChildren(parentNode);
                } else {
                    String msg = "The file '" +
                            currentFile +
                            "' could not be renamed.";
                    showErrorMessage(msg, "Rename Failed");
                }
            } catch (Throwable t) {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void deleteFile() {
        if (currentFile == null) {
            showErrorMessage("No file selected for deletion.", "Select File");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                gui,
                "Are you sure you want to delete this file?",
                "Delete File",
                JOptionPane.ERROR_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                System.out.println("currentFile: " + currentFile);
                TreePath parentPath = findTreePath(currentFile.getParentFile());
                System.out.println("parentPath: " + parentPath);
                DefaultMutableTreeNode parentNode =
                        (DefaultMutableTreeNode) parentPath.getLastPathComponent();
                System.out.println("parentNode: " + parentNode);

                boolean directory = currentFile.isDirectory();
                boolean deleted = currentFile.delete();
                if (deleted) {
                    if (directory) {
                        // delete the node..
                        TreePath currentPath = findTreePath(currentFile);
                        System.out.println(currentPath);
                        DefaultMutableTreeNode currentNode =
                                (DefaultMutableTreeNode) currentPath.getLastPathComponent();

                        treeModel.removeNodeFromParent(currentNode);
                    }

                    showChildren(parentNode);
                } else {
                    String msg = "The file '" +
                            currentFile +
                            "' could not be deleted.";
                    showErrorMessage(msg, "Delete Failed");
                }
            } catch (Throwable t) {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void newFile() {
        if (currentFile == null) {
            showErrorMessage("No location selected for new file.", "Select Location");
            return;
        }

        if (newFilePanel == null) {
            newFilePanel = new JPanel(new BorderLayout(3, 3));

            JPanel southRadio = new JPanel(new GridLayout(1, 0, 2, 2));
            newTypeFile = new JRadioButton("File", true);
            JRadioButton newTypeDirectory = new JRadioButton("Directory");
            ButtonGroup bg = new ButtonGroup();
            bg.add(newTypeFile);
            bg.add(newTypeDirectory);
            southRadio.add(newTypeFile);
            southRadio.add(newTypeDirectory);

            name = new JTextField(15);

            newFilePanel.add(new JLabel("Name"), BorderLayout.WEST);
            newFilePanel.add(name);
            newFilePanel.add(southRadio, BorderLayout.SOUTH);
        }

        int result = JOptionPane.showConfirmDialog(
                gui,
                newFilePanel,
                "Create File",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean created;
                File parentFile = currentFile;
                if (!parentFile.isDirectory()) {
                    parentFile = parentFile.getParentFile();
                }
                File file = new File(parentFile, name.getText());
                if (newTypeFile.isSelected()) {
                    created = file.createNewFile();
                } else {
                    created = file.mkdir();
                }
                if (created) {

                    TreePath parentPath = findTreePath(parentFile);
                    DefaultMutableTreeNode parentNode =
                            (DefaultMutableTreeNode) parentPath.getLastPathComponent();

                    if (file.isDirectory()) {
                        // add the new node..
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

                        TreePath currentPath = findTreePath(currentFile);
                        DefaultMutableTreeNode currentNode =
                                (DefaultMutableTreeNode) currentPath.getLastPathComponent();

                        treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
                    }

                    showChildren(parentNode);
                } else {
                    String msg = "The file '" +
                            file +
                            "' could not be created.";
                    showErrorMessage(msg, "Create Failed");
                }
            } catch (Throwable t) {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
                gui,
                errorMessage,
                errorTitle,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
                gui,
                t.toString(),
                t.getMessage(),
                JOptionPane.ERROR_MESSAGE
        );
        gui.repaint();
    }

    /**
     * Update the table on the EDT
     */
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel == null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    table.setRowHeight(icon.getIconHeight() + rowIconPadding);

                    setColumnWidth(0, -1);
                    setColumnWidth(3, 60);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4, -1);
                    setColumnWidth(5, -1);
                    setColumnWidth(6, -1);
                    setColumnWidth(7, -1);
                    setColumnWidth(8, -1);
                    setColumnWidth(9, -1);

                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header..
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int) preferred.getWidth() + 14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels.
     */
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels.
     */
    private void showChildrenRes(final JTextField pathResult) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = new File(pathResult.getText());
                if (file.isDirectory()) {
                    file = new File(pathResult.getText());
                    File[] files = fileSystemView.getFiles(file, true);
                    //!!
//                    if (node.isLeaf()) {
//                        for (File child : files) {
//                            if (child.isDirectory()) {
//                                publish(child);
//                            }
//                        }
//                    }

                    FileTableModel.listFile = new ArrayList<>(Arrays.asList(files));
//                    FileTableModel.listFile.forEach(x -> System.out.println("FILE = " + x));

                    setTableData(files);
                }
                return null;
            }

//            @Override
//            protected void process(List<File> chunks) {
//                for (File child : chunks) {
//                    node.add(new DefaultMutableTreeNode(child));
//                }
//            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * Update the File details view with the details of this File.
     */
    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame) gui.getTopLevelAncestor();
        if (f != null) {
            f.setTitle(
                    APP_TITLE +
                            " :: " +
                            fileSystemView.getSystemDisplayName(file));
        }

        gui.repaint();
    }

    private void setSelectFile(String file) {
        currentFileDiff = file;
        gui.repaint();
    }

    public static boolean copyFile(File from, File to) throws IOException, IOException {

        boolean created = to.createNewFile();

        if (created) {
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            try {
                fromChannel = new FileInputStream(from).getChannel();
                toChannel = new FileOutputStream(to).getChannel();

                toChannel.transferFrom(fromChannel, 0, fromChannel.size());

                // set the flags of the to the same as the from
                to.setReadable(from.canRead());
                to.setWritable(from.canWrite());
                to.setExecutable(from.canExecute());
            } finally {
                if (fromChannel != null) {
                    fromChannel.close();
                }
                if (toChannel != null) {
                    toChannel.close();
                }
                return false;
            }
        }
        return created;
    }

    public static void replaceFile(/*File from, File to*/) throws IOException {
        System.out.println(currentFile);
        File to = new File("C:\\Users\\USER\\Desktop\\src1.bmp");
        File from = new File(String.valueOf(currentFile));//new File("C:\\Users\\USER\\Desktop\\Мусорка\\output.png");
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(from).getChannel();
            destChannel = new FileOutputStream(to).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
        System.out.println("Replace");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Инициализируем конфиги
                new Config();
                try {
                    // Significantly improves the look of the output in
                    // terms of the file names returned by FileSystemView!
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception weTried) {
                }
                JFrame f = new JFrame(APP_TITLE);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                FileManager fileManager = new FileManager();
                f.setContentPane(fileManager.getGui());

                try {
                    URL urlBig = fileManager.getClass().getResource("fm-icon-32x32.png");
                    URL urlSmall = fileManager.getClass().getResource("fm-icon-16x16.png");
                    ArrayList<Image> images = new ArrayList<Image>();
                    images.add(ImageIO.read(urlBig));
                    images.add(ImageIO.read(urlSmall));
                    f.setIconImages(images);
                } catch (Exception weTried) {
                }

                f.pack();
                f.setLocationByPlatform(true);
                f.setMinimumSize(f.getSize());
                f.setVisible(true);

                fileManager.showRootFile();
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
    }
}