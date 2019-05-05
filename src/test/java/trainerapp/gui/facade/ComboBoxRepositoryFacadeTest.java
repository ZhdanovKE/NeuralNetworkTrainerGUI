package trainerapp.gui.facade;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.control.ComboBox;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mockito;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.testutil.JavaFXThreadingRule;

/**
 * Test cases for ComboBoxRepositoryFacade class.
 * Run with JavaFXThreadingRule to be run in the JavaFX thread.
 * @author Konstantin Zhdanov
 */
public class ComboBoxRepositoryFacadeTest {
    
    @Rule 
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();
    
    private ComboBox<Object> comboBox;
    private Function<Object, String> converter;
    
    public ComboBoxRepositoryFacadeTest() {
    }
    
    @Before
    public void setUp() {
        comboBox = new ComboBox<>();
        converter = (t) -> t.toString();
    }
    
    @After
    public void cleanUp() {
        comboBox = null;
    }

    /**
     * Test of setRepository method, of class ComboBoxRepositoryFacade.
     */
    @Test(expected = NullPointerException.class)
    public void testSetRepository_NullRepository_Throw() {
        System.out.println("setRepository");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        instance.setRepository(null);
        
        fail("The test case must throw");
    }
    
    @Test
    public void testSetRepository_CorrectRepository_ComboBoxItemsReturnSameCollection() {
        System.out.println("setRepository");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        
        instance.setRepository(repo);
        
        List<Object> items = comboBox.getItems();
        assertThat(items, CoreMatchers.is(repo.getObjectsObservableList()));
    }

    @Test
    public void testSetRepository_CorrectRepository_ComboBoxValueNull() {
        System.out.println("setRepository");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        
        instance.setRepository(repo);
        
        Object actual = comboBox.getValue();
        assertThat(actual, CoreMatchers.nullValue());
    }
    
    /**
     * Test of select method, of class ComboBoxRepositoryFacade.
     */
    @Test
    public void testSelect_ExistingObject_ComboboxValueReturnSameObject() {
        System.out.println("select");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        Object item = repo.get("Object 1");

        instance.select(item);
        
        Object actual = comboBox.getValue();
        
        assertThat(actual, CoreMatchers.is(item));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testSelect_RepositoryNotSet_Throw() {
        System.out.println("select");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);

        Object item = "Any object";
        instance.select(item);
        
        fail("The test case must throw");
    }
    
    @Test(expected = NullPointerException.class)
    public void testSelect_NullObject_Throw() {
        System.out.println("select");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);

        Object item = null;
        instance.select(item);
        
        fail("The test case must throw");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSelect_NonExistingObject_Throw() {
        System.out.println("select");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);

        Object item = "Not Object 4";
        instance.select(item);
        
        fail("The test case must throw");
    }

    /**
     * Test of getSelectedItem method, of class ComboBoxRepositoryFacade.
     */
    @Test
    public void testGetSelectedItem_NonSelected_ReturnNull() {
        System.out.println("getSelectedItem");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        Object expResult = null;
        Object result = instance.getSelectedItem();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetSelectedItem_ObjectWasSelected_ReturnSameObject() {
        System.out.println("getSelectedItem");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        Object expResult = "Object 2";
        instance.select(expResult);
        
        Object result = instance.getSelectedItem();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of setOnItemSelected method, of class ComboBoxRepositoryFacade.
     */
    @Test(expected = NullPointerException.class)
    public void testSetOnItemSelected_NullArgument_Throw() {
        System.out.println("setOnItemSelected");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
         
        instance.setOnItemSelected(null);
        
        fail("The test case must throw");
    }
    
    @Test
    public void testSetOnItemSelected_SelectInvoked_HandlerInvokedWithSelectedObject() {
        System.out.println("setOnItemSelected");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        
        Consumer<Object> handler = Mockito.mock(Consumer.class);
        instance.setOnItemSelected(handler);
        
        Object selected = repo.get("Object 2");
        instance.select(selected);
        
        Mockito.verify(handler, Mockito.times(1)).accept(selected);
    }
    
    @Test
    public void testSetOnItemSelected_SelectAlreadySelected_HandlerNotInvoked() {
        System.out.println("setOnItemSelected");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        
        Object selected = repo.get("Object 2");
        instance.select(selected);
        
        Consumer<Object> handler = Mockito.mock(Consumer.class);
        instance.setOnItemSelected(handler);
        
        instance.select(selected);
        
        Mockito.verify(handler, Mockito.never()).accept(Mockito.anyObject());
    }
    
    @Test
    public void testSetOnItemSelected_SelectNonExistingObjectInvoked_HandlerNotInvoked() {
        System.out.println("setOnItemSelected");
        ComboBoxRepositoryFacade instance = new ComboBoxRepositoryFacade(
                comboBox, converter);
        NamedObjectRepository<Object> repo = new NamedObjectRepository<>();
        repo.add("Object 1", "Object 1");
        repo.add("Object 2", "Object 2");
        repo.add("Object 3", "Object 3");
        instance.setRepository(repo);
        
        Consumer<Object> handler = Mockito.mock(Consumer.class);
        instance.setOnItemSelected(handler);
        try {
            Object selected = "Not Object 3";
            instance.select(selected);
        }
        catch(IllegalArgumentException e) {
            Mockito.verify(handler, Mockito.never()).accept(Mockito.anyObject());
            return;
        }
        
        fail("The test case shouldn't reach this line");
    }
}
