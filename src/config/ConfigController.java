package config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ConfigController
{
    private ConfigManager configManager;

    @FXML private TextField directoryTextField;
    @FXML private TextField commandTextField;

    @FXML private Button excelButton;

    @FXML private CheckBox caseCheckbox;

    @FXML private TableView<ConfigItem> tableView;
    @FXML private TableView<ConfigItem> nfTableView;

    @FXML private TabPane tabPane;

    @FXML private HBox commandHbox;

    @FXML private Label summaryLabel;
    @FXML private Label filesFoundLabel;
    @FXML private Label filesSearchedLabel;

    private ConfigItem[] foundItems;
    private ConfigItem[] notFoundItems;


    @FXML private void initialize()
    {
        System.out.println("Initializing ConfigController...");
        this.directoryTextField.setDisable(true);
    }

    @FXML private void didSelectDirectoryButton()
    {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a Directory");

        File directory = chooser.showDialog(null);

        if (directory == null)
            return;

        this.configManager = new ConfigManager(directory);
        this.directoryTextField.setText(directory.getPath());

        this.commandHbox.setDisable(false);
    }

    @FXML private void didSelectCommandButton()
    {
        // if the directory is not selected
        if(this.configManager == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a directory to search.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String command = this.commandTextField.getText();

        // if a command was not entered
        if(command.compareTo("") == 0)
        {
            resetFields(true);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please type a command to search within the directory files", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try
        {
            // retrieve the items with the given search command
            foundItems = this.configManager.search(command, this.caseCheckbox.isSelected());

            // switch to the "Files Found" tab
            this.tabPane.getSelectionModel().select(0);

            // if nothing was found given the search command
            if(foundItems == null)
            {
                // alert the user that no items were found given the search criteria
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nothing was found!", ButtonType.OK);
                alert.showAndWait();

                // an empty array to display in the tableview
                foundItems = new ConfigItem[]{};

                // switch to the "Files Not Found" tab
                this.tabPane.getSelectionModel().select(1);
            }

            // create observable list for tableview
            ObservableList foundList = FXCollections.observableArrayList(foundItems);

            // get not found items and create observable list for tableview
            notFoundItems = this.configManager.getNotFoundItems();
            ObservableList notFoundList = FXCollections.observableArrayList(notFoundItems);

            // set tableview items to new items found
            tableView.getItems().setAll(foundList);
            nfTableView.getItems().setAll(notFoundList);

            // update summary of search
            this.filesSearchedLabel.setText("Number of Files Searched: " + this.configManager.fileFoundCount);
            this.filesFoundLabel.setText("Number of Entries: " + foundItems.length);

            resetFields(false);

        } catch (IOException e)
        {
            alertError();
            e.printStackTrace();
        }
    }

    private void resetFields(boolean value)
    {
        this.excelButton.setDisable(value);
        this.tabPane.setDisable(value);
        this.summaryLabel.setDisable(value);
        this.filesFoundLabel.setDisable(value);
        this.filesSearchedLabel.setDisable(value);
    }

    @FXML private void didSelectGenerateExcelButton()
    {
        Workbook wb = new HSSFWorkbook();

        Sheet foundSheet = wb.createSheet("Found");
        Sheet notFoundSheet = wb.createSheet("Not Found");

        reportFound(foundSheet);
        reportNotFound(notFoundSheet);

        try
        {
            FileOutputStream fos = new FileOutputStream(this.configManager.getDirectoryString() + "/report.xls");
            wb.write(fos);
            fos.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Excel File created within " + this.configManager.getDirectoryString(), ButtonType.OK);
            alert.showAndWait();

        } catch(FileNotFoundException e)
        {
            alertError();
            e.printStackTrace();
        } catch(IOException e)
        {
            alertError();
            e.printStackTrace();
        }

    }

    private void alertError()
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error! Try again!", ButtonType.OK);
        alert.showAndWait();
    }

    private void reportNotFound(Sheet notFoundSheet)
    {
        ConfigItem[] notFoundItems = this.configManager.getNotFoundItems();

        Row headRow = notFoundSheet.createRow(0);
        headRow.createCell(0).setCellValue("ID");
        headRow.createCell(1).setCellValue("File Name");

        for (int i = 0; i < notFoundItems.length; i++)
        {
            Row row = notFoundSheet.createRow(i+1);

            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(notFoundItems[i].getId());
            row.createCell(cellIndex++).setCellValue(notFoundItems[i].getFileName());
        }


    }

    private void reportFound(Sheet foundSheet)
    {
        Row headRow = foundSheet.createRow(0);
        headRow.createCell(0).setCellValue("ID");
        headRow.createCell(1).setCellValue("File Name");
        headRow.createCell(2).setCellValue("Command");
        headRow.createCell(3).setCellValue("Line Number");

        for (int i = 0; i < foundItems.length; i++)
        {
            Row row = foundSheet.createRow(i+1);

            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(foundItems[i].getId());
            row.createCell(cellIndex++).setCellValue(foundItems[i].getFileName());
            row.createCell(cellIndex++).setCellValue(foundItems[i].getLineCommand());
            row.createCell(cellIndex++).setCellValue(foundItems[i].getLineNumber());
        }
    }


}
