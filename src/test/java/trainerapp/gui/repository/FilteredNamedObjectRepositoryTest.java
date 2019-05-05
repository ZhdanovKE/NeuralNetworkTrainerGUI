package trainerapp.gui.repository;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;

/**
 * Test cases for FilteredNamedObjectRepository class.
 * @author Konstantin Zhdanov
 */
public class FilteredNamedObjectRepositoryTest {
    
    private static Predicate<String> lengthFilter;
    private FilteredNamedObjectRepository<String> instance;
    private NamedObjectRepository<String> repository;
    private int numFiltered;
    
    public FilteredNamedObjectRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpAll() {
        lengthFilter = s -> s.length() == 3;
    }
    
    @Before
    public void setUp() {
        repository = new NamedObjectRepository<>();
        repository.add("Name 1", "AAA");
        repository.add("Name 2", "BB");
        repository.add("Name 3", "CCCC");
        repository.add("Name 4", "DDD");
        repository.add("Name 5", "EE");
        repository.add("Name 6", "FFFFF");
        repository.add("Name 7", "");
        repository.add("Name 8", "G");
        repository.add("Name 9", "HHH");
        numFiltered = 3;
        instance = new FilteredNamedObjectRepository<>(repository, lengthFilter);
    }
    
    @After
    public void cleanUp() {
        repository = null;
        numFiltered = -1;
        instance = null;
    }

