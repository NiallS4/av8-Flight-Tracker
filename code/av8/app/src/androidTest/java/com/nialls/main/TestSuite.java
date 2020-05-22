package com.nialls.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({APICallerTest.class,
            ARActivityUITest.class,
            FilterTest.class,
            IconChooserTest.class,
            MainActivityTest.class,
            MapTest.class})

public class TestSuite {}


