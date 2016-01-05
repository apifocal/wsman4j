call winrm quickconfig -q
call winrm set winrm/config/client @{AllowUnencrypted="true"}
call winrm set winrm/config/client/auth @{CredSSP="true"}
call winrm set winrm/config/service @{AllowUnencrypted="true"}
call winrm set winrm/config/service/auth @{Basic="true"}
call winrm set winrm/config/service/auth @{CredSSP="true"}
call winrm set winrm/config/winrs @{MaxConcurrentUsers="100"}
rem call winrm set winrm/config/winrs @{MaxMemoryPerShellMB="0"}
rem call winrm set winrm/config/winrs @{MaxProcessesPerShell="0"}
rem call winrm set winrm/config/winrs @{MaxShellsPerUser="0"}
call netsh advfirewall firewall add rule name=WinRM dir=in protocol=tcp localport=5985 action=allow profile=any