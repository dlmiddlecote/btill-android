package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import junit.framework.TestCase;

import java.util.ArrayList;

public class OrderDialogFragmentTest extends TestCase {

    public void testGetMenu() throws Exception {

    }

    public void testSetMenu() throws Exception {

    }

    public void testNewInstance() throws Exception {
        ArrayList<MenuItem> list = new ArrayList<MenuItem>();
        list.add(new MenuItem("Item", new GBP(500), "Mains"));
        Menu testMenu = new Menu(list);
        OrderDialogFragment fragment = OrderDialogFragment.newInstance(testMenu);

        assertEquals(testMenu, fragment.getMenu());
    }
}