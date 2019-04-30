package trainerapp.gui.repository;

import java.util.Collection;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Konstantin Zhdanov
 */
public class NamedObjectRepositoryTest {
    
    private NamedObjectRepository<Object> repository;
    
    public NamedObjectRepositoryTest() {
    }
    
    public void setUp() {
        repository = new NamedObjectRepository<>();
        repository.add("Name 1", "Object 1");
        repository.add("Name 2", "Object 2");
        repository.add("Name 3", "Object 3");
    }
    
    public void cleanUp() {
        repository = null;
    }

    /**
     * Test of rename method, of class NamedObjectRepository.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRename_OldNameNull_Throw() {
        System.out.println("rename");
        String oldName = null;
        String newName = "new Name";
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.rename(oldName, newName);
        
        fail("The test case must throw");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRename_OldNameDoesntExist_Throw() {
        System.out.println("rename");
        String oldName = "No name";
        String newName = "New name";
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add("Another name", new Object());
        
        instance.rename(oldName, newName);
        
        fail("The test case must throw");
    }
    
    @Test(expected = NullPointerException.class)
    public void testRename_OldNameExistAndNewNameNull_Throw() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = null;
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, new Object());
        
        instance.rename(oldName, newName);
        
        fail("The test case must throw");
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetOldReturnNull() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, new Object());
        
        instance.rename(oldName, newName);
        
        assertNull(instance.get(oldName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_ContainsOldReturnFalse() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, new Object());
        
        instance.rename(oldName, newName);
        
        assertTrue(!instance.containsName(oldName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNewReturnOldObject() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        assertSame(object, instance.get(newName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_ContainsNewReturnTrue() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, new Object());
        
        instance.rename(oldName, newName);
        
        assertTrue(instance.containsName(newName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_SizeStaySame() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        int oldSize = instance.size();
        
        instance.rename(oldName, newName);
        
        assertEquals(oldSize, instance.size());
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNameForObjectReturnNewName() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        assertSame(newName, instance.getNameForObject(object));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNamesObservableListNotContainOldName() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        List<String> newNamesList = instance.getNamesObservableList();
        
        assertThat(newNamesList, CoreMatchers.not(CoreMatchers.hasItem(oldName)));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNamesObservableListContainNewName() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        List<String> newNamesList = instance.getNamesObservableList();
        
        assertThat(newNamesList, CoreMatchers.hasItem(newName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNamesNotContainOldName() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        Collection<String> newNamesList = instance.getNames();
        
        assertThat(newNamesList, CoreMatchers.not(CoreMatchers.hasItem(oldName)));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetNamesContainNewName() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        
        instance.rename(oldName, newName);
        
        Collection<String> newNamesList = instance.getNames();
        
        assertThat(newNamesList, CoreMatchers.hasItem(newName));
    }
    
    @Test
    public void testRename_OldNameExistAndNewNameNotNull_GetObjectsObservableListNotChanged() {
        System.out.println("rename");
        String oldName = "Old name";
        String newName = "New name";
        Object object = new Object();
        NamedObjectRepository instance = new NamedObjectRepository();
        instance.add(oldName, object);
        Collection<Object> oldObjectsList = instance.getObjectsObservableList();
        
        instance.rename(oldName, newName);
        
        Collection<Object> newObjectsList = instance.getObjectsObservableList();
        
        assertThat(newObjectsList, CoreMatchers.hasItems(oldObjectsList.toArray()));
        assertThat(oldObjectsList, CoreMatchers.hasItems(newObjectsList.toArray()));
    }
}
