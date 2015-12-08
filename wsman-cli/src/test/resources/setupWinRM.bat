winrm quickconfig -q
winrm set winrm/config/service/auth @{Basic="true"}
winrm set winrm/config/service/auth @{CredSSP="true"}
winrm set winrm/config/client/auth @{CredSSP="true"}
winrm set winrm/config/client @{AllowUnencrypted="true"}
winrm set winrm/config/service @{AllowUnencrypted="true"}
winrm set winrm/config/winrs @{MaxConcurrentUsers="100"}
winrm set winrm/config/winrs @{MaxMemoryPerShellMB="0"}
winrm set winrm/config/winrs @{MaxProcessesPerShell="0"}
winrm set winrm/config/winrs @{MaxShellsPerUser="0"}
netsh advfirewall firewall add rule name=WinRM dir=in protocol=tcp localport=5985 action=allow profile=any