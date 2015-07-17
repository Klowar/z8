package org.zenframework.z8.pde.refactoring;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

@SuppressWarnings("rawtypes")
public class UserInterfaceManager {
    private Map<Class, Tuple> fMap = new HashMap<Class, Tuple>();

    private static class Tuple {
        private Class starter;
        private Class wizard;

        public Tuple(Class s, Class w) {
            starter = s;
            wizard = w;
        }
    }

    protected void put(Class processor, Class starter, Class wizard) {
        fMap.put(processor, new Tuple(starter, wizard));
    }

    @SuppressWarnings("unchecked")
    public UserInterfaceStarter getStarter(Refactoring refactoring) {
        RefactoringProcessor processor = (RefactoringProcessor)refactoring.getAdapter(RefactoringProcessor.class);

        if(processor == null)
            return null;

        Tuple tuple = (Tuple)fMap.get(processor.getClass());

        if(tuple == null)
            return null;

        try {
            UserInterfaceStarter starter = (UserInterfaceStarter)tuple.starter.newInstance();
            Class wizardClass = tuple.wizard;
            Constructor constructor = wizardClass.getConstructor(new Class[] { Refactoring.class });
            RefactoringWizard wizard = (RefactoringWizard)constructor.newInstance(new Object[] { refactoring });
            starter.initialize(wizard);
            return starter;
        }
        catch(NoSuchMethodException e) {
            return null;
        }
        catch(IllegalAccessException e) {
            return null;
        }
        catch(InstantiationException e) {
            return null;
        }
        catch(IllegalArgumentException e) {
            return null;
        }
        catch(InvocationTargetException e) {
            return null;
        }
    }
}
