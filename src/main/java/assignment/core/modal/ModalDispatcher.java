package assignment.core.modal;


import assignment.core.modal.selector.*;
import assignment.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ModalDispatcher {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private interface ControllerCreator {
        ModalController create(Stage modalStage);
    };

    private Stage primaryStage;
    private IntegerProperty openModalsCount = new SimpleIntegerProperty(0);
    public BooleanProperty isModalOpen = new SimpleBooleanProperty(false);

    public ModalDispatcher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        isModalOpen.bind(openModalsCount.isNotEqualTo(0));
    }

    private Object showModal(Stage stage, ControllerCreator controllerCreator) {
        Stage parentStage = (stage != null)
                ? stage
                : primaryStage;

        try {
            // Create the modal Stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);

            ModalController controller = controllerCreator.create(modalStage);
            modalStage.setTitle(controller.getTitle());

            FXMLLoader loader = new FXMLLoader(classLoader.getResource(controller.getTemplatePath()));
            loader.setController(controller);

            Parent layout = loader.load();
            modalStage.setScene(new Scene(layout));

            // Show the modal, wait until the user closes it
            // and then return the result
            openModalsCount.setValue(openModalsCount.getValue() + 1);
            modalStage.showAndWait();
            openModalsCount.setValue(openModalsCount.getValue() - 1);

            return controller.result();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountType showCreateAccountTypeModal(Stage stage) {
        return (AccountType) showModal(stage, (Stage modalStage) -> {
            return new AccountTypeFormController(this, modalStage,
                     true, new AccountType());
        });
    }

    public AccountType showSelectAccountTypeModal(Stage stage) {
        return (AccountType) showModal(stage, (Stage modalStage) -> {
            return new AccountTypeSelectorController(this, modalStage,
                    false);
        });
    }

    public Account showCreateAccountModal(Stage stage) {
        return (Account) showModal(stage, (Stage modalStage) -> {
            return new AccountFormController(this, modalStage,
                    true, new Account());
        });
    }

    public Client showCreateClientModal(Stage stage) {
        return (Client) showModal(stage, (Stage modalStage) -> {
            return new ClientFormController(this, modalStage,
                    true, new Client());
        });
    }

    public Client showSelectClientModal(Stage stage) {
        return (Client) showModal(stage, (Stage modalStage) -> {
            return new ClientSelectorController(this, modalStage,
                    false);
        });
    }

    public Client showSelectClientModal(Stage stage, boolean canCreate) {
        return (Client) showModal(stage, (Stage modalStage) -> {
            return new ClientSelectorController(this, modalStage,
                    canCreate);
        });
    }

    public PriceType showSelectPriceTypeModal(Stage stage) {
        return (PriceType) showModal(stage, (Stage modalStage) -> {
            return new PriceTypeSelectorController(this, modalStage,
                    false);
        });
    }

    public Price showCreatePriceModal(Stage stage) {
        return (Price) showModal(stage, (Stage modalStage) -> {
            return new PriceFormController(this, modalStage,
                    true, new Price());
        });
    }

    public Price showSelectPriceModal(Stage stage) {
        return (Price) showModal(stage, (Stage modalStage) -> {
            return new PriceSelectorController(this, modalStage,
                    false);
        });
    }

    public Motorhome showCreateMotorhomeModal(Stage stage) {
        return (Motorhome) showModal(stage, (Stage modalStage) -> {
            return new MotorhomeFormController(this, modalStage,
                    true, new Motorhome());
        });
    }

    public Motorhome showSelectMotorhomeModal(Stage stage) {
        return (Motorhome) showModal(stage, (Stage modalStage) -> {
            return new MotorhomeSelectorController(this, modalStage,
                    false);
        });
    }

    public Extra showCreateExtrasModal(Stage stage) {
        return (Extra) showModal(stage, (Stage modalStage) -> {
            return new ExtraFormController(this, modalStage,
                    true, new Extra());
        });
    }

    public Extra showExtraClientModal(Stage stage) {
        return (Extra) showModal(stage, (Stage modalStage) -> {
            return new ExtraSelectorController(this, modalStage,
                    false);
        });
    }

    public Order showCreateOrderModal(Stage stage) {
        return (Order) showModal(stage, (Stage modalStage) -> {
            return new OrderFormController(this, modalStage,
                    true, new Order());
        });
    }

    public Order showCancelOrderModal(Stage stage, Order order) {
        return (Order) showModal(stage, (Stage modalStage) -> {
            return new OrderCancelFormController(this, modalStage, order);
        });
    }
}
