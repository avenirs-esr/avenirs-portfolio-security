/**
 * 
 */
package fr.avenirsesr.portfolio.security.controllers;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * <h1>AccessControlControllerAllTests</h1>
 * <p>
 * Description:  Test suite for AccessControlController.
 * </p>
 * 
 * <h2>Version:</h2>
 * 1.0
 * 
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 1 Oct 2024
 */

@Suite
@SelectClasses({ AccessControlControllerCase1Test.class, AccessControlControllerCase2Test.class,
    AccessControlControllerTest.class })
public class AccessControlControllerAllTests {

}
