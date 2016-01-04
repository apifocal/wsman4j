# wsman4j
WS-Management java binding

# Running tests

Set up the appropriate WinRM test system by running setupWinRM.bat on it.
Run maven using the appropriate properties to reach the WinRM test system

    mvn -Dwinrm.test.host=test-host \
        -Dwinrm.test.user=TestUser \
        -Dwinrm.test.pass=somePass \
        test

TODO: don't use passwords on command-line etc
