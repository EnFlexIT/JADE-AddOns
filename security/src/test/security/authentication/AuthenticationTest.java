/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package test.security.authentication;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.security.SecurityHelper;
import test.common.JadeController;
import test.common.Test;
import test.common.TestException;
import test.common.TestUtility;

/**
 * @author itr
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class AuthenticationTest extends Test {

    private static final String CONFIG_FILE_PATH = "cfg/test/security/authentication/";
    private static final String CONFIG_FILE = "configFile";
    private static final String ACTION = "action";
    private static final String START_MAIN_CONTAINER = "startMainContainer";
    private static final String START_REMOTE_CONTAINER = "startRemoteContainer";

    private static final String AUTHENTICATION_KIND = "authenticationKind";

    private static final String SUCCESSFUL_AUTHENTICATION = "successfulAuthentication";
    private static final String UNSUCCESSFUL_AUTHENTICATION = "unsuccessfulAuthentication";

    private static final String NEW_PLATFORM_NAME = "NewPlaform";

    private SecurityHelper sh;
    private JadeController newPlatform;

public Behaviour load(Agent a) throws TestException {
        try {

            SequentialBehaviour b1 = new SequentialBehaviour(a);
            b1.addSubBehaviour(new OneShotBehaviour(a) {

                private String configFile = getTestArgument(CONFIG_FILE);
                private String action = getTestArgument(ACTION);
                private String authKind = getTestArgument(AUTHENTICATION_KIND);

                public void action() {

                    if (action.equals(START_MAIN_CONTAINER)) {
                        //								START MAIN CONTAINER
                        try {
                            String params = createMainParams();
                            newPlatform = TestUtility.launchJadeInstance(
                                    "TestPlatform", null, params, null);

                            if (newPlatform != null) {
                                if (getTestArgument(AUTHENTICATION_KIND)
                                        .equals(SUCCESSFUL_AUTHENTICATION)) {
                                    passed("New instance of the Jade platform HAS BEEN created with SUCCESSFUL authentication.");
                                } else {
                                    failed("New instance of the Jade platform HAS BEEN created with UNsuccessful authentication.");
                                }
                            } else {
                                if (getTestArgument(AUTHENTICATION_KIND)
                                        .equals(SUCCESSFUL_AUTHENTICATION)) {
                                    failed("New instance of the Jade platform has NOT been created with SUCCESSFUL authentication.");
                                } else {
                                    passed("New instance of the Jade platform has NOT been created with UNsuccessful authentication.");
                                }
                            }
                        } catch (Exception te) {
                            if (getTestArgument(AUTHENTICATION_KIND).equals(
                                    UNSUCCESSFUL_AUTHENTICATION)) {
                                //te.printStackTrace();
                                passed("New instance of the Jade platform has NOT been created with UNsuccessful authentication.");
                            } else {
                                failed("New instance of the Jade platform has NOT been created with SUCCESSFUL authentication.\n"
                                        + "Error creating remote platform: "
                                        + te.getMessage());
                                te.printStackTrace();
                            }
                        }
                    }
                }
            });
            return b1;
        } catch (Exception e) {
            throw new TestException(e.getMessage());
        }
    }    /*
          * (non-Javadoc)
          * 
          * @see test.common.Test#clean(jade.core.Agent)
          */

    public void clean(Agent a) {
        // Kill the remote container
        if (newPlatform != null) {
            newPlatform.kill();
        }
    }

    private String createMainParams() {
        String services = "jade.core.security.SecurityService;"
        //+ "jade.core.security.permission.PermissionService;"
                + "jade.core.security.signature.SignatureService;"
                + "jade.core.security.encryption.EncryptionService";

        String params = " -conf " + CONFIG_FILE_PATH
                + getTestArgument(CONFIG_FILE);
        params += " -nomtp";
        params += " -port 1088";
        params += " -services " + services;

        return params;
    }
}