    /**
     * Test of add method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testAdd_Called_OnlyRepositoryAddCalled() {
        System.out.println("add");
        String name = "Name";
        String object = "Object";
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        instanceWrapper.add(name, object);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).add(name, object);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    /**
     * Test of remove method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testRemove_Called_OnlyRepositoryRemoveCalled() {
        System.out.println("remove");
        String name = "Name";
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        instanceWrapper.remove(name);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).remove(name);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    /**
     * Test of getObjectsObservableList method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testGetObjectsObservableList_Called_OnlyRepositoryGetObjectsObservableListCalled() {
        System.out.println("getObjectsObservableList");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        Mockito.when(repositoryMock.getObjectsObservableList()).
                thenReturn(FXCollections.observableArrayList());
        
        instanceWrapper.getObjectsObservableList();
        
        Mockito.verify(repositoryMock, Mockito.times(1)).getObjectsObservableList();
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testGetObjectsObservableList_Called_OnlyFilteredOutElemsReturned() {
        System.out.println("getObjectsObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("AAA");
        expResult.add("DDD");
        expResult.add("HHH");
        
        ObservableList<String> result = instance.getObjectsObservableList();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetObjectsObservableList_FilterOkElemAdded_ReturnedListChanges() {
        System.out.println("getObjectsObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("AAA");
        expResult.add("DDD");
        expResult.add("HHH");
        expResult.add("BVV");
        
        ObservableList<String> result = instance.getObjectsObservableList();
        instance.add("Another name", "BVV");
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetObjectsObservableList_FilterWrongElemAdded_ReturnedListNotChanged() {
        System.out.println("getObjectsObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("AAA");
        expResult.add("DDD");
        expResult.add("HHH");
        
        ObservableList<String> result = instance.getObjectsObservableList();
        instance.add("Another name", "VVVFF");
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamesObservableList method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testGetNamesObservableList_Called_OnlyRepositoryGetNamesObservableListCalled() {
        System.out.println("getNamesObservableList");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        Mockito.when(repositoryMock.getNamesObservableList()).
                thenReturn(FXCollections.observableArrayList());
        
        instanceWrapper.getNamesObservableList();
        
        Mockito.verify(repositoryMock, Mockito.times(1)).getNamesObservableList();
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testGetNamesObservableList_Called_ReturnOnlyFilteredOkNames() {
        System.out.println("getNamesObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("Name 1");
        expResult.add("Name 4");
        expResult.add("Name 9");
        
        ObservableList<String> result = instance.getNamesObservableList();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetNamesObservableList_FilteredOkElemAdded_ReturnedListChanges() {
        System.out.println("getNamesObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("Name 1");
        expResult.add("Name 4");
        expResult.add("Name 9");
        expResult.add("Another name");
        
        ObservableList<String> result = instance.getNamesObservableList();
        instance.add("Another name", "VVV");
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetNamesObservableList_FilteredWrongElemAdded_ReturnedListNotChanged() {
        System.out.println("getNamesObservableList");
        ObservableList<String> expResult = FXCollections.observableArrayList();
        expResult.add("Name 1");
        expResult.add("Name 4");
        expResult.add("Name 9");
        
        ObservableList<String> result = instance.getNamesObservableList();
        instance.add("Another name", "213213s");
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getNames method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testGetNames_Called_OnlyRepositoryGetNamesCalled() {
        System.out.println("getNames");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        instanceWrapper.getNames();
        
        Mockito.verify(repositoryMock, Mockito.times(1)).getNames();
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testGetNames_Called_ReturnOnlyFilteredOkElems() {
        System.out.println("getNames");
        Set<String> expResult = new HashSet<>();
        expResult.add("Name 1");
        expResult.add("Name 4");
        expResult.add("Name 9");
                
        Set<String> result = instance.getNames();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testIsEmpty_RepoContainOneFilterOkObject_ReturnFalse() {
        System.out.println("isEmpty");
        FilteredNamedObjectRepository instanceEmpty = new FilteredNamedObjectRepository(
                repository, lengthFilter);
        boolean expResult = false;
        instanceEmpty.add("Name 1", "A");
        instanceEmpty.add("Name 2", "AA");
        instanceEmpty.add("Name 3", "AAA");
        instanceEmpty.add("Name 4", "AAAA");
        
        boolean result = instanceEmpty.isEmpty();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsEmpty_RepoContainNoFilterOkObject_ReturnTrue() {
        System.out.println("isEmpty");
        NamedObjectRepository<String> repositoryEmpty = new NamedObjectRepository<>();
        FilteredNamedObjectRepository instanceEmpty = new FilteredNamedObjectRepository(
                repositoryEmpty, lengthFilter);
        boolean expResult = true;
        instanceEmpty.add("Name 1", "A");
        instanceEmpty.add("Name 2", "AA");
        instanceEmpty.add("Name 4", "AAAA");
        
        boolean result = instanceEmpty.isEmpty();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsEmpty_EmptyRepoAddFilterOkObject_ReturnFalse() {
        System.out.println("isEmpty");
        FilteredNamedObjectRepository instanceEmpty = new FilteredNamedObjectRepository(
                repository, lengthFilter);
        boolean expResult = false;
        instanceEmpty.add("Name", "AAA");
        
        boolean result = instanceEmpty.isEmpty();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsEmpty_EmptyRepoAddFilterWrongObject_ReturnTrue() {
        System.out.println("isEmpty");
        NamedObjectRepository<String> repositoryEmpty = new NamedObjectRepository<>();
        FilteredNamedObjectRepository instanceEmpty = new FilteredNamedObjectRepository(
                repositoryEmpty, lengthFilter);
        boolean expResult = true;
        instanceEmpty.add("Name", "WW");
        
        boolean result = instanceEmpty.isEmpty();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsEmpty_OneElemRepoRemoveFilterOkObject_ReturnTrue() {
        System.out.println("isEmpty");
        NamedObjectRepository<String> repositoryEmpty = new NamedObjectRepository<>();
        FilteredNamedObjectRepository instanceOneElem = new FilteredNamedObjectRepository(
                repositoryEmpty, lengthFilter);
        boolean expResult = true;
        instanceOneElem.add("Name", "AAA");
        instanceOneElem.add("Name Wrong", "A");
        instanceOneElem.remove("Name");
        
        boolean result = instanceOneElem.isEmpty();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsEmpty_OneElemRepoRemoveFilterWrongObject_ReturnFalse() {
        System.out.println("isEmpty");
        FilteredNamedObjectRepository instanceOneElem = new FilteredNamedObjectRepository(
                repository, lengthFilter);
        boolean expResult = false;
        instanceOneElem.add("Name", "AAA");
        instanceOneElem.add("Name Wrong", "A");
        instanceOneElem.remove("Name Wrong");
        
        boolean result = instanceOneElem.isEmpty();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class FilteredNamedObjectRepository.
     */
    
    @Test
    public void testSize_EmptyRepo_SizeZero() {
        System.out.println("size");
        NamedObjectRepository<String> repositoryEmpty = new NamedObjectRepository<>();
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryEmpty, lengthFilter);
        int expResult = 0;
        
        int result = instanceWrapper.size();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSize_OnlyFilterWrongElems_SizeZero() {
        System.out.println("size");
        NamedObjectRepository<String> repositoryEmpty = new NamedObjectRepository<>();
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryEmpty, lengthFilter);
        instanceWrapper.add("Name 1", "A");
        instanceWrapper.add("Name 2", "AA");
        instanceWrapper.add("Name 3", "AAAA");
        int expResult = 0;
        
