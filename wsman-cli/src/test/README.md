#Testing prerequisites

WinRM may be used on a windows host only.

Before connecting to a Windows server, you will need to ensure that the server is accessible and has been configured to allow unencrypted WinRM connections over http. 

The following batch script will configure WinRM and open port 5985 (the default WinRM port) on the local firewall.

```
resources\setupWinRM.bat
```

Make sure to customize the user and password used for your account, by editing this file.
```
resources\configTests.properties
```
