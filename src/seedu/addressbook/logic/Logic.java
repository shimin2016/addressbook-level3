package seedu.addressbook.logic;

import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.parser.Parser;
import seedu.addressbook.storage.StorageFile;
import seedu.addressbook.storage.Storage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Represents the main Logic of the AddressBook.
 */
public class Logic {

    public static final int INITIAL_STORAGE_INDEX = 0;
    public static final String TYPE_OF_STORAGE_FILE = "Storage File 1";
    private ArrayList<Storage> storage_list = new ArrayList<Storage>();

    private Storage storage;
    private AddressBook addressBook;

    /** The list of person shown to the user most recently.  */
    private List<? extends ReadOnlyPerson> lastShownList = Collections.emptyList();

    public Logic() throws Exception{
/*        setStorage(initializeStorage());
        setAddressBook(storage.load());*/
        addStorage(initializeStorage(TYPE_OF_STORAGE_FILE));
        setStorage(storage_list.get(INITIAL_STORAGE_INDEX));
        setAddressBook(storage_list.get(INITIAL_STORAGE_INDEX).load());
    }

    Logic(Storage storageFile, AddressBook addressBook){
        addStorage(storageFile);
        setStorage(storageFile);
        setAddressBook(addressBook);
    }

    void addStorage(Storage newStorage){
        storage_list.add(newStorage);
    }

    void setStorage(Storage storage){
        this.storage = storage;
    }

    void setAddressBook(AddressBook addressBook){
        this.addressBook = addressBook;
    }

    /**
     * Creates the Storage object based on the user specified path (if any) or the default storage path.
     * @throws Storage.InvalidStorageFilePathException if the target file path is incorrect.
     */
    private Storage initializeStorage(String storageType) throws StorageFile.InvalidStorageFilePathException {
        switch(storageType) {
        case TYPE_OF_STORAGE_FILE:
            //return other type of storage file
        default:
            return new StorageFile();
        }
    }

    public String getStorageFilePath(int storageIndex) {
        return storage_list.get(storageIndex).getPath();
    }

    /**
     * Unmodifiable view of the current last shown list.
     */
    public List<ReadOnlyPerson> getLastShownList() {
        return Collections.unmodifiableList(lastShownList);
    }

    protected void setLastShownList(List<? extends ReadOnlyPerson> newList) {
        lastShownList = newList;
    }

    /**
     * Parses the user command, executes it, and returns the result.
     * @throws Exception if there was any problem during command execution.
     */
    public CommandResult execute(String userCommandText) throws Exception {
        Command command = new Parser().parseCommand(userCommandText);
        CommandResult result = execute(command);
        recordResult(result);
        return result;
    }

    /**
     * Executes the command, updates storage, and returns the result.
     *
     * @param command user command
     * @return result of the command
     * @throws Exception if there was any problem during command execution.
     */
    private CommandResult execute(Command command) throws Exception {
        command.setData(addressBook, lastShownList);
        CommandResult result = command.execute();
        for (Storage storage : storage_list) {
            storage.save(addressBook);
        }
        return result;
    }

    /** Updates the {@link #lastShownList} if the result contains a list of Persons. */
    private void recordResult(CommandResult result) {
        final Optional<List<? extends ReadOnlyPerson>> personList = result.getRelevantPersons();
        if (personList.isPresent()) {
            lastShownList = personList.get();
        }
    }
}