        int result = instanceWrapper.size();
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSize_AddFilterOkObject_SizeIncreaseByOne() {
        System.out.println("size");
        int expResult = numFiltered + 1;
        instance.add("Another name", "VVV");
        
        int result = instance.size();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSize_AddFilterWrongObject_SizeStaySame() {
        System.out.println("size");
        int expResult = numFiltered;
        instance.add("Wrong size object name", "W");
        
        int result = instance.size();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSize_RemoveFilterOkObject_SizeDecreaseByOne() {
        System.out.println("size");
        int expResult = numFiltered - 1;
        instance.remove("Name 4");
        
        int result = instance.size();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSize_RemoveFilterWrongObject_SizeStaySame() {
        System.out.println("size");
        int expResult = numFiltered;
        instance.remove("Name 3");
        
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of rename method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testRename_Called_OnlyRepositoryRenameCalled() {
        System.out.println("rename");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        instanceWrapper.rename("Name", "New Name");
        
        Mockito.verify(repositoryMock, Mockito.times(1)).rename("Name", "New Name");
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    /**
     * Test of removeOnNameChangeListener method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testRemoveOnNameChangeListener_Called_OnlyRepositoryRemoveOnNameChangeListenerCalled() {
        System.out.println("removeOnNameChangeListener");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        instanceWrapper.removeOnNameChangeListener();
        
        Mockito.verify(repositoryMock, Mockito.times(1)).removeOnNameChangeListener();
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    /**
     * Test of setOnNameChangeListener method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testSetOnNameChangeListener_Called_OnlyRepositorySetOnNameChangeListenerCalled() {
        System.out.println("setOnNameChangeListener");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        NamedObjectRepository.NameChangeListener<String> listener = (object, oldName, newName) -> {
        };
        
        instanceWrapper.setOnNameChangeListener(listener);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).setOnNameChangeListener(listener);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    /**
     * Test of containsObject method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testContainsObject_Called_OnlyRepositoryContainsObjectCalled() {
        System.out.println("containsObject");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        String object = "Object";
        boolean result = instanceWrapper.containsObject(object);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).containsObject(object);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testContainsObject_NonExistingObject_ReturnFalse() {
        System.out.println("containsObject");
        String object = "Non existing object";

        boolean expResult = false;
        boolean result = instance.containsObject(object);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testContainsObject_FilteredOutObject_ReturnFalse() {
        System.out.println("containsObject");
        String object = "Name 2";

        boolean expResult = false;
        boolean result = instance.containsObject(object);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testContainsObject_FilteredOkObject_ReturnTrue() {
        System.out.println("containsObject");
        String object = "AAA";

        boolean expResult = true;
        boolean result = instance.containsObject(object);
        assertEquals(expResult, result);
    }

    /**
     * Test of containsName method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testContainsName_Called_OnlyRepositoryContainsNameCalled() {
        System.out.println("containsName");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        boolean result = instanceWrapper.containsName("Name");
        
        Mockito.verify(repositoryMock, Mockito.times(1)).containsName("Name");
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testContainsName_NonExistingElem_ReturnFalse() {
        System.out.println("containsName");
        String name = "Name Not Exist";

        boolean expResult = false;
        boolean result = instance.containsName(name);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testContainsName_FilteredOutElem_ReturnFalse() {
        System.out.println("containsName");
        String name = "Name 2";

        boolean expResult = false;
        boolean result = instance.containsName(name);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testContainsName_FilteredOkElem_ReturnTrue() {
        System.out.println("containsName");
        String name = "Name 1";

        boolean expResult = true;
        boolean result = instance.containsName(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNameForObject method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testGetNameForObject_Called_OnlyRepositoryGetNameForObjectCalled() {
        System.out.println("getNameForObject");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        String object = "AAA";
        String result = instanceWrapper.getNameForObject(object);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).getNameForObject(object);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testGetNameForObject_FilteredOutElem_ReturnNull() {
        System.out.println("getNameForObject");
        String object = "BB";

        String expResult = null;
        String result = instance.getNameForObject(object);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetNameForObject_FilteredOkElem_ReturnName() {
        System.out.println("getNameForObject");
        String object = "AAA";

        String expResult = "Name 1";
        String result = instance.getNameForObject(object);
        assertEquals(expResult, result);
    }

    /**
     * Test of get method, of class FilteredNamedObjectRepository.
     */
    @Test
    public void testGet_Called_OnlyRepositoryGetCalled() {
        System.out.println("get");
        NamedObjectRepository<String> repositoryMock = Mockito.mock(NamedObjectRepository.class);
        FilteredNamedObjectRepository instanceWrapper = new FilteredNamedObjectRepository(
                repositoryMock, lengthFilter);
        
        String name = "Name";
        instanceWrapper.get(name);
        
        Mockito.verify(repositoryMock, Mockito.times(1)).get(name);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }
    
    @Test
    public void testGet_FilteredOutElem_ReturnNull() {
        System.out.println("get");
        String name = "Name 2";
        
        Object expResult = null;
        Object result = instance.get(name);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGet_FilteredOkElem_ReturnElem() {
        System.out.println("get");
        String name = "Name 1";
        
        Object expResult = "AAA";
        Object result = instance.get(name);
        
        assertEquals(expResult, result);
    }
}
