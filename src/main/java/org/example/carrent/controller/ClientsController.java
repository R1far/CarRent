package org.example.carrent.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.carrent.db.ClientDB;
import org.example.carrent.model.Client;
import org.example.carrent.utils.AlertUtils;

import java.io.IOException;
import java.util.List;

// Контроллер раздела "Клиенты" - отображает таблицу клиентов
public class ClientsController {

    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> passportSeriesColumn;
    @FXML
    private TableColumn<Client, String> passportNumberColumn;

    @FXML
    public void initialize() {
        // Настраиваем фабрики значений для колонок
        nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
        passportSeriesColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("passportSeries"));
        passportNumberColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("passportNumber"));

        refreshTable();
    }

    // Обновление таблицы клиентов
    private void refreshTable() {
        List<Client> clientList = ClientDB.getClients();
        ObservableList<Client> observableList = FXCollections.observableArrayList(clientList);
        clientsTable.setItems(observableList);
    }

    // Обработчик добавления клиента
    @FXML
    protected void onAddClientClick() {
        openClientForm(null);
    }

    // Обработчик редактирования клиента
    @FXML
    protected void onEditClientClick() {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            AlertUtils.showWarning("Внимание", "Выберите клиента для редактирования!");
            return;
        }

        openClientForm(selectedClient);
    }

    // Открытие формы клиента
    private void openClientForm(Client client) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/carrent/client-form-view.fxml"));
            Parent parent = fxmlLoader.load();

            ClientFormController controller = fxmlLoader.getController();
            controller.setClient(client);

            Scene scene = new Scene(parent, 340, 450);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            // Заголовок зависит от режима
            if (client == null) {
                stage.setTitle("Новый клиент");
            } else {
                stage.setTitle("Редактирование клиента");
            }

            stage.setScene(scene);
            stage.showAndWait();

            // Обновляем таблицу после закрытия формы
            refreshTable();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Обработчик удаления клиента
    @FXML
    protected void onDeleteClientClick() {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            AlertUtils.showWarning("Внимание", "Выберите клиента для удаления!");
            return;
        }

        // Удаляем клиента и обновляем таблицу
        ClientDB.deleteClient(selectedClient.getId());
        refreshTable();
    }
}
